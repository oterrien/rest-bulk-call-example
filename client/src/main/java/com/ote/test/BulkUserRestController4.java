package com.ote.test;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/users/async4")
@Slf4j
public class BulkUserRestController4 {

    @Autowired
    private RemoteUserService remoteUserService;

    @RequestMapping(value = "/{num}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Integer> readAsync(@PathVariable("num") int num) {

        Params params = new Params();
        IntStream.range(0, num).mapToObj(i -> new Param(i)).forEach(p -> params.params.add(p));

        String requestId = remoteUserService.post(params);

        return remoteUserService.get(requestId);
    }

    @Getter
    public static class Params {
        private final List<Param> params = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Param {
        private int id;
    }



}
