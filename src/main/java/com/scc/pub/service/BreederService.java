package com.scc.pub.service;

import com.scc.pub.config.PubConfiguration.PubSubGateway;
import com.scc.pub.model.Breeder;
import com.scc.pub.repository.BreederRepository;
import com.scc.pub.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by AnthonyLenovo on 06/01/2019.
 */
@Service
public class BreederService extends AbstractGenericService<Breeder> {

    private static final Logger logger = LoggerFactory.getLogger(BreederService.class);

    @Autowired
    private BreederRepository breederRepository;

    public BreederService(PubSubGateway pubSubGateway) {
        super(pubSubGateway, Constants.breederDomaine);
    }

    public Breeder getBreederByIdDog(int dogId) {

        try {
            return breederRepository.findByIdDog(dogId);
        } finally {
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T populateMessage(int _id, String _action) {

        Breeder breeder = new Breeder();
        if (!_action.equals("D")) {
            breeder = getBreederByIdDog(_id);
            if (breeder == null) {
                return null;
            }
        } else
            breeder.withIdDog(_id);

        return (T) breeder;
    }

}


