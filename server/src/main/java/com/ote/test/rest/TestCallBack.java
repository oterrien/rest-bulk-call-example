package com.ote.test.rest;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/test")
@Slf4j
public class TestCallBack {

    private Map<String, Value> map = new ConcurrentHashMap<>();

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String testPost(@RequestBody Params params) {

        String key = UUID.randomUUID().toString();
        map.put(key, new Value());
        CompletableFuture.
                runAsync(() -> process(params, key)).
                thenAccept(p -> map.get(key).finished = true);
        return key;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> get(@RequestParam("key") String key) {
        try {
            while (!map.get(key).finished) {
                sleep();
            }
            return map.get(key).id;
        } finally {
            map.remove(key);
        }
    }

    private void process(Params params, String key) {
        params.getParams().
                stream().
                peek(p -> sleep()).
                peek(p -> map.get(key).id.add(p.id)).
                forEach(p -> log.info(p + ""));
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
    }

    @Setter
    @Getter
    private class Value {
        private boolean finished = false;
        private final List<Integer> id = new ArrayList<>();
    }


    @ToString
    @Getter
    public static class Params {
        private final List<Param> params = new ArrayList<>();
    }

    @ToString
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Param {
        private int id;
    }
}
