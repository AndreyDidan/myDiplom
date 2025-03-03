package TicketManager.organization.department.controller;

import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.repository.DepartmentRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.user.model.MyUser;
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

@Controller
@RequestMapping("/organization/{organizationId}/department")
public class DepartmentController {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    @GetMapping//вместо blog-main
    public String departments(@PathVariable(value = "organizationId") Long organizationId,
            Model model, @AuthenticationPrincipal MyUser users) {
        List<Department> departments = departmentRepository.findAllDepartmentOrganization(organizationId);
        Organization organization = organizationRepository.findById(organizationId).get();
        model.addAttribute("organization", organization);
        model.addAttribute("departments", departments);
        model.addAttribute("title", "Главная страница");
        return "departments_organization";
    }

    @Transactional(readOnly = true)
    @GetMapping("/all")//вместо blog-main
    public String departmentsAll(Model model, @AuthenticationPrincipal MyUser users) {
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("title", "Главная страница");
        return "departments";
    }

    @Transactional(readOnly = true)
    @GetMapping("/add")
    public String departmentAdd(@PathVariable(value = "organizationId") Long organizationId,
                                Model model, @AuthenticationPrincipal MyUser users) {

        List<Department> departments = departmentRepository.findAllDepartmentOrganization(organizationId);
        List<Organization> organizations = organizationRepository.findAll();  // Получение всех организаций

        model.addAttribute("organizations", organizations);  // Передаем в модель
        model.addAttribute("departments", departments);
        model.addAttribute("title", "Добавление отдела");
        return "departments_organization_add";
    }


   /* @PostMapping("/add")
    public String managerDepartmentAdd(@PathVariable(value = "organizationId") Long organizationId,
                                       @RequestParam String nameDepartment,
                                       //@RequestParam Long organizationId, // Принимаем organizationId как Long
                                       Model model) {
        Optional<Organization> organizationOpt = organizationRepository.findById(organizationId);

        if (!organizationOpt.isPresent()) {
            model.addAttribute("error", "Такой организации не существует");
            return "departments_add";
        }

        Organization organization = organizationOpt.get();

        Optional<Department> department = departmentRepository
                .findByNameDepartmentAndOrganizationId(nameDepartment, organizationId);
        if (department.isPresent()) {
            model.addAttribute("error", "Такой отдел уже существует в этой организации");
            return "departments_add";
        }

        // Создание нового отдела
        Department newDepartment = new Department(nameDepartment, organization);
        departmentRepository.save(newDepartment);

        return "redirect:/organization/{organizationId}/department";
    }*/
   @PostMapping("/add")
   public String managerDepartmentAdd(@PathVariable(value = "organizationId") Long organizationId,
                                      @RequestParam String nameDepartment,
                                      Model model, @AuthenticationPrincipal MyUser users) {
       Optional<Organization> organizationOpt = organizationRepository.findById(organizationId);

       if (!organizationOpt.isPresent()) {
           model.addAttribute("error", "Такой организации не существует");
           return "departments_organization_add";
       }

       Organization organization = organizationOpt.get();

       Optional<Department> department = departmentRepository
               .findByNameDepartmentAndOrganizationId(nameDepartment, organizationId);
       if (department.isPresent()) {
           model.addAttribute("error", "Такой отдел уже существует в этой организации");
           return "departments_organization_add";
       }

       // Создание нового отдела
       Department newDepartment = new Department(nameDepartment, organization);
       departmentRepository.save(newDepartment);

       return "redirect:/organization/{organizationId}/department";
   }

    @Transactional(readOnly = true)
    @GetMapping("/{departmentId}")//вместо blog-main
    public String departmentDetails(@PathVariable(value = "organizationId") Long organizationId ,
                                    @PathVariable(value = "departmentId") Long departmentId,
                                    Model model, @AuthenticationPrincipal MyUser users) {
        if (!departmentRepository.existsById(departmentId)) {
            return "redirect:/department";
        }
        Optional<Department> department = departmentRepository.findById(departmentId);
        ArrayList<Department> result = new ArrayList<>();
        department.ifPresent(result::add);
        model.addAttribute("department", result);
        model.addAttribute("title", "Главная страница");
        return "department_organization_details";
    }













    @PostMapping("/{departmentId}/remove")//вместо blog-main
    public String managerDepartmentDelete(@PathVariable(value = "organizationId") Long organizationId,
            @PathVariable(value = "departmentId") Long departmentId,
            Model model) {
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        departmentRepository.delete(department);
        model.addAttribute("title", "Главная страница");
        return "redirect:/organization/{organizationId}/department";
    }

    @Transactional(readOnly = true)
    @GetMapping("/{departmentId}/edit")
    public String departmentEdit(@PathVariable(value = "departmentId") Long departmentId, Model model) {
        // Ищем отдел по его ID
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);

        // Если отдел не найден, перенаправляем на главную страницу
        if (departmentOpt.isEmpty()) {
            model.addAttribute("title", "Главная страница");
            return "redirect:/department";
        }

        // Получаем текущий отдел
        Department department = departmentOpt.get();

        // Получаем список всех кратких наименований организаций
        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());

        // Передаем в модель:
        model.addAttribute("department", department);  // Передаем сам объект отдела
        model.addAttribute("organizationShortNames", organizationShortNames);  // Список организаций
        model.addAttribute("currentOrganizationId", department.getOrganizationId().getLittleNameOrganization());  // Краткое наименование текущей организации
        model.addAttribute("title", "Редактирование отдела");

        // Возвращаем шаблон для редактирования отдела
        return "department_edit";
    }

    @PostMapping("/{departmentId}/edit")
    public String managerOrganizationUpdate(
            @PathVariable(value = "departmentId") Long departmentId,
            @RequestParam String nameDepartment,
            @RequestParam String organizationId,
            Model model) {

        // Получаем существующий объект отдела
        Department existingDepartment = departmentRepository.findById(departmentId).orElseThrow();

        Organization organization;

        // Проверяем существование организации по переданному ID
        if (!organizationRepository.findByLittleNameOrganization(organizationId).isPresent()) {
            model.addAttribute("error", "Такой организации не существует");
            return "department_edit"; // Возвращаем с ошибкой
        } else {
            Optional<Organization> organizationOpt = organizationRepository.findByLittleNameOrganization(organizationId);
            organization = organizationOpt.get();
        }

        // Обновляем данные отдела
        existingDepartment.setNameDepartment(nameDepartment); // Устанавливаем новое название отдела
        existingDepartment.setOrganizationId(organization);  // Обновляем организацию

        departmentRepository.save(existingDepartment);  // Сохраняем изменения

        model.addAttribute("title", "Главная страница");
        return "redirect:/department";
    }
}