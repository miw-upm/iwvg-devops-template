package es.upm.api.infrastructure.data.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DataProcessingConsent {
    @Id
    private UUID id;
    private LocalDateTime signatureAt;
    @OneToOne
    private User signer;
    private String signerFullName;
    private String signerIdentity;
    private String mobile;
    private String policyVersion;
    private String signerEmail;
    private Boolean dataProcessingAccepted;
    private Boolean promotionsAccepted;
}
