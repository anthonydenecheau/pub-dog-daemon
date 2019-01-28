package com.scc.pub.service;

import com.scc.pub.config.PubConfiguration;
import com.scc.pub.model.Owner;
import com.scc.pub.repository.OwnerRepository;
import com.scc.pub.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerService extends AbstractGenericService<Owner> {

	private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

	@Autowired
	private OwnerRepository ownerRepository;

	public OwnerService(PubConfiguration.PubSubGateway pubSubGateway) {
		super(pubSubGateway, Constants.ownerDomaine);
	}

	public Owner getOwnerByIdDog(int dogId) {

		try {
			return ownerRepository.findByIdDog(dogId);
		} finally {
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T populateMessage(int _id, String _action) {

		Owner dog = new Owner();
		if (!_action.equals("D")) {
			dog = getOwnerByIdDog(_id);
			if (dog == null) {
				return null;
			}
		} else
			dog.withId(_id);

		return (T) dog;
	}
}
