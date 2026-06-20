package es.upm.api.infrastructure.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "miwUser")
public class User {
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String mobile;
    private String firstName;
    private String familyName;
    private String email;
    private String identity;
    private String address;
    private String city;
    @Enumerated(EnumType.STRING)
    private Province province;
    private Integer postalCode;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate registrationDate;
    private Boolean active;
}
