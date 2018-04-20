package com.ote.test.rest;

import com.ote.test.JsonUtils;
import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAsyncRestController6Bis {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostMapping(value = "/post6b", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void postRequest(@RequestBody Params params,
                            @RequestParam("queue") String queue) {
        CompletableFuture.runAsync(() -> process(params, queue));
    }

    private void process(Params params, String queue) {

            List<UserPayload> payloadList = params.getParams().
                    parallelStream().
                    map(p -> userJpaRepository.findOne(p.getId())).
                    map(p -> userMapperService.convert(p)).
                    filter(Objects::nonNull).
                    collect(Collectors.toList());

        jmsTemplate.send(queue, session -> this.sendPayload(session, payloadList));
    }

    private Message sendPayload(Session session, List<UserPayload> payloads) {
        try {
            TextMessage textMessage = session.createTextMessage(JsonUtils.serialize(payloads));
            textMessage.setBooleanProperty("isFinished", false);
            return textMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
