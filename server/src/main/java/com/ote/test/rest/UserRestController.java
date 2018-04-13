package com.ote.test.rest;

import com.ote.test.mapper.UserMapperService;
import com.ote.test.persistence.UserEntity;
import com.ote.test.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
public class UserRestController {

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload create(@Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        userEntity = userJpaRepository.save(userEntity);
        return userMapperService.convert(userEntity);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload read(@PathVariable("id") int id) {
        log.info("Find id: " + id);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return userMapperService.convert(userJpaRepository.findOne(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAll() {
        return userMapperService.convert(userJpaRepository.findAll());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        userEntity.setId(id);
        userEntity = userJpaRepository.save(userEntity);
        return userMapperService.convert(userEntity);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        userJpaRepository.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    @PostMapping(value = "init/{max}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public long init(@PathVariable("max") long max) {

        max = Math.max(userJpaRepository.count(), max);

        LongStream.range(userJpaRepository.count(), max).
                parallel().
                forEach(i -> {
                    UUID login = UUID.randomUUID();
                    UserEntity userEntity = new UserEntity();
                    userEntity.setLogin(login.toString());
                    userJpaRepository.save(userEntity);
                });

        return userJpaRepository.count();
    }

    @PostMapping(value = "add/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public long add(@PathVariable("num") long num) {

        LongStream.range(0, num).
                parallel().
                forEach(i -> {
                    UUID login = UUID.randomUUID();
                    UserEntity userEntity = new UserEntity();
                    userEntity.setLogin(login.toString());
                    userJpaRepository.save(userEntity);
                });

        return userJpaRepository.count();
    }


}
