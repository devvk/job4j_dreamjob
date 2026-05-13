package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenRequestRegistrationPageThenGetRegistrationView() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/create");
    }

    @Test
    void whenRegisterUserThenRedirectToIndexPage() {
        var user = new User(1, "test@test.com", "name", "password");
        when(userService.save(user)).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);

        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    void whenRegisterUserWithExistingEmailThenGetErrorPageWithMessage() {
        var user = new User(1, "test@test.com", "name", "password");
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo("Пользователь с такой почтой уже существует.");
    }

    @Test
    void whenRequestLoginPageThenGetLoginView() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenLoginUserThenRedirectToVacanciesAndSetUserToSession() {
        var user = new User(1, "test@test.com", "name", "password");
        var session = mock(HttpSession.class);
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.login(user, model, session);

        assertThat(view).isEqualTo("redirect:/vacancies");
        // проверяем был ли вызван метод у мок-объекта и с какими аргументами!
        verify(session).setAttribute("user", user);
    }

    @Test
    void whenLoginWithWrongCredentialsThenGetLoginPageWithError() {
        var session = mock(HttpSession.class);
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.login(new User(), model, session);
        var actualExceptionMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualExceptionMessage).isEqualTo("Почта или пароль введены неверно.");
    }

    @Test
    void whenLogoutUserThenInvalidateSessionAndRedirectToLoginPage() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
        verify(session).invalidate();
    }
}
