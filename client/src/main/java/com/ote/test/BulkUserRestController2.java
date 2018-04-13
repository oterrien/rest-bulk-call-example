package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/users/async2")
@Slf4j
public class BulkUserRestController2 {

    @Autowired
    protected RestTemplate restTemplate;

    @Value("${remote.server.uri}")
    private String serverUri;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAsync(@PathVariable("num") int num) {

        List<UserPayload> payloads = IntStream.range(0, num).
                parallel().
                mapToObj(i -> executorService.submit(this.execute(i))).
                map(f -> get(f)).
                filter(opt -> opt.isPresent()).
                map(payload -> payload.get()).
                collect(Collectors.toList());

        log.info("List #" + payloads.size());

        return payloads;
    }

    private Callable<Optional<UserPayload>> execute(int id) {

        return () -> {
            ResponseEntity<UserPayload> res = restTemplate.getForEntity(serverUri + "/api/v1/users/" + id, UserPayload.class);
            return Optional.ofNullable(res).map(p -> p.getBody());
        };
    }

    private Optional<UserPayload> get(Future<Optional<UserPayload>> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return Optional.empty();
        }
    }
}
