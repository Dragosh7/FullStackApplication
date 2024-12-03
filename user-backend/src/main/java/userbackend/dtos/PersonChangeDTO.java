package userbackend.dtos;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonChangeDTO {
    private PersonMonitorDTO person;
    private String action;
}
