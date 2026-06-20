package es.upm.api.functionaltests;

import es.upm.api.configurations.SeederForDev;
import es.upm.api.resources.UserResource;
import es.upm.api.resources.dtos.UserDto;
import es.upm.api.services.exceptions.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserResourceFT {

    @LocalServerPort
    private int port;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + this.port)
                .build();
    }

    @Test
    void testFindAll() {
        this.restTestClient.get()
                .uri(UserResource.USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .extracting(UserDto::getMobile)
                        .contains(
                                SeederForDev.C_0.getMobile(),
                                SeederForDev.ADMIN.getMobile(),
                                SeederForDev.MANAGER.getMobile(),
                                SeederForDev.OPERATOR.getMobile()
                        ));
    }

    @Test
    void testFindByMobile() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.MANAGER.getMobile())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile, UserDto::getFirstName)
                        .containsExactly(SeederForDev.MANAGER.getMobile(), SeederForDev.MANAGER.getFirstName()));
    }

    @Test
    void testCreate() {
        String mobile = "699999997";
        String firstName = "ResourceUser";
        UserDto userDto = UserDto.builder()
                .mobile(mobile)
                .firstName(firstName)
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", mobile)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile, UserDto::getFirstName)
                        .containsExactly(mobile, firstName));
    }

    @Test
    void testCreateWithExistingMobile() {
        UserDto userDto = UserDto.builder()
                .mobile(SeederForDev.MANAGER.getMobile())
                .firstName("DuplicatedMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .value(error -> assertThat(error.getMessage()).contains(SeederForDev.MANAGER.getMobile()));
    }
}
