package es.upm.api.services;

import es.upm.api.infrastructure.data.daos.UserRepository;
import es.upm.api.infrastructure.data.models.User;
import es.upm.api.services.criteria.UserFindCriteria;
import es.upm.api.services.exceptions.ClientBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    public void create(User user) {
        this.assertNoExistByMobile(user.getMobile());
        user.setId(UUID.randomUUID());
        if (Objects.isNull(user.getPassword())) {
            user.setPassword(UUID.randomUUID().toString());
        }
        user.setRegistrationDate(LocalDate.now());
        this.userRepository.save(user);
    }

    private void assertNoExistByMobile(String mobile) {
        if (this.userRepository.existsByMobile(mobile)) {
            throw new ClientBusinessException("El móvil ya existe: " + mobile);
        }
    }

    public Stream<User> find(UserFindCriteria criteria) {
        if (criteria.all()) {
            return this.userRepository.findAll().stream();
        }
        if (criteria.hasMobile() && criteria.getActive() != null) {
            return this.userRepository.findByMobileAndActive(criteria.getMobile(), criteria.getActive()).stream();
        }
        if (criteria.hasMobile()) {
            return this.userRepository.findByMobile(criteria.getMobile()).stream();
        }
        return this.userRepository.findByActive(criteria.getActive()).stream();
    }

}
