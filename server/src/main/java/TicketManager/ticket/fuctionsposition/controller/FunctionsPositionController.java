package TicketManager.ticket.fuctionsposition.controller;

import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.organization.department.model.Department;
import TicketManager.organization.department.positions.model.Position;
import TicketManager.organization.department.positions.repository.PositionsRepository;
import TicketManager.organization.department.repository.DepartmentRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.functionssystem.repository.FunctionSystemRepositry;
import TicketManager.systems.model.System;
import TicketManager.systems.repository.SystemRepository;
import TicketManager.ticket.fuctionsposition.model.FunctionsPosition;
import TicketManager.ticket.fuctionsposition.model.Rools;
import TicketManager.ticket.fuctionsposition.repository.FunctionsPositionRepository;
import TicketManager.user.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/position/function")
public class FunctionsPositionController {
    @Autowired
    private PositionsRepository positionsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FunctionSystemRepositry functionSystemRepositry;

    @Autowired
    private FunctionsPositionRepository functionsPositionRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private ContractRepository contractRepository;

    @GetMapping//вместо blog-main
    public String functionsPosytion(Model model, @AuthenticationPrincipal MyUser user) {
        List<FunctionsPosition> functionsPositions = functionsPositionRepository.findAll();
        model.addAttribute("functionsPositions", functionsPositions);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "functions_positions";
    }

    @GetMapping("/add")
    public String functionPosition(Model model, @AuthenticationPrincipal MyUser user) {

        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());

        List<String> departmentShortNames = departmentRepository.findAll().stream()
                .map(Department::getNameDepartment)
                .collect(Collectors.toList());

        List<String> positionShortNames = positionsRepository.findAll().stream()
                .map(Position::getNamePosition)
                .collect(Collectors.toList());

        List<String> systemShortNames = systemRepository.findAll().stream()
                .map(System::getLittleNameSystem)
                .collect(Collectors.toList());

        List<String> functionSystemShortNames = functionSystemRepositry.findAll().stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());

        List<Contract> contracts = contractRepository.findAll();


        List<FunctionsPosition> functionsPositions = functionsPositionRepository.findAll();

        model.addAttribute("contracts", contracts);
        model.addAttribute("organizationShortNames", organizationShortNames);
        model.addAttribute("departmentShortNames", departmentShortNames);
        model.addAttribute("positionShortNames", positionShortNames);

        model.addAttribute("systemShortNames", systemShortNames);
        model.addAttribute("functionSystemShortNames", functionSystemShortNames);

        model.addAttribute("functionsPositions", functionsPositions);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "functions_positions_add";
    }

    @PostMapping("/add")
    public String ManagerUserAdd(
            //@RequestParam String nameFunction,
            @RequestParam String organizationLitleName,
            @RequestParam String departmentName,
            @RequestParam String positionId,
            @RequestParam String contractNumber,
            @RequestParam List<String> nameFunctionSystem,  // Изменено на список
            @RequestParam Rools rools,
            Model model, @AuthenticationPrincipal MyUser users) {

        // Найти организацию по короткому имени
        Optional<Organization> organization = organizationRepository.findByLittleNameOrganization(organizationLitleName);
        if (!organization.isPresent()) {
            model.addAttribute("error", "Организация не найдена");
            return "functions_positions_add";
        }
        Organization org = organization.get();

        // Найти отдел по имени и ID организации
        Optional<Department> department = departmentRepository.findByNameDepartmentAndOrganizationId(departmentName, org.getOrganizationId());
        if (!department.isPresent()) {
            model.addAttribute("error", "Отдел не найден");
            return "functions_positions_add";
        }
        Department dept = department.get();

        // Найти должность по имени должности, организации и отделу
        Optional<Position> positionOpt = positionsRepository.findByNamePositionAndDepartmentId(positionId, dept.getDepartmentId());
        if (!positionOpt.isPresent()) {
            model.addAttribute("error", "Должность не найдена");
            return "functions_positions_add";
        }
        Position position1 = positionOpt.get();

        // Найти все функции по списку имен
        List<FunctionSystem> functionSystems = functionSystemRepositry.findByNameFunctionSystemIn(nameFunctionSystem);

        // Создание объекта FunctionsPosition с несколькими функциями
        FunctionsPosition functionsPosition = new FunctionsPosition(position1, contractNumber, functionSystems, rools);

        functionsPositionRepository.save(functionsPosition);  // Сохранение пользователя в базе данных

        // Перенаправление на страницу со списком функций должностей
        return "redirect:/position/function";
    }

    @GetMapping("/{functionPosityonId}")//вместо blog-main
    public String functionsPosytionDetails(@PathVariable(value = "functionPosityonId") Long functionPosityonId,
                                           Model model, @AuthenticationPrincipal MyUser users) {
        if (!functionsPositionRepository.existsById(functionPosityonId)) {
            return "redirect:/position/function";
        }
        Optional<FunctionsPosition> functionsPosition = functionsPositionRepository.findById(functionPosityonId);
        ArrayList<FunctionsPosition> result = new ArrayList<>();
        functionsPosition.ifPresent(result::add);
        model.addAttribute("functionsPosition", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "functions_positions_details";
    }

    @PostMapping("/{functionPosityonId}/remove")//вместо blog-main
    public String managerFunctionsPosytionDelete(
            @PathVariable(value = "functionPosityonId") Long functionPosityonId,
            Model model, @AuthenticationPrincipal MyUser users) {
        FunctionsPosition functionsPosition = functionsPositionRepository.findById(functionPosityonId).orElseThrow();
        functionsPositionRepository.delete(functionsPosition);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/position/function";
    }

    @GetMapping("/functionssystem")
    @ResponseBody
    public List<String> getFunctionsBySysten(@RequestParam String systemShortNames) {

        log.info("Попытка найти функции системы " + "'" + systemShortNames + "'");
        System system = systemRepository.findByLittleNameSystem(systemShortNames).get();

        log.info("Попытка найти функции системы " + "'" + system.getSystemId() + "'");
        // Получаем список функций для выбранной системы
        List<FunctionSystem> functionSystems = functionSystemRepositry.findBySystemId(system);

        return functionSystems.stream()
                .map(FunctionSystem::getNameFunctionSystem)  // Возвращаем имена функций
                .collect(Collectors.toList());
    }

    @GetMapping("/departments")
    @ResponseBody
    public List<String> getPositionsByDepartment(@RequestParam String organizationShortName) {
        Organization organization = organizationRepository.findByLittleNameOrganization(organizationShortName)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Получаем список отделов для выбранной организации
        List<String> departmentNames = departmentRepository.findByOrganizationId(organization).stream()
                .map(Department::getNameDepartment)
                .collect(Collectors.toList());

        return departmentNames;
    }

    @GetMapping("/positions")
    @ResponseBody
    public List<String> getPositionsByDepartment(@RequestParam String departmentName, @RequestParam String organizationShortName) {
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

    @GetMapping("/functionsBySystemAndContract")
    @ResponseBody
    public List<String> getFunctionsBySystemAndContract(@RequestParam String systemShortName, @RequestParam String contractNumber) {
        System system = systemRepository.findByLittleNameSystem(systemShortName).get();

        // Найти контракт по номеру контракта
        Contract contract = contractRepository.findByNumberContract(contractNumber)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Получаем функции для выбранной системы и контракта
        List<FunctionSystem> functionSystems = functionSystemRepositry.findBySystemIdAndContractId(system, contract);

        return functionSystems.stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());
    }


}
