package com.scc.pub.config;

import com.scc.pub.model.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Created by AnthonyLenovo on 05/01/2019.
 */
@Configuration
public class PubConfiguration {

    private static final Log LOGGER = LogFactory.getLog(PubConfiguration.class);

    @Value("${spring.cloud.stream.bindings.output.destination}")
    private String TOPIC_NAME;

    @Bean
    public DirectChannel pubSubOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubSubTemplate) {
        PubSubMessageHandler adapter = new PubSubMessageHandler(pubSubTemplate, TOPIC_NAME);
        adapter.setPublishCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.info("There was an error sending the message.");
            }

            @Override
            public void onSuccess(String result) {
                LOGGER.info("Message was sent successfully.");
            }
        });

        return adapter;
    }

    /**
     * an interface that allows sending a message to Pub/Sub.
     */
    @MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
    public interface PubSubGateway {
        void sendToPubSub(Event e);
    }

}
