package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpSession;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

@Controller
@RequestMapping("/vacancies")
@ThreadSafe
public class VacancyController {

    private final VacancyService vacancyService;

    private final CityService cityService;

    public VacancyController(VacancyService vacancyService, CityService cityService) {
        this.vacancyService = vacancyService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        addUserToModel(model, session);
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model, HttpSession session) {
        addUserToModel(model, session);
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        try {
            vacancyService.save(vacancy, new FileDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/vacancies";
        } catch (Exception e) {
            addUserToModel(model, session);
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model, HttpSession session) {
        var vacancyOptional = vacancyService.findById(id);
        addUserToModel(model, session);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена!");
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, @RequestParam MultipartFile file,
                         Model model, HttpSession session) {
        addUserToModel(model, session);
        try {
            boolean isUpdated = vacancyService.update(vacancy, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("message", "Вакансия с указанным идентификатором не найдена!");
                return "errors/404";
            }
            return "redirect:/vacancies";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model, HttpSession session) {
        boolean isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            addUserToModel(model, session);
            model.addAttribute("message", "Вакансия с указанным идентификатором не найдена!");
            return "errors/404";
        }
        return "redirect:/vacancies";
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
