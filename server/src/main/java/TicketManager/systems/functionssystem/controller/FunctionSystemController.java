package TicketManager.systems.functionssystem.controller;

import TicketManager.contract.model.Contract;
import TicketManager.contract.repository.ContractRepository;
import TicketManager.systems.functionssystem.model.FunctionSystem;
import TicketManager.systems.functionssystem.repository.FunctionSystemRepositry;
import TicketManager.systems.model.System;
import TicketManager.systems.repository.SystemRepository;
import TicketManager.systems.virtualmachines.model.VirtualMachine;
import TicketManager.systems.virtualmachines.repository.VirtualMachineRepository;
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
@RequestMapping("/system/function")
public class FunctionSystemController {
    @Autowired
    private FunctionSystemRepositry functionSystemRepositry;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    public System getSystem (String name) {
        System system = systemRepository.findByLittleNameSystem(name).get();
        return system;
    }

    @Transactional(readOnly = true)
    @GetMapping("/system/{systemId}")//вместо blog-main
    public String functionsSystem(@PathVariable(value = "systemId") Long systemId,
                                  Model model, @AuthenticationPrincipal MyUser user) {
        List<FunctionSystem> functions = functionSystemRepositry.findAllFunctionSystems(systemId);
        System system = systemRepository.findById(systemId).get();

        model.addAttribute("system", system);
        model.addAttribute("functions", functions);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "functions_systems";
    }

    @GetMapping("/all")//вместо blog-main
    public String aLLfunctions(Model model, @AuthenticationPrincipal MyUser user) {
        List<FunctionSystem> functions = functionSystemRepositry.findAll();
        model.addAttribute("functions", functions);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "functions";
    }

    @GetMapping("/add")
    public String functionAdd(Model model, @AuthenticationPrincipal MyUser user) {
        // Список кратких наименований систем
        List<String> systemShortLittleNameSystem = systemRepository.findAll().stream()
                .map(System::getLittleNameSystem)
                .collect(Collectors.toList());

        // Получаем все контракты
        List<String> contractShortNames = contractRepository.findAll().stream()
                .map(Contract::getNumberContract)
                .collect(Collectors.toList());

        // Получаем все IP адреса виртуальных машин
        List<String> machineShortNames = virtualMachineRepository.findAll().stream()
                .map(VirtualMachine::getMachineIp)
                .collect(Collectors.toList());

        model.addAttribute("systemShortLittleNameSystem", systemShortLittleNameSystem);
        model.addAttribute("contractShortNames", contractShortNames);
        model.addAttribute("machineShortNames", machineShortNames);
        model.addAttribute("title", "Добавление функции системы");
        model.addAttribute("currentUser", user);

        return "functions_add";
    }

    @PostMapping("/add")
    public String functionAddSubmit(@RequestParam String littleNameSystem,
                                    @RequestParam String nameFunctionSystem,
                                    @RequestParam String description,
                                    @RequestParam String contractId,
                                    @RequestParam String ipVirtualMachine,
                                    @RequestParam String port,
                                    @RequestParam(required = false) String functionAccept,  // Изменено на String
                                    @AuthenticationPrincipal MyUser user,
                                    Model model) {

        // Найдем систему по littleNameSystem
        System system = systemRepository.findByLittleNameSystem(littleNameSystem)
                .orElseThrow(() -> new RuntimeException("System not found"));

        // Найдем контракт по номеру
        Contract contract = contractRepository.findByNumberContract(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Найдем виртуальную машину по IP
        VirtualMachine virtualMachine1 = virtualMachineRepository.findByMachineIp(ipVirtualMachine)
                .orElseThrow(() -> new RuntimeException("VirtualMachine not found"));

        // Проверка на существование функции с таким же именем в данной системе
        boolean functionExists = functionSystemRepositry.findAll().stream()
                .anyMatch(function -> function.getSystemId().equals(system) && function.getNameFunctionSystem().equals(nameFunctionSystem));

        if (functionExists) {
            model.addAttribute("errorMessage", "Функция с таким названием уже существует в выбранной системе.");
            model.addAttribute("systemShortNames", systemRepository.findAll().stream()
                    .map(System::getLittleNameSystem)
                    .collect(Collectors.toList()));
            model.addAttribute("littleNameSystem", littleNameSystem);
            model.addAttribute("nameFunctionSystem", nameFunctionSystem);
            model.addAttribute("contractId", contractId);
            model.addAttribute("ipVirtualMachine", ipVirtualMachine);
            model.addAttribute("port", port);
            model.addAttribute("functionAccept", functionAccept);

            return "functions_add"; // Возвращаем ту же форму с ошибкой
        }

        // Конвертируем строку functionAccept в Boolean
        Boolean parsedFunctionAccept = null;
        if (functionAccept != null) {
            if (functionAccept.equals("true")) {
                parsedFunctionAccept = true;
            } else if (functionAccept.equals("false")) {
                parsedFunctionAccept = false;
            }
        }

        // Создание объекта FunctionSystem
        FunctionSystem functionSystem;
        if (parsedFunctionAccept == null) {
            functionSystem = new FunctionSystem(system, nameFunctionSystem, description, contract, virtualMachine1, port);
        } else {
            functionSystem = new FunctionSystem(system, nameFunctionSystem, description, contract, virtualMachine1, port, parsedFunctionAccept);
        }

        // Сохраняем в базу данных
        functionSystemRepositry.save(functionSystem);

        return "redirect:/system/function/all"; // Перенаправление на страницу со списком функций
    }

    @Transactional(readOnly = true)
    @GetMapping("/{functionId}")
    public String functionDetails(@PathVariable(value = "functionId") Long functionId, Model model,
                                  @AuthenticationPrincipal MyUser users) {
        if (!functionSystemRepositry.existsById(functionId)) {
            return "redirect:/system/function/all";
        }
        Optional<FunctionSystem> functionSystem = functionSystemRepositry.findById(functionId);

        ArrayList<FunctionSystem> result = new ArrayList<>();
        functionSystem.ifPresent(result::add);

        model.addAttribute("functionSystem", result);
        model.addAttribute("title", "Детали функции системы");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "functionSystem_details";
    }


    @PostMapping("/{functionId}/remove")
    public String managerFunctionDelete(@PathVariable(value = "functionId") Long functionId,
                                        Model model, @AuthenticationPrincipal MyUser users) {
        FunctionSystem functionSystem = functionSystemRepositry.findById(functionId).orElseThrow();
        functionSystemRepositry.delete(functionSystem);
        model.addAttribute("title", "Функции системы");
        model.addAttribute("currentUser", users);
        return "redirect:/system/function/all";
    }

    /*@GetMapping("/allfunctionssystem")
    private List<String> getAllFunctionPosition(@RequestParam String systemShortName) {

        System system = systemRepository.findByLittleNameSystem(systemShortName).get();
        // Получаем все функции для позиции пользователя

        List<String> functionSystemShortName = functionSystemRepositry.findBySystemId(system).stream()
                .map(FunctionSystem::getNameFunctionSystem)
                .collect(Collectors.toList());
        return functionSystemShortName;
    }*/
}