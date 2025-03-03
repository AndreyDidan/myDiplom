package TicketManager.organization.department.positions.controller;

import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.positions.model.Position;
import TicketManager.organization.department.repository.DepartmentRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.department.positions.repository.PositionsRepository;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.user.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/organization/department/{departmentId}/position")
public class PositionController {

    @Autowired
    private PositionsRepository positionsRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping//вместо blog-main
    public String departments(@PathVariable(value = "departmentId") Long departmentId,
                              Model model, @AuthenticationPrincipal MyUser users) {
        List<Position> positions = positionsRepository.findAllPositionsDepartment(departmentId);
        Department department = departmentRepository.findById(departmentId).get();

        model.addAttribute("department", department);
        model.addAttribute("positions", positions);
        model.addAttribute("title", "Главная страница");
        return "positions";
    }

    @GetMapping("/all")//вместо blog-main
    public String positions(@PathVariable(value = "departmentId") Long departmentId, Model model, @AuthenticationPrincipal MyUser users) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        Long orgId = department.get().getOrganizationId().getOrganizationId();
        List<Department> departments = departmentRepository.findAllDepartmentOrganization(orgId);
        List<Position> positions = positionsRepository.findAll();

        model.addAttribute("departments", departments);
        model.addAttribute("positions", positions);
        model.addAttribute("title", "Главная страница");
        return "positions";
    }

    @GetMapping("/add")//вместо blog-main
    public String positionAdd(@PathVariable(value = "departmentId") Long departmentId, Model model, @AuthenticationPrincipal MyUser users) {

        List<Position> positions = positionsRepository.findAllPositionsDepartment(departmentId);
        Optional<Department> department = departmentRepository.findById(departmentId);
        Optional<Organization> organization = organizationRepository.findById(department.get().getOrganizationId().getOrganizationId());
        List<Department> departments = departmentRepository.findAllDepartmentOrganization(organization.get().getOrganizationId());
        model.addAttribute("positions", positions);
        model.addAttribute("departments", departments);
        model.addAttribute("title", "Главная страница");
        return "positions_add";
    }

    @PostMapping("/add")
    public String positionAddSubmit(
            @PathVariable(value = "departmentId") Long departmentId,
            @RequestParam String namePosition,
            @RequestParam String descriptionPosition,
            Model model, @AuthenticationPrincipal MyUser users) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (!department.isPresent()) {
            model.addAttribute("error", "Такого отдела не существует");
            return "positions_add";
        }
        Department department1 = department.get();
        Optional<Position> position = positionsRepository
                .findByNamePositionAndDepartmentId(namePosition, departmentId);

        if (position.isPresent()) {
            model.addAttribute("error", "Такая должность уже существует в этом отделе");
            return "positions_add";
        }
        Position newPosition = new Position(namePosition, descriptionPosition, department1);
        positionsRepository.save(newPosition);
        return "redirect:/organization/department/{departmentId}/position";
    }

    @GetMapping("/positions")
    @ResponseBody
    public List<String> getPositionsByDepartment(@RequestParam String departmentName, @RequestParam String organizationShortName, @AuthenticationPrincipal MyUser users) {
        Organization organization = organizationRepository.findByLittleNameOrganization(organizationShortName)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Найти отдел по имени и ID организации
        Department department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentName, organization.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // Получаем список должностей для выбранного отдела
        List<String> positionNames = positionsRepository.findByDepartmentId(department).stream()
                .map(Position::getNamePosition)
                .collect(Collectors.toList());

        return positionNames;
    }


    // Метод для получения списка должностей по выбранному отделу
    @GetMapping("/departments/organizations")
    @ResponseBody
    public List<String> getDepartmentsByOrganization(@RequestParam String departmentShortNames, @RequestParam Long organizationId, @AuthenticationPrincipal MyUser users) {
        Department department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentShortNames, organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Получаем список отделов для выбранной организации
        List<String> positiontNames = positionsRepository.findByNamePositionAndDepartmentId(
                departmentShortNames, department.getDepartmentId()).stream()
                .map(Position::getNamePosition)
                .collect(Collectors.toList());

        return positiontNames;
    }

    // Метод для получения списка отделов по выбранной организации
    @GetMapping("/departments")
    @ResponseBody
    public List<String> getPositionsByDepartment(@RequestParam String organizationShortName, @AuthenticationPrincipal MyUser users) {
        Organization organization = organizationRepository.findByLittleNameOrganization(organizationShortName)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Получаем список отделов для выбранной организации
        List<String> departmentNames = departmentRepository.findByOrganizationId(organization).stream()
                .map(Department::getNameDepartment)
                .collect(Collectors.toList());

        return departmentNames;
    }

    public Optional<Position> findPositionByNameAndOrganizationAndDepartment(String namePosition, Long organizationId, Long departmentId) {
        return positionsRepository.findByNamePositionAndDepartmentId(namePosition, departmentId);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{positionId}")//вместо blog-main
    public String positionDetails(@PathVariable(value = "positionId") Long positionId,
                                  @PathVariable(value = "departmentId") Long departmentId, Model model, @AuthenticationPrincipal MyUser users) {
        if (!positionsRepository.existsById(positionId)) {
            return "redirect:/position";
        }
        Optional<Position> position = positionsRepository.findById(positionId);
        ArrayList<Position> result = new ArrayList<>();
        position.ifPresent(result::add);
        model.addAttribute("position", result);
        model.addAttribute("title", "Главная страница");
        return "position_details";
    }


    @PostMapping("/{positionId}/remove")//вместо blog-main
    public String managerPositionDelete(
            @PathVariable(value = "positionId") Long positionId,
            @PathVariable(value = "departmentId") Long departmentId, Model model, @AuthenticationPrincipal MyUser users) {
        Position position = positionsRepository.findById(positionId).orElseThrow();
        positionsRepository.delete(position);
        model.addAttribute("title", "Главная страница");
        return "redirect:/organization/department/{departmentId}/position";
    }

    @GetMapping("/{positionId}/edit")
    public String positionEdit(@PathVariable(value = "positionId") Long positionId,
                               @PathVariable(value = "departmentId") Long departmentId, Model model, @AuthenticationPrincipal MyUser users) {
        // Ищем отдел по его ID
        Optional<Position> positionOpt = positionsRepository.findById(positionId);

        // Если отдел не найден, перенаправляем на главную страницу
        if (positionOpt.isEmpty()) {
            model.addAttribute("title", "Главная страница");
            return "redirect:/organization/department/{departmentId}/position";
        }

        // Получаем текущий отдел
        Position position = positionOpt.get();

        // Получаем список всех кратких наименований организаций
        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());

        // Получаем список всех кратких наименований организаций
        List<String> departmentShortNames = departmentRepository.findAll().stream()
                .map(Department::getNameDepartment)
                .collect(Collectors.toList());



        // Передаем в модель:
        model.addAttribute("position", position);  // Передаем сам объект отдела
        model.addAttribute("organizationShortNames", organizationShortNames);  // Список организаций
        model.addAttribute("currentOrganizationId", position.getDepartmentId().getOrganizationId().getLittleNameOrganization());  // Краткое наименование текущей организации

        model.addAttribute("departmentShortNames", departmentShortNames);  // Список организаций
        model.addAttribute("currentDepartmentId", position.getDepartmentId().getNameDepartment());

        model.addAttribute("title", "Редактирование должности");

        // Возвращаем шаблон для редактирования отдела
        return "position_edit";
    }

    @PostMapping("/{positionId}/edit")
    public String managerPositionUpdate(
            @PathVariable(value = "positionId") Long positionId,
            @RequestParam String namePosition,
            @RequestParam String descriptionPosition,
            @RequestParam String organizationId,
            @RequestParam String departmentId,
            Model model, @AuthenticationPrincipal MyUser users) {

        // Проверка валидации
        if (namePosition.isEmpty() || descriptionPosition.isEmpty()) {
            model.addAttribute("errorMessage", "Пожалуйста, заполните все поля.");
            return "position_edit";
        }

        Organization organization = organizationRepository.findByLittleNameOrganization(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        Department department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentId, organization.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Position existingPosition = positionsRepository.findById(positionId).orElseThrow();
        existingPosition.setNamePosition(namePosition);
        existingPosition.setDescriptionPosition(descriptionPosition);
        existingPosition.setDepartmentId(department);

        // Сохранение обновленных данных
        positionsRepository.save(existingPosition);

        model.addAttribute("title", "Главная страница");
        return "redirect:/position"; // Перенаправление на страницу со списком должностей
    }
}