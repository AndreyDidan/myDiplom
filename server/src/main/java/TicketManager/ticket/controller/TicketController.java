package TicketManager.ticket.controller;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.limit_contract.repository.LimitRepository;
import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.exception.NotFoundException;
import TicketManager.organization.model.Organization;
import TicketManager.organization.repository.OrganizationRepository;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.functionssystem.repository.FunctionSystemRepositry;
import TicketManager.systems.model.System;
import TicketManager.systems.repository.SystemRepository;
import TicketManager.ticket.comment.model.CommentsTickets;
import TicketManager.ticket.comment.repository.CommentRepository;
import TicketManager.ticket.model.Status;
import TicketManager.ticket.model.Ticket;
import TicketManager.ticket.problem.model.Problem;
import TicketManager.ticket.problem.repository.ProblemRepository;
import TicketManager.ticket.repository.TicketRepository;
import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private FunctionSystemRepositry functionSystemRepositry;
    @Autowired
    private LimitRepository limitRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SystemRepository systemRepository;

    @GetMapping("/limitcontract")
    @ResponseBody
    public List<String> getLimitsBySContract(@RequestParam String contractsShortNames) {
        log.info("Попытка найти ограничения контракта " + "'" + contractsShortNames + "'");
        Optional<Contract> contractOpt = contractRepository.findByNumberContract(contractsShortNames);
        if (!contractOpt.isPresent()) {
            return Collections.emptyList(); // Если контракт не найден, возвращаем пустой список
        }
        Contract contract = contractOpt.get();
        // Получаем все ограничения для этого контракта
        List<Limit> limits = limitRepository.findByContractId(contract);
        List<String> limitContractNames = limits.stream()
                .map(Limit::getLevelName)
                .collect(Collectors.toList());
        log.info("Передача ограничений: " + limitContractNames);
        return limitContractNames; // Возвращаем список строк
    }

    @GetMapping("/allcontractfunctions")
    @ResponseBody
    public List<String> getContractsBySFunction(@RequestParam String nameFunctionSystem) {
        log.info("Попытка найти функции с названием: " + "'" + nameFunctionSystem + "'");
        // Получаем все функции системы FunctionSystem, у которых имя функции совпадает с переданным
        List<FunctionSystem> functionSystems = functionSystemRepositry.findByNameFunctionSystemIn(List.of(nameFunctionSystem));
        // Извлекаем из каждой FunctionSystem контракт и собираем их номера
        List<String> contractNumbers = functionSystems.stream()
                .map(functionSystem -> functionSystem.getContractId().getNumberContract()) // получаем номер контракта
                .distinct() // уникальные номера
                .collect(Collectors.toList());
        log.info("Передача номеров контрактов: " + contractNumbers);
        return contractNumbers; // Возвращаем список номеров контрактов
    }

    @Transactional(readOnly = true)
    @GetMapping
    public String ticket(Model model, @AuthenticationPrincipal MyUser user) {
        // Получаем все заявки
        List<Ticket> tickets = ticketRepository.findAll(Sort.by(Sort.Order.asc("ticketId")));
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (Ticket ticket : tickets) {
            if (ticket.getLimitsId() != null && ticket.getLimitsId().getPenaltysStart() != null) {
                Duration penaltyStart = ticket.getLimitsId().getPenaltysStart();
                // Проверяем: заявка закрылась или нет, и сравниваем с лимитом штрафного времени
                boolean isExpiredNow = false;
                if (ticket.getDateClose() != null) {
                    // Если заявка закрыта, сравниваем дату закрытия с лимитом
                    isExpiredNow = Duration.between(ticket.getDateCreate(), ticket.getDateClose()).compareTo(penaltyStart) > 0;
                } else {
                    // Если заявка не закрыта, сравниваем текущее время с лимитом
                    isExpiredNow = Duration.between(ticket.getDateCreate(), currentDateTime).compareTo(penaltyStart) > 0;
                }
                // Обновляем статус просрочки, если он изменился
                if (!Objects.equals(ticket.getIsExpired(), isExpiredNow)) {
                    ticket.setIsExpired(isExpiredNow);
                    ticketRepository.save(ticket); // Фиксируем изменения
                }
            } else {
                log.warn("Пропущена заявка ID=" + ticket.getTicketId() + ": данные о лимите отсутствуют.");
            }
        }
        // Повторно получаем обновлённый список заявок для отображения в модели
        tickets = ticketRepository.findAll(Sort.by(Sort.Order.asc("ticketId")));
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user);
        return "tickets";
    }

    @Transactional(readOnly = true)
    @GetMapping("/add")
    public String ticketAdd(Model model, @AuthenticationPrincipal MyUser user) {

        List<String> systemShortNames = systemRepository.findAll().stream()
                .map(System::getLittleNameSystem)
                .collect(Collectors.toList());
        List<String> functionSystemShortName = functionSystemRepositry.findAll().stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());
        List<String> contractsShortNames = contractRepository.findAll().stream()
                .map(Contract::getNumberContract)
                .collect(Collectors.toList());
        List<String> problemShortNames = problemRepository.findAll().stream()
                .map(Problem::getLitleDescriptionProblem)
                .collect(Collectors.toList());
        List<String> organizationShortName = organizationRepository.findAll().stream()
                .map(Organization::getLittleNameOrganization)
                .collect(Collectors.toList());
        List<String> levelsShortNames = limitRepository.findAll().stream()
                .map(Limit::getLevelName)
                .collect(Collectors.toList());
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("systemShortNames", systemShortNames);
        model.addAttribute("functionSystemShortName", functionSystemShortName);
        model.addAttribute("problemShortNames", problemShortNames);
        model.addAttribute("contractsShortNames", contractsShortNames);
        model.addAttribute("organizationShortName", organizationShortName);
        model.addAttribute("levelsShortNames", levelsShortNames);
        model.addAttribute("tickets", tickets);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "tickets_add";
    }

    @Transactional
    @PostMapping("/add")
    public String ManagerTicketAdd(
            @AuthenticationPrincipal MyUser user,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm") LocalDateTime dateCreatedProblem,
            @RequestParam(required = false) String problemFunctionId,
            @RequestParam String litleDescriptionProblem,
            @RequestParam String descriptionProblem,
            @RequestParam String nameFunctionSystem,
            @RequestParam String numberContract,
            @RequestParam String limit,
            Model model) {
        if (dateCreatedProblem == null) {
            dateCreatedProblem = LocalDateTime.now();
        }
        FunctionSystem functionSystem = functionSystemRepositry.findByNameFunctionSystem(nameFunctionSystem).
                orElseThrow(() -> new NotFoundException("Функция не добавлена в систему"));
        Optional<Contract> contract1 = contractRepository.findByNumberContract(numberContract);
        Contract contract = contract1.orElseThrow(() -> new NotFoundException("Данный контракт не добавлен в систему"));
        String organization = contract.getOrganizationPerfomer();
        log.info(organization);
        Organization organization1 = organizationRepository.findByLittleNameOrganization(organization).
                orElseThrow(() -> new NotFoundException("Организация не добавлена в систему"));
        Optional<Limit> limitOpt = limitRepository.findByLevelNameAndContractId(limit, contract.getContractId());
        if (!limitOpt.isPresent()) {
            model.addAttribute("error", "Ограничение не найдено");
            return "tickets_add";
        }
        Limit limit1 = limitOpt.get();
        Boolean isExpired = false;
        Ticket ticket;
        Optional<Problem> problemFunction = problemRepository.findByLitleDescriptionProblem(problemFunctionId);
        if (problemFunction.isPresent()) {
            log.info("Если проблема есть");
            ticket = new Ticket(user, dateCreatedProblem, litleDescriptionProblem, descriptionProblem,
                    functionSystem.getNameFunctionSystem(), problemFunction.get(), contract,
                    organization1.getLittleNameOrganization(), limit1, isExpired, Status.OPEN);
        } else {
            log.info("если проблемы нет");
            ticket = new Ticket(user, dateCreatedProblem, litleDescriptionProblem, descriptionProblem,
                    functionSystem.getNameFunctionSystem(), contract,
                    organization1.getLittleNameOrganization(), limit1, isExpired, Status.OPEN);
        }
        log.info("Сохраняем ticket='" + ticket + "'");
        ticketRepository.save(ticket);
        return "redirect:/ticket";
    }

    @Transactional
    @PostMapping("/{ticketId}/close")
    public String ticketClose(
            @PathVariable(value = "ticketId") Long ticketId,
            @Valid Ticket ticket,
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal MyUser users) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки, возвращаем форму с ошибками
            model.addAttribute("title", "Закрытие заявки");
            model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
            return "ticket";
        }
        // Получаем актуальную заявку из базы данных
        Ticket ticket1 = ticketRepository.findById(ticketId).orElseThrow(()
                -> new NotFoundException("Заявка не добавлена в систему"));
        // Если статус уже CLOSED, можно вернуть ошибку или просто ничего не делать
        if (ticket1.getStatus() == Status.CLOSED) {
            model.addAttribute("error", "Заявка уже закрыта");
            return "tickets_details";
        }
        ticket1.setStatus(Status.CLOSED);
        ticket1.setDateClose(LocalDateTime.now());
        // Проверка на просрочку (если есть)
        Boolean bool = false;
        Duration result = Duration.between(ticket1.getDateCreate(), ticket1.getDateClose());
        if (result.equals(ticket1.getLimitsId().getPenaltysStart()) ||
                result.compareTo(ticket1.getLimitsId().getPenaltysStart()) > 0) {
            bool = true;
        } else {
            bool = false;
        }
        ticket1.setIsExpired(bool);
        // Сохраняем обновленный объект
        ticketRepository.save(ticket1);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users);
        return "redirect:/ticket";
    }

    @Transactional(readOnly = true)
    @GetMapping("/{ticketId}")
    public String problemTicketDetails(@PathVariable(value = "ticketId") Long ticketId, Model model,
                                       @AuthenticationPrincipal MyUser users) {
        if (!ticketRepository.existsById(ticketId)) {
            return "redirect:/ticket";
        }
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        ArrayList<Ticket> result = new ArrayList<>();
        ticket.ifPresent(result::add);
        // Получаем все комментарии по тикету, отсортированные по дате (старые к новым)
        List<CommentsTickets> comments = commentRepository.findByTicketIdOrderByCreatedAsc(ticket.get());
        // Если комментариев нет, передаем пустой список
        if (comments == null) {
            comments = new ArrayList<>();
        }
        // Передаем обновленную информацию в модель
        model.addAttribute("ticket", result);
        model.addAttribute("comments", comments);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users);
        return "tickets_details";
    }

    @Transactional
    @PostMapping("/{ticketId}/add")
    public String addComment(
            @AuthenticationPrincipal MyUser user,
            @PathVariable(value = "ticketId") Long ticketId,
            @RequestParam String text,
            Model model) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (!ticketOpt.isPresent()) {
            return "redirect:/ticket"; // Если тикет не найден, редирект на список зпявок
        }
        Ticket ticket = ticketOpt.orElseThrow(() -> new NotFoundException("Заявка не добавлена в систему"));;
        CommentsTickets comment = new CommentsTickets(user, text, ticket, LocalDateTime.now());
        commentRepository.save(comment);
        return "redirect:/ticket/{ticketId}"; // После добавления комментария возвращаемся на страницу заявки
    }
}

    /*@Transactional(readOnly = true)
    @GetMapping("/{id}")//вместо blog-main
    public String problemTicketDetails(@PathVariable(value = "id") Long id, Model model, @AuthenticationPrincipal MyUser users) {
        if (!ticketRepository.existsById(id)) {
            return "redirect:/ticket";
        }
        Optional<Ticket> ticket = ticketRepository.findById(id);
        ArrayList<Ticket> result = new ArrayList<>();
        ticket.ifPresent(result::add);

        // Обновляем статус просрочки заявки
        Ticket ticket1 = result.get(0);
        Boolean expired = ticket1.getExpired();

        // Проверка на просрочку (если есть)
        if (expired == null) {  // если expired = null, обновляем статус
            LocalDateTime localDateTime = LocalDateTime.now();
            Duration resultDuration = Duration.between(ticket1.getDateCreate(), localDateTime);
            expired = resultDuration.compareTo(ticket1.getLimitsId().getPenaltysStartId().getDuration()) > 0;
            ticket1.setExpired(expired);
            ticketRepository.save(ticket1);
        }

        // Передаем обновленную информацию в модель
        model.addAttribute("ticket", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "tickets_details";
    }

    @Transactional
    @PostMapping("/{id}")
    public String ticketDetails(
            @PathVariable(value = "id") Long id,
            @Valid Ticket ticket, // Используйте аннотацию @Valid
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (bindingResult.hasErrors()) {
            // Если есть ошибки, возвращаем форму с ошибками
            model.addAttribute("title", "Закрытие заявки");
            model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
            return "ticket";
        }
        // Получаем актуальную заявку из базы данных
        Ticket ticket1 = ticketRepository.findById(id).orElseThrow();

        LocalDateTime localDateTime = LocalDateTime.now();

        // Проверка на просрочку (если есть)
        Boolean bool = false;
        Duration result = Duration.between(ticket1.getDateCreate(), localDateTime);
        if (result.equals(ticket1.getLimitsId().getPenaltysStartId().getDuration()) ||
                result.compareTo(ticket1.getLimitsId().getPenaltysStartId().getDuration()) > 0) {
            bool = true;
        } else {
            bool = false;
        }

        ticket1.setExpired(bool);

        // Сохраняем обновленный объект
        ticketRepository.save(ticket1);

        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users);
        return "redirect:/ticket/{id}";
    }




    @Transactional
    @PostMapping("/{id}/remove")//вместо blog-main
    public String managerTicketDelete(
            @PathVariable(value = "id") Long id,
            Model model, @AuthenticationPrincipal MyUser users) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();

        // Удалим организацию из базы данных
        ticketRepository.delete(ticket);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/ticket";
    }


    @Transactional
    @PostMapping("/{id}/close")
    public String ticketClose(
            @PathVariable(value = "id") Long id,
            @Valid Ticket ticket, // Используйте аннотацию @Valid
            BindingResult bindingResult,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (bindingResult.hasErrors()) {
            // Если есть ошибки, возвращаем форму с ошибками
            model.addAttribute("title", "Закрытие заявки");
            model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
            return "ticket";
        }

        // Получаем актуальную заявку из базы данных
        Ticket ticket1 = ticketRepository.findById(id).orElseThrow();

        // Если статус уже CLOSED, можно вернуть ошибку или просто ничего не делать
        if (ticket1.getStatus() == Status.CLOSED) {
            model.addAttribute("error", "Заявка уже закрыта");
            return "tickets_details";  // Вы можете вернуть пользователя на страницу с деталями заявки
        }

        ticket1.setStatus(Status.CLOSED);
        ticket1.setDateClose(LocalDateTime.now());

        // Проверка на просрочку (если есть)
        Boolean bool = false;
        Duration result = Duration.between(ticket1.getDateCreate(), ticket1.getDateClose());
        if (result.equals(ticket1.getLimitsId().getPenaltysStartId().getDuration()) ||
                result.compareTo(ticket1.getLimitsId().getPenaltysStartId().getDuration()) > 0) {
            bool = true;
        } else {
            bool = false;
        }

        ticket1.setExpired(bool);

        // Сохраняем обновленный объект
        ticketRepository.save(ticket1);

        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users);
        return "redirect:/ticket";
    }

}*/