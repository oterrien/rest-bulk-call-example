package com.ote.test.rest;

import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserJpaRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserAsyncRestController4 {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserMapperService userMapperService;

    private Map<String, Value> map = new ConcurrentHashMap<>();

    @PostMapping(value = "/post4", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String postRequest(@RequestBody Params params) {

        String key = UUID.randomUUID().toString();
        map.put(key, new Value());
        CompletableFuture.
                runAsync(() -> process(params, key)).
                thenAccept(p -> map.get(key).finished = true);
        return key;
    }

    @GetMapping("/get4/{key}")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPayload> getRequest(@PathVariable("key") String key) {
        try {
            while (!map.get(key).finished) {

            }
            return map.get(key).payloads;
        } finally {
            map.remove(key);
        }
    }

    private void process(Params params, String key) {
        List<UserPayload> payloads = map.get(key).payloads;
        params.getParams().
                parallelStream().
                map(p -> userJpaRepository.findOne(p.getId())).
                map(p -> userMapperService.convert(p)).
                forEach(p -> payloads.add(p));
    }

    @Setter
    @Getter
    private class Value {
        private boolean finished = false;
        private final List<UserPayload> payloads = Collections.synchronizedList(new ArrayList<>());
    }
}
