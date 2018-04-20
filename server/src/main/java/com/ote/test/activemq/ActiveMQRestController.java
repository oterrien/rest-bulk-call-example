package com.ote.test.activemq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activemq")
@Slf4j
public class ActiveMQRestController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping
    public void sendMessage(){
        log.info("#### Sending an email message.");
        jmsTemplate.convertAndSend("mailbox.queue", new Email("info@example.com", "Hello"));
    }
}
