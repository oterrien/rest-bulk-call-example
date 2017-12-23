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

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload create(@Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        userEntity = userJpaRepository.save(userEntity);
        return userMapperService.convert(userEntity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> readAll() {
        return userMapperService.convert(userJpaRepository.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        userEntity.setId(id);
        userEntity = userJpaRepository.save(userEntity);
        return userMapperService.convert(userEntity);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        userJpaRepository.delete(id);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    @RequestMapping(value = "init/{max}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "add/{num}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
