package com.scc.pub.service;

import com.scc.pub.model.Parent;
import com.scc.pub.utils.Constants;
import com.scc.pub.repository.ParentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.scc.pub.config.PubConfiguration.PubSubGateway;
import org.springframework.stereotype.Service;

/**
 * Created by AnthonyLenovo on 05/01/2019.
 */
@Service
public class ParentService extends AbstractGenericService<Parent> {

	private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

	@Autowired
	private ParentRepository parentRepository;

	public ParentService(PubSubGateway pubSubGateway) {
		super(pubSubGateway, Constants.parentDomaine);
	}

	public Parent getParentByIdDog(int dogId) {

		try {
			return parentRepository.findById(dogId);
		} finally {
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T populateMessage(int _id, String _action) {

		Parent parent = new Parent();
		if (!_action.equals("D")) {
			parent = getParentByIdDog(_id);
			if (parent == null) {
				return null;
			}
		} else
			parent.withId(_id);

		return (T) parent;
	}

}
