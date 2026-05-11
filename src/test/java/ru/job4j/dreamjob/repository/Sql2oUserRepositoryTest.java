package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
        }
    }

    @Test
    void whenSaveThenFindByEmailAndPassword() {
        User user = new User(1, "user@mail.com", "user", "password");
        sql2oUserRepository.save(user);
        Optional<User> foundUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenSaveDuplicateEmailThenGetEmptyOptional() {
        User user1 = new User(0, "user@mail.com", "user1", "password");
        User user2 = new User(0, "user@mail.com", "user2", "password");
        sql2oUserRepository.save(user1);
        Optional<User> optionalUser = sql2oUserRepository.save(user2);
        assertThat(optionalUser).isEmpty();
    }

    @Test
    void whenFindByWrongEmailThenGetEmptyOptional() {
        User user = new User(0, "user@mail.com", "user", "password");
        sql2oUserRepository.save(user);
        Optional<User> optionalUser = sql2oUserRepository.findByEmailAndPassword("wrong", user.getPassword());
        assertThat(optionalUser).isEmpty();
    }

    @Test
    void whenFindByWrongPasswordThenGetEmptyOptional() {
        User user = new User(0, "user@mail.com", "user", "password");
        sql2oUserRepository.save(user);
        Optional<User> optionalUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), "wrong");
        assertThat(optionalUser).isEmpty();
    }
}
