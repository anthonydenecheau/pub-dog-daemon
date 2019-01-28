package com.scc.pub.tasks;

import com.scc.pub.service.BreederService;
import org.springframework.beans.factory.annotation.Autowired;
import com.scc.pub.service.DogService;
import com.scc.pub.service.OwnerService;
import com.scc.pub.service.ParentService;
import com.scc.pub.service.PedigreeService;
import com.scc.pub.service.TitleEtrService;
import com.scc.pub.service.TitleFrService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by AnthonyLenovo on 05/01/2019.
 */
@Component
public class DogScheduler {

	private static final Logger logger = LoggerFactory.getLogger(DogScheduler.class);

	@Autowired
	ParentService parentService;

	@Autowired
	PedigreeService pedigreeService;

	@Autowired
	TitleEtrService titleFrService;

	@Autowired
	TitleFrService titleEtrService;

	@Autowired
	OwnerService ownerService;

	@Autowired
	BreederService breederService;

	@Autowired
	DogService dogService;

	@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
	public void synchronizeDog() {

		try {

			// I. Lecture dans la table demande de synchro pour l'ensemble des chiens sur
			// lesquels 1 maj est demandée
			dogService.syncChanges();

			// II. Lecture dans la table demande de synchro pour l'ensemble des éleveurs sur
			// lesquels 1 maj est demandée
			breederService.syncChanges();

			// III. Lecture dans la table demande de synchro pour l'ensemble des
			// propriétaires sur lesquels 1 maj est demandée
			ownerService.syncChanges();

			// IV. Lecture dans la table demande de synchro pour l'ensemble des titres
			// (francais et étrangers) sur lesquels 1 maj est demandée
			titleFrService.syncChanges();
			titleEtrService.syncChanges();

			// V. Lecture dans la table demande de synchro pour l'ensemble des livres sur
			// lesquels 1 maj est demandée
			pedigreeService.syncChanges();

			// VI. Lecture dans la table demande de synchro pour l'ensemble des géniteurs
			// sur lesquels 1 maj est demandée
			parentService.syncChanges();

		} catch (Exception e) {
			logger.error(" synchronizeDog : {}", e.getMessage());
		} finally {
		}
	}
}
