package com.ote.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserPayload {

    @JsonProperty
    private int id;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String login;

}