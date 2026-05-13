package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    void setUp() {
        indexController = new IndexController();
    }

    @Test
    void whenRequestIndexPageThenGetIndexView() {
        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}
