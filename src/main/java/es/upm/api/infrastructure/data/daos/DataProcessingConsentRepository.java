package es.upm.api.infrastructure.data.daos;

import es.upm.api.infrastructure.data.models.DataProcessingConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataProcessingConsentRepository extends JpaRepository<DataProcessingConsent, String> {
}
