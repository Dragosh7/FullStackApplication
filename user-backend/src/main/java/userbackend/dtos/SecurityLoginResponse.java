package userbackend.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class SecurityLoginResponse {
    private String message;
    private UUID id;
    private String name;
    private String role;
    private String token;

    public SecurityLoginResponse(String message) {
        this.message = message;
    }

}
