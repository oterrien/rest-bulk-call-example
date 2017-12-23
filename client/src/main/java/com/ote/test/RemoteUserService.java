package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Future;

@Service
@Slf4j
public class RemoteUserService {

    @Autowired
    protected RestTemplate restTemplate;

    @Value("${remote.server.uri}")
    private String serverUri;

    @Async
    public Future<Optional<UserPayload>> findOne(int id) {
        log.info("Find user #" + id);
        ResponseEntity<UserPayload> res = restTemplate.getForEntity(serverUri + "/api/v1/users/" + id, UserPayload.class);
        return new AsyncResult<>(Optional.ofNullable(res).map(p -> p.getBody()));
    }
}
