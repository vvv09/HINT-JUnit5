package com.valunskii.labjunit5.service;

import com.valunskii.labjunit5.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.*;

import javax.crypto.spec.IvParameterSpec;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) //-чтобы не делать @BeforeAll / @AfterAll статичными
class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Peter", "111");

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

        MatcherAssert.assertThat(users, IsEmptyCollection.empty());

        assertTrue(users.isEmpty(), () -> "Список пользователей должен быть пустой");
    }

    @Test
    void user_size_when_user_added() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
//        assertEquals(2, users.size(), () -> "Список пользователей должен содержать 2 элемента");
    }

    @Test
    void login_success_if_user_exists(){
        userService.add(IVAN);
        Optional<User> requestedUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertThat(requestedUser).isPresent();
        requestedUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//        assertTrue(requestedUser.isPresent(), () -> "Должен вернуться существующий пользователь");
//        requestedUser.ifPresent(user -> assertEquals(IVAN, user, () -> "Должен вернуться конкретный пользователь"));
    }

    @Test
    void users_converted_to_map_by_id(){
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

    }

    @Test
    void login_fail_if_password_not_correct(){
        userService.add(IVAN);

        Optional<User> requestedUser = userService.login(IVAN.getUsername(), "wrong_password");

        assertTrue(requestedUser.isEmpty(), () -> "Пользователь не должен вернуться");
    }

    @Test
    void login_fail_if_user_does_not_exist(){
        userService.add(IVAN);

        Optional<User> requestedUser = userService.login("Anton", IVAN.getPassword());

        assertTrue(requestedUser.isEmpty(), () -> "Пользователь не должен вернуться");
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
