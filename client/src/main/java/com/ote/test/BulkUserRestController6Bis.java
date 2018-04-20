package com.ote.test;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/users/async6b")
@Slf4j
public class BulkUserRestController6Bis {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private ConnectionFactory connectionFactory;

    private Map<String, Value> map = new ConcurrentHashMap<>();

    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAsync(@PathVariable("num") int num) throws Exception {
        String key = UUID.randomUUID().toString();
        try (Listener listener = new Listener(connectionFactory, key, message -> this.onMessage(message, key))) {
            map.put(key, new Value());
            Params params = new Params();
            IntStream.range(0, num).mapToObj(i -> new Params.Param(i)).forEach(p -> params.getParams().add(p));
            listener.start();
            remoteUserService.findMany6b(params, key);
            while (!map.get(key).isFinished()) {
            }
            List<UserPayload> result = map.get(key).payloads;
            log.info("Number of payloads: " + result.size());
            return result;
        } finally {
            map.remove(key);
        }
    }

    private class Listener implements AutoCloseable {

        private DefaultMessageListenerContainer messageListenerContainer;

        public Listener(ConnectionFactory connectionFactory, String queue, MessageListener messageListener) {
            messageListenerContainer = new DefaultMessageListenerContainer();
            messageListenerContainer.setConnectionFactory(connectionFactory);
            messageListenerContainer.setAutoStartup(false);
            messageListenerContainer.setRecoveryInterval(1);
            messageListenerContainer.setExceptionListener(e -> log.error(e.getMessage(), e));
            messageListenerContainer.setDestinationName(queue);
            messageListenerContainer.setConcurrentConsumers(10);
            messageListenerContainer.setMessageListener(messageListener);
            messageListenerContainer.initialize();
        }

        public void start() {
            messageListenerContainer.start();
        }

        @Override
        public void close() {
            messageListenerContainer.stop();
        }
    }

    private void onMessage(Message message, String key) {
        try {
            String body = ((TextMessage) message).getText();
            List<UserPayload> userPayloads = JsonUtils.parseFromJsonList(body, UserPayload.class);
            Value value = map.get(key);
            value.payloads.addAll(userPayloads);
            value.setFinished(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Setter
    @Getter
    private class Value {
        private boolean finished;
        private final List<UserPayload> payloads = new ArrayList<>();
    }


}
