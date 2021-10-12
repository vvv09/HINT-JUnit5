package com.valunskii.labjunit5.service;

import com.valunskii.labjunit5.dto.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) //-чтобы не делать @BeforeAll / @AfterAll статичными
class UserServiceTest {

    private UserService userService;

    @BeforeAll
    static void beforeAll(){
        System.out.println("Before All");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before Each: " + this);
        userService = new UserService();
    }

    @Test
    void users_empty_if_no_user_added() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "Список пользователей должен быть пустой");
    }

    @Test
    void user_size_when_user_added() {
        System.out.println("Test 2: " + this);
        userService.add(new User());
        userService.add(new User());

        var users = userService.getAll();
        assertEquals(2, users.size(), () -> "Список пользователей должен содержать 2 элемента");
    }

    @AfterEach
    void clear() {
        System.out.println("After Each: " + this);
    }

    @AfterAll
    static void afterAll(){
        System.out.println("After All");
    }
}
