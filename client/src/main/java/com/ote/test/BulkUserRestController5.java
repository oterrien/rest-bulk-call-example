package com.ote.test;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/users/async5")
@Slf4j
public class BulkUserRestController5 {

    @Autowired
    private RemoteUserService remoteUserService;

    private Map<String, Value> map = new ConcurrentHashMap<>();

    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAsync(@PathVariable("num") int num) throws Exception {
        String key = UUID.randomUUID().toString();
        map.put(key, new Value());

        Params params = new Params();
        IntStream.range(0, num).mapToObj(i -> new Params.Param(i)).forEach(p -> params.getParams().add(p));
        remoteUserService.findMany(params, "/api/v1/users/async5/callback/" + key);
        /*while (!map.get(key).finished) {

        }*/
        Thread.sleep(20);
        return map.get(key).payloads;
    }

    @PostMapping(value = "/callback/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void callBack(@RequestBody UserPayload userPayload, @PathVariable("key") String key) {
        map.get(key).payloads.add(userPayload);
    }


    @Setter
    @Getter
    private class Value {
        private boolean finished = false;
        private final List<UserPayload> payloads = Collections.synchronizedList(new ArrayList<>());
    }

}
