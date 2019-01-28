package com.scc.pub.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.scc.pub.config.PubConfiguration;
import com.scc.pub.model.Breeder;
import com.scc.pub.model.Event;
import com.scc.pub.model.Owner;
import com.scc.pub.model.SyncData;
import com.scc.pub.repository.BreederRepository;
import com.scc.pub.repository.OdsDataRepository;
import com.scc.pub.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

	@Autowired
	private OdsDataRepository odsDataRepository;

	@Autowired
	private BreederRepository breederRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	protected final PubConfiguration.PubSubGateway pubSubGateway;

	public PersonService(PubConfiguration.PubSubGateway pubSubGateway) {
		super();
		this.pubSubGateway = pubSubGateway;
	}

	private List<SyncData> getAllPersons() {

		try {
			return odsDataRepository.findByTransfertAndDomaine("N", "PERSONNE");
		} finally {
		}

	}

	private SyncData savePerson(SyncData person) {

		try {
			return odsDataRepository.save(person);
		} finally {
		}

	}

	private void deletePerson(SyncData person) {

		try {
			odsDataRepository.delete(person);
		} finally {
		}

	}

	private List<Breeder> getBreederById(int personId) {

		try {
			return breederRepository.findById(personId);
		} finally {
		}

	}

	private List<Owner> getOwnerById(int personId) {

		try {
			return ownerRepository.findById(personId);
		} finally {
		}

	}

	private void sendOwner(Event<Owner> e) {
		this.pubSubGateway.sendToPubSub(e);
	}

	private void publishChangeOwner(String action, Owner owner) {

		Instant instant = Instant.now();
		logger.debug("Sending PubSub Person message {} for {} at {} ", action, owner.toString(), instant);

		try {

			// Propriété type : permet de cible les objets PostgreSQL
			// Propriété action : CRUD à effectuer sur ces mêmes objets
			// Propriété T : data (format json)
			List<Owner> owners = new ArrayList<Owner>();
			owners.add(owner);
			Event<Owner> change = new Event<Owner>(Owner.class.getSimpleName(), action, owners, instant.toEpochMilli());

			sendOwner(change);

		} finally {
		}

	}

	private void sendMessageOwner(List<Owner> owners, String action) {

		try {

			if (owners != null && owners.size() > 0) {
				for (Owner owner : owners) {
					switch (action) {
					case "U":
						publishChangeOwner("UPDATE", owner);
						break;
					case "I":
						publishChangeOwner("SAVE", owner);
						break;
					case "D":
						publishChangeOwner("DELETE", owner);
						break;
					default:
						logger.error("Received an UNKNOWN event type {} for {}", action,
								owner.getClass().getSimpleName());
						break;
					}
				}
			}

		} finally {
		}

	}

	private void publishChangeBreeder(String action, Breeder breeder) {

		Instant instant = Instant.now();
		logger.debug("Sending PubSub Person message {} for {} at {} ", action, breeder.toString(), instant);

		try {

			// Propriété type : permet de cible les objets PostgreSQL
			// Propriété action : CRUD à effectuer sur ces mêmes objets
			// Propriété T : data (format json)
			List<Breeder> breeders = new ArrayList<Breeder>();
			breeders.add(breeder);
			Event<Breeder> change = new Event<Breeder>(Breeder.class.getSimpleName(), action, breeders,
					instant.toEpochMilli());

			sendBreeder(change);

		} finally {
		}

	}

	private void sendBreeder(Event<Breeder> e) {
		this.pubSubGateway.sendToPubSub(e);
	}

	private void sendMessageBreeder(List<Breeder> breeders, String action) {

		try {

			if (breeders != null && breeders.size() > 0) {
				for (Breeder breeder : breeders) {
					switch (action) {
					case "U":
						publishChangeBreeder("UPDATE", breeder);
						break;
					case "I":
						publishChangeBreeder("SAVE", breeder);
						break;
					case "D":
						publishChangeBreeder("DELETE", breeder);
						break;
					default:
						logger.error("Received an UNKNOWN event type {} for {}", action,
								breeder.getClass().getSimpleName());
						break;
					}
				}
			}

		} finally {
		}

	}

	public void syncChanges() {

		try {

			List<SyncData> personList = new ArrayList<SyncData>();
			int idPerson = 0;

			logger.debug("syncChanges {}", "PERSONNE");

			// 0. Lecture dans la table demande de synchro pour l'ensemble des personnes
			// (eleveurs et propriétaires) sur lesquelles 1 maj est demandée
			personList = getAllPersons();
			if (personList.size() > 0) {
				// [[Boucle]] s/ la personne
				for (SyncData syncPers : personList) {

					try {

						// 1. Maj du chien de la table (ODS_SYNC_PERSONNE)
						idPerson = (int) syncPers.getId();
						syncPers.setTransfert("O");
						savePerson(syncPers);

						// 2. Lecture des infos pour l'éleveur/propriétaire à synchroniser

						// PARTIE 1. Info ELEVEUR
						// Note : vue ODS_ELEVEUR (Oracle) == image de la table ODS_ELEVEUR (PostGRE)
						// Si UPDATE/INSERT et breeder == null alors l'éleveur n'est pas dans le
						// périmètre -> on le supprime de la liste
						// + DELETE, breeder == null -> on publie uniquement l'id à supprimer
						List<Breeder> breeders = new ArrayList<Breeder>();
						if (!syncPers.getAction().equals("D"))
							breeders = getBreederById(idPerson);
						else
							breeders.add(new Breeder().withId(idPerson));

						// Envoi du message à ods-service pour maj Postgre
						if (breeders != null && breeders.size() > 0)
							sendMessageBreeder(breeders, syncPers.getAction());

						// PARTIE 2. Info PROPRIETAIRE
						// Note : vue ODS_PROPRIETAIRE (Oracle) == image de la table ODS_PROPRIETAIRE
						// (PostGRE)
						// Si UPDATE/INSERT et owner == null alors le propriétaire n'est pas dans le
						// périmètre -> on le supprime de la liste
						// + DELETE, owner == null -> on publie uniquement l'id à supprimer
						List<Owner> owners = new ArrayList<Owner>();
						if (!syncPers.getAction().equals("D"))
							owners = getOwnerById(idPerson);
						else
							owners.add(new Owner().withId(idPerson));

						// Envoi du message à ods-service pour maj Postgre
						if (owners != null && owners.size() > 0)
							sendMessageOwner(owners, syncPers.getAction());

						if ((owners == null || owners.size() == 0) && (breeders == null || breeders.size() == 0))
							deletePerson(syncPers);

					} catch (Exception e) {
						logger.error(" idPerson {} : {}", idPerson, e.getMessage());
					} finally {
					}
				}
			}

		} finally {
		}
	}

}
