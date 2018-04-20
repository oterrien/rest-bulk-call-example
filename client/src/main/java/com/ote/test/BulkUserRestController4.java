package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v1/users/async4")
@Slf4j
public class BulkUserRestController4 {

    @Autowired
    private RemoteUserService remoteUserService;

    @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAsync(@PathVariable("num") int num) {

        Params params = new Params();
        IntStream.range(0, num).mapToObj(i -> new Params.Param(i)).forEach(p -> params.getParams().add(p));
        return remoteUserService.findMany4(params);
    }


}
