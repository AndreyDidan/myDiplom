package TicketManager.systems.controller;

import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.exception.ValidationException;

import TicketManager.systems.model.System;
import TicketManager.systems.repository.SystemRepository;
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
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private SystemRepository systemRepository;

    @GetMapping//вместо blog-main
    public String systems(Model model, @AuthenticationPrincipal MyUser user) {
        List<System> systems = systemRepository.findAll();
        model.addAttribute("systems", systems);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "systems";
    }

    @GetMapping("/add")
    public String systemAdd(Model model, @AuthenticationPrincipal MyUser user) {

        // Получаем все краткие наименования организаций
        List<String> contractShortNames = contractRepository.findAll().stream()
                .map(Contract::getNumberContract)
                .collect(Collectors.toList());

        model.addAttribute("contractShortNames", contractShortNames); // Передаем в модель
        model.addAttribute("systems", systemRepository.findAll());
        model.addAttribute("title", "Добавление должности");
        model.addAttribute("currentUser", user);

        return "systems_add";
    }

    @PostMapping("/add")
    public String systemAddSubmit(@RequestParam String nameSystem,
                                  @RequestParam String littleNameSystem,
                                  @RequestParam String numberContract,
                                  @AuthenticationPrincipal MyUser user,
                                  Model model) {

        // Получаем контракт по номеру контракта
        Contract contract = contractRepository.findByNumberContract(numberContract)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if (systemRepository.findByNameSystem(nameSystem).isPresent()) {
            throw new ValidationException("Система " + nameSystem + " уже существует.");
        }

        System system = new System(nameSystem, littleNameSystem, contract);
        //systemRepository.save(newSystem);

        systemRepository.save(system);
        return "redirect:/system"; // Перенаправление на страницу со списком систем
    }

    @GetMapping("/{systemId}")//вместо blog-main
    public String systemDetails(@PathVariable(value = "systemId") Long systemId, Model model, @AuthenticationPrincipal MyUser users) {
        if (!systemRepository.existsById(systemId)) {
            return "redirect:/system";
        }
        Optional<System> system = systemRepository.findById(systemId);
        ArrayList<System> result = new ArrayList<>();
        system.ifPresent(result::add);
        model.addAttribute("system", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "system_details";
    }

    @PostMapping("/{systemId}/remove")//вместо blog-main
    public String managerPositionDelete(
            @PathVariable(value = "systemId") Long systemId,
            Model model, @AuthenticationPrincipal MyUser users) {
        System system = systemRepository.findById(systemId).orElseThrow();
        systemRepository.delete(system);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/system";
    }

    /*
    @PostMapping("/add")
    public String positionAddSubmit(@RequestParam String namePosition,
                                    @RequestParam String descriptionPosition,
                                    @RequestParam String organizationId,
                                    @RequestParam String departmentId,
                                    @AuthenticationPrincipal MyUser user,
                                    Model model) {

        // Получаем организацию и отдел по их ID (или по краткому наименованию)
        Organization organization = organizationRepository.findByLittleNameOrganization(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Department department = departmentRepository.findByNameDepartment(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Проверка, существует ли уже должность с таким названием в выбранном отделе
        boolean positionExists = positionsRepository.findAll().stream()
                .anyMatch(position -> position.getDepartmentId().equals(department) && position.getNamePosition().equals(namePosition));

        if (positionExists) {
            // Если должность уже существует, добавляем ошибку в модель и возвращаем на страницу с формой
            model.addAttribute("errorMessage", "Должность с таким названием уже существует в выбранном отделе.");
            model.addAttribute("organizationShortNames", organizationRepository.findAll().stream()
                    .map(Organization::getLittleNameOrganization)
                    .collect(Collectors.toList()));

            // Мы также передаем данные о выбранном отделе и организации
            model.addAttribute("namePosition", namePosition);
            model.addAttribute("descriptionPosition", descriptionPosition);
            model.addAttribute("organizationId", organizationId);
            model.addAttribute("departmentId", departmentId);

            return "positions_add"; // Возвращаем ту же форму с ошибкой
        }

        // Создаем новую должность
        Position newPosition = new Position(namePosition, descriptionPosition, organization, department);

        // Сохраняем новую должность
        positionsRepository.save(newPosition);

        // Перенаправляем на страницу списка должностей (это может быть главная страница или страница списка)
        return "redirect:/position"; // Перенаправление на главную страницу должностей
    }
     */
}