package es.upm.api.resources;

import es.upm.api.resources.dtos.UserDto;
import es.upm.api.services.UserService;
import es.upm.api.services.criteria.UserFindCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UserResource.USERS)
@RequiredArgsConstructor
@Log4j2
public class UserResource {
    public static final String USERS = "/users";

    private final UserService userService;

    @PostMapping
    public void create(@Valid @RequestBody UserDto userDto) {
        userDto.doDefault();
        this.userService.create(userDto.toDomain());
    }

    @GetMapping
    public List<UserDto> find(@ModelAttribute UserFindCriteria criteria) {
        return this.userService.find(criteria)
                .map(UserDto::new)
                .map(UserDto::toSummary)
                .toList();
    }


}
