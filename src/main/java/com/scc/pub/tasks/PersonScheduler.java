package com.scc.pub.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.scc.pub.service.PersonService;

@Component
public class PersonScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PersonScheduler.class);

    @Autowired
    PersonService personService;

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    public void synchronizePerson() {

        try {
            personService.syncChanges();
        } finally {
        }
    }

}
