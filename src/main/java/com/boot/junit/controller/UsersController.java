package com.boot.junit.controller;

import com.boot.junit.model.UserDetailsRequestModel;
import com.boot.junit.model.UserRest;
import com.boot.junit.service.UsersService;
import com.boot.junit.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public UserRest createUser(@RequestBody @Valid UserDetailsRequestModel userDetails) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);

        UserDto createdUser = usersService.createUser(userDto);

        return modelMapper.map(createdUser, UserRest.class);
    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit) {
        List<UserDto> users = usersService.getUsers(page, limit);

        Type listType = new TypeToken<List<UserRest>>() {
        }.getType();

        return new ModelMapper().map(users, listType);
    }

    @GetMapping(path = "/email")
    public UserRest getUser(@RequestParam(value = "emailName", required = true) String emailName) {
        System.out.println("EmailName : " + emailName);
        UserDto returnedUser = usersService.getUser(emailName);

        return new ModelMapper().map(returnedUser, UserRest.class);
    }

    @GetMapping(path = "/names/{lastName}")
    public UserRest getUserByEmail(@PathVariable(value = "lastName", required = true) String lastName) {
        System.out.println("lastName : " + lastName);
        UserDto returnedUser = usersService.getUserByLastName(lastName);

        return new ModelMapper().map(returnedUser, UserRest.class);
    }
}
