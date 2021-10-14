package com.valunskii.labjunit5.service;

import com.valunskii.labjunit5.dto.User;
import com.valunskii.labjunit5.paramresilver.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.crypto.spec.IvParameterSpec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) //-чтобы не делать @BeforeAll / @AfterAll статичными
@Tag("fast")
@Tag("user")
@ExtendWith({
        UserServiceParamResolver.class
})
class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Peter", "111");
    private static final String WRONG_USERNAME = "dummy";
    private static final String WRONG_PASSWORD = "dummy";

    private UserService userService;


    /*
    *  Пример встроенного в JUnit5 DI
    *  Если проект не на Spring то почему бы не использовать этот DI ;)
    */
    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void beforeAll(){
        System.out.println("Before All");
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before Each: " + this);
        this.userService = userService; //- теперь инжектим
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
    void users_converted_to_map_by_id(){
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

    }

    @Nested
    @Tag("login")
    class LoginTest {

        @Test
        @DisplayName("Если пользователь существует, то логин успешный")
        void login_success_if_user_exists(){
            userService.add(IVAN);
            Optional<User> requestedUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(requestedUser).isPresent();
            requestedUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//        assertTrue(requestedUser.isPresent(), () -> "Должен вернуться существующий пользователь");
//        requestedUser.ifPresent(user -> assertEquals(IVAN, user, () -> "Должен вернуться конкретный пользователь"));
        }

        @Test
        @DisplayName("Если пароль не верный, то логин не проходит")
        void login_fail_if_password_not_correct(){
            userService.add(IVAN);

            Optional<User> requestedUser = userService.login(IVAN.getUsername(), WRONG_PASSWORD);

            assertTrue(requestedUser.isEmpty(), () -> "Пользователь не должен вернуться");
        }

        @Test
        @DisplayName("Если пользователь не существует, то логин не проходит")
        void login_fail_if_user_does_not_exist(){
            userService.add(IVAN);

            Optional<User> requestedUser = userService.login(WRONG_USERNAME, IVAN.getPassword());

            assertTrue(requestedUser.isEmpty(), () -> "Пользователь не должен вернуться");
        }

        @Test
        @DisplayName("Если имя пользователя или пароль равны null, то бросается IllegalArgumentException с сообщением \"username or password is null\"")
        void throw_exception_if_username_or_password_is_null() {
            assertAll(
                    () -> {
                        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, IVAN.getPassword()));
                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, ()-> userService.login(IVAN.getUsername(), null))
            );
        }

    }


    @ParameterizedTest
//    @ArgumentsSource()
    /*
    * Ниже готовые наследники @ArgumentSource, которые можно использовать, если входной параметр один.
    * */
//    @NullSource
//    @ValueSource
//    @EmptySource
//    @NullAndEmptySource
    //Все аннотации можно использовать сразу - каждая по очереди вызовет этот тест
    @DisplayName("Проверка логина")
    @MethodSource("com.valunskii.labjunit5.service.UserServiceTest#getArgumentsForLoginParameterizedTest")
    void loginParameterizedTest(String username, String password, Optional<User> expectedUser) {
        userService.add(IVAN, PETR);
        var requestedUser = userService.login(username, password);
        assertThat(requestedUser).isEqualTo(expectedUser);
    }

    static Stream<Arguments> getArgumentsForLoginParameterizedTest() {
        return Stream.of(
                Arguments.of(IVAN.getUsername(), IVAN.getPassword(), Optional.of(IVAN)),
                Arguments.of(PETR.getUsername(), PETR.getPassword(), Optional.of(PETR)),
                Arguments.of(PETR.getUsername(), WRONG_PASSWORD, Optional.empty()),
                Arguments.of(WRONG_USERNAME, PETR.getPassword(), Optional.empty()),
                Arguments.of(WRONG_USERNAME, WRONG_PASSWORD, Optional.empty())
        );
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
