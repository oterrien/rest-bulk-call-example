package com.ote.test.rest;

import com.ote.test.JsonUtils;
import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAsyncRestController6 {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping(value = "/post6", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void postRequest(@RequestBody Params params,
                            @RequestParam("queue") String queue) {
        CompletableFuture.runAsync(() -> process(params, queue));
    }

    private void process(Params params, String queue) {
        AtomicInteger count = new AtomicInteger(0);
        log.info(queue);
        try {
            params.getParams().
                    parallelStream().
                    map(p -> userJpaRepository.findOne(p.getId())).
                    map(p -> userMapperService.convert(p)).
                    filter(Objects::nonNull).
                    peek(p -> count.getAndIncrement()).
                    forEach(p -> jmsTemplate.send(queue, session -> this.sendPayload(session, p)));
        } finally {
            jmsTemplate.send(queue, session -> this.sendFinish(session, count.get()));
        }
    }

    private Message sendPayload(Session session, UserPayload payload) {
        try {
            TextMessage textMessage = session.createTextMessage(JsonUtils.serialize(payload));
            textMessage.setBooleanProperty("isFinished", false);
            return textMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Message sendFinish(Session session, int count) {
        try {
            log.info("Number of sent payloads: " + count);
            TextMessage textMessage = session.createTextMessage(String.valueOf(count));
            textMessage.setBooleanProperty("isFinished", true);
            return textMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
