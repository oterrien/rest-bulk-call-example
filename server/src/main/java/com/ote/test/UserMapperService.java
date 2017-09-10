package com.ote.test;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserMapperService {

    public List<UserPayload> convert(List<UserEntity> entity) {

        return entity.stream().
                map(this::convert).
                collect(Collectors.toList());
    }

    public UserPayload convert(Optional<UserEntity> entity) {

        return entity.
                map(this::convert).
                orElseThrow(NotFoundException::new);
    }

    public UserPayload convert(UserEntity entity) {

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setLogin(entity.getLogin());
        return payload;
    }

    public UserEntity convert(UserPayload payload) {

        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setLogin(payload.getLogin());
        return entity;
    }


    public class NotFoundException extends RuntimeException {

    }
}