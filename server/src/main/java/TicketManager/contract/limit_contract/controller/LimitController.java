package TicketManager.contract.limit_contract.controller;

import TicketManager.contract.limit_contract.model.Limit;
import TicketManager.contract.limit_contract.repository.LimitRepository;
import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.contract.technicalspecification.model.TechnicalSpecification;
import TicketManager.organization.department.model.Department;
import TicketManager.organization.model.Organization;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.model.System;
import TicketManager.user.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("contract/{contractId}/limit")
public class LimitController {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private LimitRepository limitRepository;

    private String formatDurationForList(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" дней ");
        }
        if (hours > 0) {
            result.append(hours).append(" часов ");
        }
        if (minutes > 0) {
            result.append(minutes).append(" минут");
        }

        return result.toString().trim();
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        return String.format("Дни: %d, Часы: %02d, Минуты: %02d", days, hours, minutes);  // Новый формат
    }

    @GetMapping
    public String limits(@PathVariable(value = "contractId") Long contractId, Model model, @AuthenticationPrincipal MyUser user) {
        List<Limit> limits = limitRepository.findAllLimitContract(contractId);
        Contract contract = contractRepository.findById(contractId).get();

        for (Limit limit : limits) {
            String formattedDuration = formatDuration(limit.getPenaltysStart());
            limit.setFormattedDuration(formattedDuration); // Добавим отформатированную строку в объект Limit
        }

        model.addAttribute("limits", limits);
        model.addAttribute("contract", contract);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "limits";
    }

    @GetMapping("/add")
    public String limitAdd(@PathVariable(value = "contractId") Long contractId,
                           Model model, @AuthenticationPrincipal MyUser user) {
        // Получаем список всех Duration и форматируем их
        List<String> formattedDurations = limitRepository.findAll().stream()
                .map(penaltysStart -> formatDuration(penaltysStart.getPenaltysStart()))
                .collect(Collectors.toList());

        List<Contract> contracts = contractRepository.findAll();
        List<Limit> limits = limitRepository.findAllLimitContract(contractId);

        model.addAttribute("contracts", contracts);
        model.addAttribute("limits", limits);

        model.addAttribute("formattedDurations", formattedDurations);  // Список строковых представлений Duration

        model.addAttribute("title", "Добавление должности");
        model.addAttribute("currentUser", user);

        return "limits_add";
    }

    @PostMapping("/add")
    public String managerStartPenaltyAdd(
            @PathVariable(value = "contractId") Long contractId,
            @RequestParam String levelName,
            @RequestParam String categoryName,
            @RequestParam Double penaltyPrice,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) Integer hours,
            @RequestParam(required = false) Integer minutes,
            Model model, @AuthenticationPrincipal MyUser users) {

        // Если поля не пустые, создаем Duration
        long totalSeconds = 0;

        if (days != null) totalSeconds += days * 24 * 60 * 60;
        if (hours != null) totalSeconds += hours * 60 * 60;
        if (minutes != null) totalSeconds += minutes * 60;

        Duration duration = Duration.ofSeconds(totalSeconds);

        Optional<Contract> contractOpt = contractRepository.findById(contractId);

        if (!contractOpt.isPresent()) {
            model.addAttribute("error", "Такого контракта не существует");
            return "limits_add";
        }

        Contract contract = contractOpt.get();

        // Проверка, если такой период уже существует для данного уровня и контракта
        List<Limit> existingLimits = limitRepository.findByLevelNameAndContractNumberAndPenaltyStart(levelName, contract.getNumberContract(), duration);

        if (!existingLimits.isEmpty()) {
            model.addAttribute("error", "Такой период уже существует");
            return "startPenaltys_add"; // вернуть на страницу регистрации с ошибкой
        }

        Optional<Limit> limit = limitRepository
                .findByLevelNameAndContractId(levelName, contractId);

        if (limit.isPresent()) {
            model.addAttribute("error", "Такое ограничение уже существует в данном контракте");
            return "limits_add";
        }

        Limit newLimit = new Limit(contract, levelName, categoryName, penaltyPrice, duration);

        limitRepository.save(newLimit);  // Сохранение нового лимита в базе данных

        return "redirect:/contract/{contractId}/limit";
    }

    @Transactional(readOnly = true)
    @GetMapping("/{limitId}")//вместо blog-main
    public String limitDetails(@PathVariable(value = "limitId") Long limitId,
                               @PathVariable(value = "contractId") Long contractId,
                               Model model,
                               @AuthenticationPrincipal MyUser users) {
        if (!limitRepository.existsById(limitId)) {
            return "redirect:/contract/{contractId}/limit";
        }

        Optional<Limit> limit = limitRepository.findById(limitId);
        ArrayList<Limit> result = new ArrayList<>();
        limit.ifPresent(result::add);
        model.addAttribute("limit", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "limit_details";
    }

    @PostMapping("/{limitId}/remove")//вместо blog-main
    public String managerLimitDelete(
            @PathVariable(value = "limitId") Long limitId,
            Model model, @AuthenticationPrincipal MyUser users) {
        Limit limit = limitRepository.findById(limitId).orElseThrow();
        limitRepository.delete(limit);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/contract/{contractId}/limit";
    }

}
