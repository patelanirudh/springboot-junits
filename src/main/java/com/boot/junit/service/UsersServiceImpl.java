package com.boot.junit.service;

import com.boot.junit.exceptions.UsersServiceException;
import com.boot.junit.repo.UserEntity;
import com.boot.junit.repo.UsersRepository;
import com.boot.junit.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("usersService")
public class UsersServiceImpl implements UsersService {
    private UsersRepository usersRepository;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (usersRepository.findByEmail(user.getEmail()) != null)
            throw new UsersServiceException("Record already exists");

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = UUID.randomUUID().toString();
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {

        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) page -= 1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = usersRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        returnValue = new ModelMapper().map(users, listType);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsersServiceException("User not found for email " + email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUserByLastName(String lastName) {
        UserEntity userEntity = usersRepository.findByLastName(lastName);

        if (userEntity == null)
            throw new UsersServiceException("User not found for lastName " + lastName);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }
}
