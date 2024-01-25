package monitoring.service.dev.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsDTO {

    private String username;

    private String password;
}
