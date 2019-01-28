package com.scc.pub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scc.pub.config.PubConfiguration.PubSubGateway;
import com.scc.pub.model.Pedigree;
import com.scc.pub.repository.PedigreeRepository;
import com.scc.pub.utils.Constants;

@Service
public class PedigreeService extends AbstractGenericService<Pedigree> {

	private static final Logger logger = LoggerFactory.getLogger(PedigreeService.class);

	@Autowired
	private PedigreeRepository pedigreeRepository;

	public PedigreeService(PubSubGateway pubSubGateway) {
		super(pubSubGateway, Constants.pedigreeDomaine);
	}

	public Pedigree getPedigreeById(long id) {

		try {
			return pedigreeRepository.findById(id);
		} finally {
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T populateMessage(int _id, String _action) {

		Pedigree pedigree = new Pedigree();
		if (!_action.equals("D")) {
			pedigree = getPedigreeById(_id);
			if (pedigree == null) {
				return null;
			}
		} else
			pedigree.withId(_id);

		return (T) pedigree;
	}
}
