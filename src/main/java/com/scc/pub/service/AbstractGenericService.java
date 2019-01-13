package com.scc.pub.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.scc.pub.model.SyncData;
import com.scc.pub.repository.OdsDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.scc.pub.config.PubConfiguration.PubSubGateway;
import com.scc.pub.model.Event;

public abstract class AbstractGenericService<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGenericService.class);

    protected final String domaine;
    protected final PubSubGateway pubSubGateway;

    @Autowired
    private OdsDataRepository odsDataRepository;

    public AbstractGenericService(PubSubGateway pubSubGateway, String domaine) {
        super();
        this.pubSubGateway = pubSubGateway;
        this.domaine = domaine;
    }

    protected void send(Event<T> e) {
        this.pubSubGateway.sendToPubSub(e);
    }

    protected SyncData saveDog(SyncData dog) {
        try {
            return odsDataRepository.save(dog);
        } finally {
        }
    }

    protected void deleteDog(SyncData dog) {
        try {
            odsDataRepository.delete(dog);
        } finally {
        }
    }

    protected List<SyncData> getAllChanges(String _domaine) {
        try {
            return odsDataRepository.findByTransfertAndDomaine("N", _domaine);
        } finally {
        }
    }

    protected void sendMessage(T message, String action) {

        try {

            if (message != null) {

                switch (action) {
                    case "U":
                        publishChange("UPDATE", message);
                        break;
                    case "I":
                        publishChange("SAVE", message);
                        break;
                    case "D":
                        publishChange("DELETE", message);
                        break;
                    default:
                        logger.error("Received an UNKNOWN event type {} for {}", action, message.getClass().getSimpleName());
                        break;
                }
            }
        } finally {
        }

    }

    protected void publishChange(String action, T message) {

        Instant instant = Instant.now();
        logger.debug("Sending PubSub message {} for {} at {} ", action, message.toString(), instant);

        try {

            // Propriété type : permet de cible les objets PostgreSQL
            // Propriété action : CRUD à effectuer sur ces mêmes objets
            // Propriété T : data (format json)
            List<T> messages = new ArrayList<T>();
            messages.add(message);
            Event<T> change = new Event<T>(
                    message.getClass().getSimpleName(),
                    action,
                    messages,
                    instant.toEpochMilli());

            send(change);

        } finally {
        }

    }

    public void syncChanges() {

        List<SyncData> dogList = new ArrayList<SyncData>();
        int idDog = 0;

        try {

            logger.debug("syncChanges {}", this.domaine);

            dogList = getAllChanges(this.domaine);
            if (dogList.size() > 0) {

                logger.debug("syncChanges :: dogList {}", dogList.size());

                // [[Boucle]] s/ le chien
                for (SyncData syncDog : dogList) {

                    // 1. Maj du chien, titre etc. de la table (ODS_SYNC_DATA)
                    idDog = (int) syncDog.getId();
                    syncDog.setTransfert("O");
                    saveDog(syncDog);

                    // 2. Lecture des infos pour le chien à synchroniser
                    // Note : vue ODS_ELEVEUR (Oracle) == image de la table ODS_ELEVEUR (PostGRE)
                    // Si UPDATE/INSERT et dog == null alors le chien n'est pas dans le périmètre -> on le supprime de la liste
                    // + DELETE, dog == null -> on publie uniquement l'id à supprimer
                    T message = populateMessage(idDog, syncDog.getAction());
                    if (message == null) {
                        deleteDog(syncDog);
                        continue;
                    }

                    // 3. Envoi du message pour maj Postgre
                    sendMessage(message, syncDog.getAction());

                }
                // [[Boucle]]
            }
        } finally {
            dogList.clear();
        }
    }

    @SuppressWarnings("hiding")
    protected abstract <T> T populateMessage(int _id, String _action);

}
