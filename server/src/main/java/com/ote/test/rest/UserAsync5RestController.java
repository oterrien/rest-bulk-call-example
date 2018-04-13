package com.ote.test.rest;

import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAsync5RestController {

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
        log.info(callback);
        params.getParams().
                parallelStream().
                map(p -> userJpaRepository.findOne(p.getId())).
                map(p -> userMapperService.convert(p)).
                peek(p -> log.info(p.toString())).
                forEach(p -> restTemplate.postForEntity(callback, p, UserPayload.class));
    }
}
