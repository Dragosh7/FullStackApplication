package com.example.devicebackend.dtos;

import com.example.devicebackend.entities.ActionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class PersonChangeDTO implements Serializable {
    private PersonMonitorDTO person;
    private ActionType action;
}

