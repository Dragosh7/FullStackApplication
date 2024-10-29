package userbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDeviceDTO {
    private UUID id;
    private String name;

    @Override
    public String toString() {
        return "PersonDeviceDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
