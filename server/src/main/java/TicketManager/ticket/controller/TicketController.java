package TicketManager.ticket.controller;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.limit_contract.repository.LimitRepository;
import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.exception.NotFoundException;
import TicketManager.exception.ValidationException;
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
import TicketManager.ticket.recomendations.work.model.RecomendationsWork;
import TicketManager.ticket.recomendations.work.repository.RecomendationsWorkRepository;
import TicketManager.ticket.repository.TicketRepository;
import TicketManager.user.model.MyUser;
import TicketManager.user.repository.MyUserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import java.text.DecimalFormat;
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

    @Autowired
    private RecomendationsWorkRepository recomendationsWorkRepository;

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
    @GetMapping("/expired")
    public String getExpiredTickets(Model model) {
        List<Ticket> expiredTickets = ticketRepository.findByIsExpiredTrue(); // Получаем все просроченные заявки

        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Ticket ticket : expiredTickets) {
            if (ticket.getLimitsId() != null && ticket.getLimitsId().getPenaltysStart() != null) {
                Duration penaltyStart = ticket.getLimitsId().getPenaltysStart();
                Double penaltyPrice = ticket.getLimitsId().getPenaltyPrice();
                Double priceContract = ticket.getContractId().getPriceContract();
                Double fine = 0.0;

                if (ticket.getDateClose() != null) {
                    long daysLate = Duration.between(ticket.getDateCreate(), ticket.getDateClose()).toDays();
                    // Если просрочка меньше 1 дня, считаем штраф за 1 день
                    if (daysLate > penaltyStart.toDays()) {
                        fine = Math.max(1, daysLate) * penaltyPrice * priceContract;
                    } else {
                        fine = penaltyPrice * priceContract;  // если просрочка меньше 1 дня, штраф как за 1 день
                    }
                } else {
                    long daysLate = Duration.between(ticket.getDateCreate(), currentDateTime).toDays();
                    // Если просрочка меньше 1 дня, считаем штраф за 1 день
                    if (daysLate > penaltyStart.toDays()) {
                        fine = Math.max(1, daysLate) * penaltyPrice * priceContract;
                    } else {
                        fine = penaltyPrice * priceContract;  // если просрочка меньше 1 дня, штраф как за 1 день
                    }
                }

                // Округляем штраф до 2 знаков после запятой
                ticket.setPenaltyAmount(String.format("%.2f", fine));
            } else {
                log.warn("Пропущена заявка ID=" + ticket.getTicketId() + ": данные о лимите отсутствуют.");
            }
        }


        model.addAttribute("tickets", expiredTickets); // Добавляем их в модель
        model.addAttribute("title", "Просроченные заявки");
        return "tickets_expired"; // Возвращаем представление для отображения
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
            if (ticket.getIsExpired() != null && ticket.getIsExpired()) {
                // Если заявка просрочена, рассчитываем штраф
                if (ticket.getLimitsId() != null && ticket.getLimitsId().getPenaltysStart() != null) {
                    Duration penaltyStart = ticket.getLimitsId().getPenaltysStart();
                    Double penaltyPrice = ticket.getLimitsId().getPenaltyPrice();
                    Double priceContract = ticket.getContractId().getPriceContract();
                    Double fine = 0.0;

                    // Проверяем, просрочена ли заявка
                    if (ticket.getDateClose() != null) {
                        long daysLate = Duration.between(ticket.getDateCreate(), ticket.getDateClose()).toDays();
                        // Если просрочка меньше 1 дня, считаем штраф за 1 день
                        if (daysLate > penaltyStart.toDays()) {
                            fine = Math.max(1, daysLate) * penaltyPrice * priceContract;
                        } else {
                            fine = penaltyPrice * priceContract;  // если просрочка меньше 1 дня, штраф как за 1 день
                        }
                    } else {
                        long daysLate = Duration.between(ticket.getDateCreate(), currentDateTime).toDays();
                        // Если просрочка меньше 1 дня, считаем штраф за 1 день
                        if (daysLate > penaltyStart.toDays()) {
                            fine = Math.max(1, daysLate) * penaltyPrice * priceContract;
                        } else {
                            fine = penaltyPrice * priceContract;  // если просрочка меньше 1 дня, штраф как за 1 день
                        }
                    }

                    // Округляем штраф до 2 знаков после запятой
                    ticket.setPenaltyAmount(String.format("%.2f", fine));
                } else {
                    log.warn("Пропущена заявка ID=" + ticket.getTicketId() + ": данные о лимите отсутствуют.");
                }
            } else {
                // Если заявка не просрочена, штраф равен 0
                ticket.setPenaltyAmount("0.00");
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

    /*@Transactional
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
    }*/


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
        if (comments == null) {
            comments = new ArrayList<>();
        }

        // Логика для рекомендаций
        String recommendation = "";
        Ticket t = ticket.get();
        if (!t.getIsExpired()) {
            recommendation = "Нарушений не выявлено";
        } else if (t.getStatus() == Status.OPEN) {
            recommendation = "Необходимо составить служебную записку на имя заместителя директора о нарушении сроков исполнения заявки";
        } else if (t.getStatus() == Status.CLOSED) {
            recommendation = "Необходимо составить письмо в СПб ГКУ 'УИТС' за подписью заместителя директора о нарушении сроков исполнения заявки";
        }

        // Добавляем рекомендацию в модель
        model.addAttribute("recommendation", recommendation);
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
        return "redirect:/ticket/{ticketId}";
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
            new ValidationException("Заявка уже закрыта");
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


    @Transactional
    @PostMapping("/{ticketId}/addRecommendation")
    public String addRecommendation(@PathVariable(value = "ticketId") Long ticketId,
                                    @RequestParam("recomendationWork") String recomendationWork,
                                    Model model, @AuthenticationPrincipal MyUser users) {
        // Получаем заявку из базы
        // Получаем заявку из базы
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Заявка не найдена"));

        // Создаем новый объект рекомендации
        RecomendationsWork recommendations = new RecomendationsWork(ticket, recomendationWork);

        // Сохраняем рекомендацию в базу данных
        recomendationsWorkRepository.save(recommendations);

        // Создаем BindingResult вручную для валидации
        // Вам нужно создать объект BindingResult, связанный с Ticket, вручную.
        BindingResult bindingResult = new BeanPropertyBindingResult(ticket, "ticket");

        ticketClose(ticket.getTicketId(), ticket, bindingResult, model,users);


        // Обновляем страницу с заявкой
        model.addAttribute("ticket", ticket);
        model.addAttribute("recommendation", recomendationWork); // Отображаем сохраненную рекомендацию

        return "redirect:/ticket"; // Перенаправляем на страницу заявок
    }



}