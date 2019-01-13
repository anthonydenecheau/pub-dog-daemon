package com.scc.pub.service;

import com.scc.pub.model.Title;
import com.scc.pub.repository.TitleRepository;
import com.scc.pub.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.scc.pub.config.PubConfiguration.PubSubGateway;
import org.springframework.stereotype.Service;

/**
 * Created by AnthonyLenovo on 05/01/2019.
 */
@Service
public class TitleFrService extends AbstractGenericService<Title> {

    private static final Logger logger = LoggerFactory.getLogger(TitleFrService.class);

    @Autowired
    private TitleRepository titleRepository;

    public TitleFrService(PubSubGateway pubSubGateway) {
        super(pubSubGateway, Constants.titleDomaineFr);
    }

    public Title getTitleById(long id) {

        try {
            return titleRepository.findById(id);
        } finally {
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T populateMessage(int _id, String _action) {

        Title title = new Title();
        if (!_action.equals("D")) {
            title = getTitleById(_id);
            if (title == null) {
                return null;
            }
        } else
            title.withId(_id);

        return (T) title;
    }

}
