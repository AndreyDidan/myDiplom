package TicketManager.user.controller;

import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.positions.model.Position;
import TicketManager.organization.department.positions.repository.PositionsRepository;
import TicketManager.organization.department.repository.DepartmentRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import TicketManager.user.role.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/register")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private PositionsRepository positionsRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    @GetMapping("/departments")
    @ResponseBody
    public List<String> getDepartments(@RequestParam String organizationShortName, @AuthenticationPrincipal MyUser users) {
        Optional<Organization> organizationOpt = organizationRepository.findByLittleNameOrganization(organizationShortName);
        if (organizationOpt.isPresent()) {
            Organization organization = organizationOpt.get();
            return departmentRepository.findAllDepartmentOrganization(organization.getOrganizationId())
                    .stream()
                    .map(Department::getNameDepartment)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/positions")
    @ResponseBody
    public List<String> getPositions(@RequestParam String departmentName, @RequestParam String organizationShortName, @AuthenticationPrincipal MyUser users) {
        Optional<Organization> organizationOpt = organizationRepository.findByLittleNameOrganization(organizationShortName);
        if (organizationOpt.isPresent()) {
            Organization organization = organizationOpt.get();
            Department department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentName, organization.getOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            return positionsRepository.findByDepartmentId(department)
                    .stream()
                    .map(Position::getNamePosition)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @GetMapping("/users")//вместо blog-main
    public String users(Model model, @AuthenticationPrincipal MyUser user) {
        List<MyUser> myUsers = myUserRepository.findAll();

        model.addAttribute("myUsers", myUsers);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "users";
    }


    @GetMapping("/users/add")
    public String userAdd(Model model, @AuthenticationPrincipal MyUser user) {
        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());

        List<String> departmentShortNames = departmentRepository.findAll().stream()
                .map(Department::getNameDepartment)
                .collect(Collectors.toList());

        List<String> positionShortNames = positionsRepository.findAll().stream()
                .map(Position::getNamePosition)
                .collect(Collectors.toList());

        List<MyUser> myUsers = myUserRepository.findAll();

        model.addAttribute("organizationShortNames", organizationShortNames);
        model.addAttribute("departmentShortNames", departmentShortNames);
        model.addAttribute("positionShortNames", positionShortNames);

        model.addAttribute("myUsers", myUsers);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "users_add";
    }

    @PostMapping("/users/add")
    public String ManagerUserAdd(
            @RequestParam String username,
            @RequestParam String login,
            @RequestParam String surname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String namePosition,
            @RequestParam Set<Role> role,  // Роли теперь принимаются как Set
            @RequestParam boolean active,
            @RequestParam String organizationLitleName,
            @RequestParam String departmentName,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (myUserRepository.findByLogin(login).isPresent()) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "users_add"; // вернуть на страницу регистрации с ошибкой
        }

        // Найти организацию по короткому имени
        Optional<Organization> organization = organizationRepository.findByLittleNameOrganization(organizationLitleName);
        if (!organization.isPresent()) {
            model.addAttribute("error", "Организация не найдена");
            return "users_add";
        }
        Organization org = organization.get();

        // Найти отдел по имени и ID организации
        Optional<Department> department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentName, org.getOrganizationId());
        if (!department.isPresent()) {
            model.addAttribute("error", "Отдел не найден");
            return "users_add";
        }
        Department dept = department.get();
        // Найти должность по имени должности, организации и отделу
        Optional<Position> positionOpt = positionsRepository.findByNamePositionAndDepartmentId(namePosition, dept.getDepartmentId());
        if (!positionOpt.isPresent()) {
            model.addAttribute("error", "Должность не найдена");
            return "users_add";
        }
        Position position1 = positionOpt.get();
        // Создание нового пользователя

        MyUser user = new MyUser(username, surname, email, login, passwordEncoder.encode(password), phone, position1, role, active);
        myUserRepository.save(user);  // Сохранение пользователя в базе данных
        // Перенаправление на страницу со списком пользователей
        return "redirect:/register/users";
    }

    @GetMapping("/users/{userId}")//вместо blog-main
    public String userDetails(@PathVariable(value = "userId") Long userId, Model model, @AuthenticationPrincipal MyUser users) {
        if (!myUserRepository.existsById(userId)) {
            return "redirect:/register/users";
        }
        Optional<MyUser> user = myUserRepository.findById(userId);
        ArrayList<MyUser> result = new ArrayList<>();
        user.ifPresent(result::add);
        model.addAttribute("user", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "user_details";
    }

    @GetMapping("/users/{userId}/edit")//вместо blog-main
    public String userEdit(@PathVariable(value = "userId") Long userId, Model model, @AuthenticationPrincipal MyUser users) {
        if (!myUserRepository.existsById(userId)) {
            model.addAttribute("title", "Главная страница");
            model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
            return "redirect:/register/users";
        }
        Optional<MyUser> user = myUserRepository.findById(userId);
        ArrayList<MyUser> result = new ArrayList<>();
        user.ifPresent(result::add);
        model.addAttribute("user", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "user_edit";
    }

    @PostMapping("/users/{userId}/edit")
    public String ManagerUserUpdate(
            @PathVariable(value = "userId") Long userId,
            @Valid MyUser updatedUser, // Используйте аннотацию @Valid
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (bindingResult.hasErrors()) {
            // Если есть ошибки, возвращаем форму с ошибками
            model.addAttribute("title", "Редактирование пользователя");
            model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
            return "user_edit";
        }

        MyUser user = myUserRepository.findById(userId).orElseThrow();
        user.setUsername(updatedUser.getUsername());
        user.setLogin(updatedUser.getLogin());
        user.setSurname(updatedUser.getSurname());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setPhone(updatedUser.getPhone());
        user.setPositionId(user.getPositionId());
        user.setRole(updatedUser.getRole());
        user.setActive(updatedUser.isActive());

        myUserRepository.save(user);

        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users);
        return "redirect:/register/users";
    }

    @PostMapping("/users/{userId}/remove")//вместо blog-main
    public String ManagerUserDelete(
            @PathVariable(value = "userId") Long userId,
            Model model, @AuthenticationPrincipal MyUser users) {
        MyUser user = myUserRepository.findById(userId).orElseThrow();
        myUserRepository.delete(user);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/register/users";
    }
}