package es.upm.api.infrastructure.data.daos;

import es.upm.api.infrastructure.data.models.DataProcessingConsent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DataProcessingConsentRepository extends JpaRepository<DataProcessingConsent, UUID> {
}
