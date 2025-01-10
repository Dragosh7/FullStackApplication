package com.example.chatmicroservice.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonMonitorDTO {

    private UUID id;
    private String name;
}
