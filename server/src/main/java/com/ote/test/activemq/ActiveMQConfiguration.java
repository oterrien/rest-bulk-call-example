package com.ote.test.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class ActiveMQConfiguration {

    @Value("${server.activemq.port}")
    private int port;

    @Bean
    public JmsListenerContainerFactory listenerContainerFactory(ConnectionFactory connectionFactory,
                                                                DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    private String getBrokerUrl() {
        return "tcp://localhost:" + port;
    }

    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector(getBrokerUrl());
        broker.setPersistent(false);
        broker.start();
        return broker;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(getBrokerUrl());
        return connectionFactory;
    }
}

/**
 * @Configuration public class ActiveMQConfiguration {
 * <p>
 * String BROKER_URL = "tcp://localhost:61616";
 * String BROKER_USERNAME = "admin";
 * String BROKER_PASSWORD = "admin";
 * @Bean public ActiveMQConnectionFactory connectionFactory(){
 * ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
 * connectionFactory.setBrokerURL(BROKER_URL);
 * connectionFactory.setPassword(BROKER_USERNAME);
 * connectionFactory.setUserName(BROKER_PASSWORD);
 * return connectionFactory;
 * }
 * @Bean public JmsTemplate jmsTemplate(){
 * JmsTemplate template = new JmsTemplate();
 * template.setConnectionFactory(connectionFactory());
 * return template;
 * }
 * @Bean public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
 * DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
 * factory.setConnectionFactory(connectionFactory());
 * factory.setConcurrency("1-1");
 * return factory;
 * }
 * <p>
 * }
 */