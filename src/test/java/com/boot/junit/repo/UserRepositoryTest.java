package com.boot.junit.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

// Test only data layer and spring web app.context will load only beans related to data persistence layer
// TestCases will be Transactional and later will be rollback after completion
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UsersRepository userRepo;

    private UserEntity userEntity1, userEntity2;

    @BeforeEach
    void setup() {
        userEntity1 = new UserEntity();
        userEntity1.setUserId("1");
        userEntity1.setFirstName("Shilpi");
        userEntity1.setLastName("Patel");
        userEntity1.setEmail("shilpi@pagli.com");
        testEntityManager.persistAndFlush(userEntity1);

        userEntity2 = new UserEntity();
        userEntity2.setUserId("2");
        userEntity2.setFirstName("Anirudh");
        userEntity2.setLastName("Patel");
        userEntity2.setEmail("patel.anirudh@gmail.com");
    }

    @DisplayName("FindByEmail")
    @Test
    void testFindByEmail_whenCorrectEmailGiven_returnsUserEntity() {
        // Arrange
        testEntityManager.persistAndFlush(userEntity1);
        testEntityManager.persistAndFlush(userEntity2);

        // Act
        UserEntity returnedUser = userRepo.findByEmail(userEntity1.getEmail());

        // Assert
        Assertions.assertNotNull(returnedUser, "UserEntity should not be null");
        Assertions.assertEquals(userEntity1.getFirstName(), returnedUser.getFirstName(), "Returned user firstName does not match the expected value");
    }

    @DisplayName("FindByUserId")
    @Test
    void testFindByUserId_whenCorrectUserIdGiven_returnsUserEntity() {
        // Arrange
        testEntityManager.persistAndFlush(userEntity1);
        testEntityManager.persistAndFlush(userEntity2);

        // Act
        UserEntity returnedUser = userRepo.findByUserId(userEntity2.getUserId());

        // Assert
        Assertions.assertNotNull(returnedUser, "UserEntity should not be null");
        Assertions.assertEquals(userEntity2.getFirstName(), returnedUser.getFirstName(), "Returned user firstName does not match the expected value");
    }

    @DisplayName("findUsersWithEmailEndingWith")
    @Test
    void testFindUsersWithEmailEndingWith_whenGivenCorrectEmailPattern_returnsUser() {
        // Arrange
        testEntityManager.persistAndFlush(userEntity1);
        testEntityManager.persistAndFlush(userEntity2);
        String emailPatternName = "@pagli.com";

        // Act
        List<UserEntity> returnedUsers = userRepo.findUsersWithEmailEndingWith(emailPatternName);

        // Assert
        Assertions.assertNotNull(returnedUsers, "UserEntity should not be null");
        Assertions.assertEquals(1, returnedUsers.size(),"There should be only 1 user in the list");
        Assertions.assertTrue(returnedUsers.get(0).getEmail().endsWith(emailPatternName), "User's email does not match with target email name pattern");
    }
}
