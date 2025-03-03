package TicketManager.contract.technicalspecification.controller;

import TicketManager.contract.technicalspecification.model.TechnicalSpecification;
import TicketManager.contract.technicalspecification.repository.TechnicalSpecificationRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.user.model.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/specification")
public class TechnicalSpecificationController {
    @Autowired
    private TechnicalSpecificationRepository technicalSpecificationRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping//вместо blog-main
    public String specifications(Model model, @AuthenticationPrincipal MyUser user) {
        List<TechnicalSpecification> specifications = technicalSpecificationRepository.findAll();
        model.addAttribute("specifications", specifications);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "specifications";
    }

    @GetMapping("/add")
    public String specificationAdd(Model model, @AuthenticationPrincipal MyUser user) {
        List<TechnicalSpecification> specifications = technicalSpecificationRepository.findAll();
        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList()); // Извлекаем все краткие наименования организаций

        model.addAttribute("organizationShortNames", organizationShortNames); // Передаем в модель
        model.addAttribute("specifications", specifications);
        model.addAttribute("title", "Добавление технического задания");
        model.addAttribute("currentUser", user);
        return "specification_add";
    }

    @PostMapping("/add")
    public String managerSpecificationAdd(
            @RequestParam String nameSpecification,
            @RequestParam String customer,
            @RequestParam String perfomer,
            @RequestParam String execution,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (technicalSpecificationRepository.findByNameSpecification(nameSpecification).isPresent()) {
            model.addAttribute("error", "Техническое задание с таким наименованием уже существует");
            return "specification_add"; // вернуть на страницу регистрации с ошибкой
        }

        Optional<Organization> customerOpt;
        Optional<Organization> perfomerOpt;
        Optional<Organization> executionOpt;

        Organization organizationCustomer;
        Organization organizationPerfomer;
        Organization organizationExecution;

        TechnicalSpecification technicalSpecification;

        if (!organizationRepository.findByLittleNameOrganization(customer).isPresent()) {
            model.addAttribute("error", "Такой организации не существует");
            return "specification_add"; // вернуть на страницу регистрации с ошибкой
        } else {
            customerOpt = organizationRepository.findByLittleNameOrganization(customer);
            organizationCustomer = customerOpt.get();
        }

        if (!organizationRepository.findByLittleNameOrganization(perfomer).isPresent()) {
            model.addAttribute("error", "Такой организации не существует");
            return "specification_add"; // вернуть на страницу регистрации с ошибкой
        } else {
            perfomerOpt = organizationRepository.findByLittleNameOrganization(perfomer);
            organizationPerfomer = perfomerOpt.get();
        }

        if (!organizationRepository.findByLittleNameOrganization(execution).isPresent()) {
            model.addAttribute("error", "Такой организации не существует");
            return "specification_add"; // вернуть на страницу регистрации с ошибкой
        } else {
            executionOpt = organizationRepository.findByLittleNameOrganization(execution);
            organizationExecution = perfomerOpt.get();
        }

        // Создание нового отдела
        technicalSpecification = new TechnicalSpecification(nameSpecification, organizationCustomer, organizationPerfomer,
                organizationExecution);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель

        technicalSpecificationRepository.save(technicalSpecification);  // Сохранение отдела

        return "redirect:/specification";
    }

    @GetMapping("/{id}")//вместо blog-main
    public String specificationDetails(@PathVariable(value = "id") Long id, Model model, @AuthenticationPrincipal MyUser users) {
        if (!technicalSpecificationRepository.existsById(id)) {
            return "redirect:/specification";
        }
        Optional<TechnicalSpecification> specification = technicalSpecificationRepository.findById(id);
        ArrayList<TechnicalSpecification> result = new ArrayList<>();
        specification.ifPresent(result::add);
        model.addAttribute("specification", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "specification_details";
    }

    @PostMapping("/{id}/remove")//вместо blog-main
    public String managerSpecificationDelete(
            @PathVariable(value = "id") Long id,
            Model model, @AuthenticationPrincipal MyUser users) {
        TechnicalSpecification specification = technicalSpecificationRepository.findById(id).orElseThrow();
        technicalSpecificationRepository.delete(specification);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/specification";
    }
}
