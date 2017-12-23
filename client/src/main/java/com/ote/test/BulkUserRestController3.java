package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/users/async3")
@Slf4j
public class BulkUserRestController3 {

    @Autowired
    private RemoteUserService remoteUserService;

    @RequestMapping(value = "/{num}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAsync(@PathVariable("num") int num) {

        List<UserPayload> payloads = IntStream.range(0, num).
                parallel().
                mapToObj(i -> remoteUserService.findOne(i)).
                map(p -> get(p)).
                filter(opt -> opt.isPresent()).
                map(payload -> payload.get()).
                collect(Collectors.toList());

        log.info("List #" + payloads.size());

        return payloads;
    }

    private Optional<UserPayload> get(Future<Optional<UserPayload>> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }


}
