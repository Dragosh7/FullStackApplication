package userbackend.dtos;

import java.util.UUID;

public class LoginResponse {
    private String message;
    private UUID id;
    private String name;
    private String role;

    public LoginResponse(String message, UUID id, String name, String role) {
        this.message = message;
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LoginResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
