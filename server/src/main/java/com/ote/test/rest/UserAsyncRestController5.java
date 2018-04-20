package com.ote.test.rest;

import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAsyncRestController5 {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/post5", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void postRequest(@RequestBody Params params,
                            @RequestParam("callback") String callback) {
        CompletableFuture.runAsync(() -> process(params, callback));
    }

    private void process(Params params, String callback) {
        AtomicInteger count = new AtomicInteger(0);
        log.info(callback);
        try {
            params.getParams().
                    parallelStream().
                    map(p -> userJpaRepository.findOne(p.getId())).
                    map(p -> userMapperService.convert(p)).
                    filter(Objects::nonNull).
                    peek(p -> count.getAndIncrement()).
                    forEach(p -> restTemplate.postForEntity(callback, p, UserPayload.class));
        } finally {
            log.info("Number of sent payloads: " + count.get());
            restTemplate.put(callback, count.get());
        }
    }
}
