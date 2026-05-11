package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

@Controller
@RequestMapping("/candidates")
@ThreadSafe
public class CandidateController {

    private final CandidateService candidateService;

    private final CityService cityService;

    public CandidateController(CandidateService candidateService, CityService cityService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        addUserToModel(model, session);
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model, HttpSession session) {
        addUserToModel(model, session);
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        try {
            candidateService.save(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/candidates";
        } catch (Exception e) {
            addUserToModel(model, session);
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model, HttpSession session) {
        addUserToModel(model, session);
        var candidateOptional = candidateService.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден!");
            return "errors/404";
        }
        model.addAttribute("candidate", candidateOptional.get());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        addUserToModel(model, session);
        try {
            boolean isUpdate = candidateService.update(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdate) {
                model.addAttribute("message", "Кандидат с указанным идентификатором не найден!");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model, HttpSession session) {
        boolean isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            addUserToModel(model, session);
            model.addAttribute("message", "Кандидат с указанным идентификатором не найден!");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    private void addUserToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        model.addAttribute("user", user);
    }
}

