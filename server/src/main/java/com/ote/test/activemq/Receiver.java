package com.ote.test.activemq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

//@Service
@Slf4j
public class Receiver {

    @JmsListener(destination = "mailbox.queue")
    public void receiveMessage(Email email) {
        log.info("Received <" + email + ">");
    }

}