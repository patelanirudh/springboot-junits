package com.boot.junit.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

// Test only data layer and spring web app.context will load only beans related to data persistence layer
// TestCases will be Transactional and later will be rollback after completion
@DataJpaTest
public class UserEntityIntegrationTest {

    // Alternate object to EntityManager. Allow to persist info and synchronize with DB Table
    @Autowired
    private TestEntityManager testEntityManager;

    private UserEntity userEntity;
    private String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setFirstName("Anirudh");
        userEntity.setLastName("Patel");
        userEntity.setEmail("patel.anirudh@gmail.com");
    }

    @DisplayName("UserEntity Created")
    @Test
    void testUserEntity_whenValidUserDetailsGiven_shouldReturnStoredUserDetails() {
        // Arrange

        // Act
        UserEntity storedUserEntity = testEntityManager.persistAndFlush(userEntity);

        // Assert
        Assertions.assertTrue(storedUserEntity.getId() > 0,"DB Id value should be generated");
        Assertions.assertEquals( userEntity.getFirstName(), storedUserEntity.getFirstName(), "FirstName of storedUserEntity should match");
        Assertions.assertEquals( userEntity.getUserId(), storedUserEntity.getUserId(), "UserId of storedUserEntity should match");
    }

    @DisplayName("UserEntity Creation Fails - Invalid FirstName")
    @Test
    void testUserEntity_whenInValidFirstNameGiven_shouldThrowException() {
        // Arrange
        userEntity.setFirstName("AnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudhAnirudh");

        // Act & Assert
        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Was expecting PersistenceException to be thrown");
    }

    @DisplayName("UserEntity Creation Fails - Non Unique UserId")
    @Test
    void testUserEntity_whenInValidUserIdGiven_shouldThrowException() {
        // Arrange
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUserId(userId);
        userEntity2.setFirstName("Shilpi");
        userEntity2.setLastName("Patel");
        userEntity2.setEmail("shilpi@pagli.com");

        // Act & Assert
        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
            testEntityManager.persistAndFlush(userEntity2);
        }, "Was expecting PersistenceException to be thrown");
    }
}
