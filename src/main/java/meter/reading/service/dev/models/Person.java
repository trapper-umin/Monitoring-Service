package meter.reading.service.dev.models;

import lombok.*;
import meter.reading.service.dev.common.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private int id;

    private String username;

    private String password;

    private Role role;
}
