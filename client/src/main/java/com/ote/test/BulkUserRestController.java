package com.ote.test;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class BulkUserRestController {

    private List<Integer> parameters = new ArrayList<>();

    protected RestTemplate restTemplate = new RestTemplate();

    @Value("${remote.server.uri}")
    private String serverUri;

    public BulkUserRestController() {
        for (int i = 0; i < 1000; i += 10) {
            this.parameters.add(i);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> read() throws InterruptedException {

        List<UserPayload> result = Collections.synchronizedList(new ArrayList<>());

        List<Job> jobs = parameters.parallelStream().map(id -> new Job(id)).collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.invokeAll(jobs).parallelStream().map(job -> job.get());


        return result;
    }

    @RequiredArgsConstructor
    private class Job implements Callable<Optional<UserPayload>> {

        private final int id;

        public Optional<UserPayload> call() throws Exception {
            ResponseEntity<UserPayload> res = restTemplate.getForEntity(serverUri + "/api/v1/users/" + id, UserPayload.class);
            return Optional.ofNullable(res.getBody());
        }
    }
}
