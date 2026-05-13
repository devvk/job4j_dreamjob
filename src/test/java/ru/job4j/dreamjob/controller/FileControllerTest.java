package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AAA: Arrange (Подготовка), Act (Действие), Assert (Утверждение/Проверка)
 */
class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenRequestFileByIdThenGetFileContent() {
        int fileId = 1;
        var fileDto = new FileDto("name", new byte[]{1, 2, 3});
        when(fileService.findById(fileId)).thenReturn(Optional.of(fileDto));

        var view = fileController.getById(fileId);

        assertThat(view).isEqualTo(ResponseEntity.ok(fileDto.getContent()));
    }

    @Test
    void whenRequestFileByWrongIdThenGetNotFound() {
        int fileId = 1;
        when(fileService.findById(fileId)).thenReturn(Optional.empty());

        var view = fileController.getById(fileId);

        assertThat(view).isEqualTo(ResponseEntity.notFound().build());
    }
}
