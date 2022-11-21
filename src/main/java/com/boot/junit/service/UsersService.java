package com.boot.junit.service;

import com.boot.junit.shared.UserDto;

import java.util.List;

public interface UsersService {
    UserDto createUser(UserDto user);
    List<UserDto> getUsers(int page, int limit);
    UserDto getUser(String email);
    UserDto getUserByLastName(String lastName);
}