package TicketManager.contract.controller;

import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.contract.technicalspecification.model.TechnicalSpecification;
import TicketManager.contract.technicalspecification.repository.TechnicalSpecificationRepository;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    private TechnicalSpecificationRepository technicalSpecificationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private MyUserRepository myUserRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping//вместо blog-main
    public String contracts(Model model, @AuthenticationPrincipal MyUser user) {
        List<Contract> contracts = contractRepository.findAll();
        model.addAttribute("contracts", contracts);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "contracts";
    }

    @GetMapping("/add")
    public String contractAdd(Model model, @AuthenticationPrincipal MyUser user) {

        // Получаем все краткие наименования организаций
        List<String> organizationShortNames = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());

        List<String> specificationShortNames = technicalSpecificationRepository.findAll().stream()
                .map(TechnicalSpecification::getNameSpecification)
                .collect(Collectors.toList());

        List<String> userShortNames = myUserRepository.findAll().stream()
                .map(MyUser::getLogin)
                .collect(Collectors.toList());

        model.addAttribute("organizationShortNames", organizationShortNames); // Передаем в модель
        model.addAttribute("specificationShortNames", specificationShortNames); // Передаем в модель
        model.addAttribute("userShortNames", userShortNames); // Передаем в модель



        model.addAttribute("contracts", contractRepository.findAll());
        model.addAttribute("title", "Добавление контракта");
        model.addAttribute("currentUser", user);

        return "contracts_add";
    }

    @PostMapping("/add")
    public String contractAddSubmit(@RequestParam String nameContract,
                                    @RequestParam String numberContract,
                                    @RequestParam String type,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateOpenContract,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateCloseContract,
                                    @RequestParam Double priceContract,
                                    @RequestParam(required = false) String technicalSpecification,
                                    @RequestParam String organizationCustomerId,
                                    @RequestParam String userCustomerId,
                                    @RequestParam String organizationPerfomerId,
                                    @RequestParam String userPerfomerId,
                                    @RequestParam String organizationExecutionId,
                                    @RequestParam String userExecutionId,
                                    @AuthenticationPrincipal MyUser user,
                                    Model model) {

        // Получаем организации и ТЗ по их ID (или по краткому наименованию)
        Organization organizationCustomer = organizationRepository.findByLittleNameOrganization(organizationCustomerId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Organization organizationPerfomer = organizationRepository.findByLittleNameOrganization(organizationPerfomerId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Organization organizationExecution = organizationRepository.findByLittleNameOrganization(organizationExecutionId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        TechnicalSpecification technicalSpecification1 = (technicalSpecification != null && !technicalSpecification.isEmpty())
                ? technicalSpecificationRepository.findByNameSpecification(technicalSpecification)
                .orElseThrow(() -> new RuntimeException("TechnicalSpecification not found"))
                : null;

        MyUser userCustomer = myUserRepository.findByLogin(userCustomerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MyUser userPerfomer = myUserRepository.findByLogin(userPerfomerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MyUser userExecution = myUserRepository.findByLogin(userExecutionId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (contractRepository.findByNumberContract(numberContract).isPresent()) {
            model.addAttribute("error", "Контракт с таким номером уже существует");
            return "contracts_add"; // вернуть на страницу регистрации с ошибкой
        }

        /*String nameContract, String numberContract, String type, LocalDateTime dateOpenContract,
                    LocalDateTime dateCloseContract, Double priceContract, TechnicalSpecification technicalSpecification,
                    String organizationCustomer, MyUser userCustomer, String organizationPerfomer,
                    MyUser userPerfomer, String organizationExecution, MyUser userExecution*/
        // Создаем новый контракт
        Contract newContract = new Contract(nameContract, numberContract, type, dateOpenContract, dateCloseContract, priceContract,
                technicalSpecification1, organizationCustomer.getLittleNameOrganization(),
                userCustomer, organizationPerfomer.getLittleNameOrganization(),
                userPerfomer, organizationExecution.getLittleNameOrganization(),
                userExecution);

        // Сохраняем новый контракт
        contractRepository.save(newContract);

        return "redirect:/contract"; // Перенаправление на главную страницу должностей
    }

    @GetMapping("/{contractId}")//вместо blog-main
    public String contractDetails(@PathVariable(value = "contractId") Long contractId,
                                  Model model, @AuthenticationPrincipal MyUser users) {
        if (!contractRepository.existsById(contractId)) {
            return "redirect:/contract";
        }
        Optional<Contract> contract = contractRepository.findById(contractId);
        ArrayList<Contract> result = new ArrayList<>();
        contract.ifPresent(result::add);
        model.addAttribute("contract", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "contract_details";
    }

    @PostMapping("/{contractId}/remove")//вместо blog-main
    public String managerContractDelete(
            @PathVariable(value = "contractId") Long contractId,
            Model model, @AuthenticationPrincipal MyUser users) {
        Contract contract = contractRepository.findById(contractId).orElseThrow();
        contractRepository.delete(contract);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/contract";
    }
}