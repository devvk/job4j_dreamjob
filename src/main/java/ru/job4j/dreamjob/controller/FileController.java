package ru.job4j.dreamjob.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable int id) {
        Optional<FileDto> contentOptional = fileService.findById(id);
        return contentOptional.map(fileDto -> ResponseEntity.ok(fileDto.getContent()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
