package TicketManager.systems.virtualmachines.controller;

import TicketManager.systems.virtualmachines.model.VirtualMachine;
import TicketManager.systems.virtualmachines.repository.VirtualMachineRepository;
import TicketManager.user.model.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/system/function/machine")
public class VirtualMachineController {
    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    @GetMapping//вместо blog-main
    public String machines(Model model, @AuthenticationPrincipal MyUser user) {
        List<VirtualMachine> machines = virtualMachineRepository.findAll();
        model.addAttribute("machines", machines);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "machines";
    }


    @GetMapping("/add")
    public String machineAdd(Model model, @AuthenticationPrincipal MyUser user) {
        List<VirtualMachine> machines = virtualMachineRepository.findAll();
        model.addAttribute("machines", machines);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", user); // Добавляем текущего пользователя в модель
        return "machines_add";
    }


    @PostMapping("/add")
    public String managerMachineAdd(
            @RequestParam String nameVM,
            @RequestParam String nameDNS,
            @RequestParam String machineIp,
            Model model, @AuthenticationPrincipal MyUser users) {

        if (virtualMachineRepository.findByNameVM(nameVM).isPresent()) {
            model.addAttribute("error", "Виртуальная машина с таким именем уже существует");
            return "machines_add"; // вернуть на страницу регистрации с ошибкой
        }

        if (virtualMachineRepository.findByNameDNS(nameDNS).isPresent()) {
            model.addAttribute("error", "Виртуальная машина с таким DNS именем уже существует");
            return "machines_add"; // вернуть на страницу регистрации с ошибкой
        }

        if (virtualMachineRepository.findByNameVM(machineIp).isPresent()) {
            model.addAttribute("error", "Виртуальная машина с таким IP уже существует");
            return "machines_add"; // вернуть на страницу регистрации с ошибкой
        }

        // Создание новой организации
        VirtualMachine virtualMachine = new VirtualMachine(nameVM, nameDNS, machineIp);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель

        virtualMachineRepository.save(virtualMachine);  // Сохранение пользователя в базе данных

        return "redirect:/system/function/machine";
    }


    @GetMapping("/{id}")//вместо blog-main
    public String machineDetails(@PathVariable(value = "id") Long id, Model model, @AuthenticationPrincipal MyUser users) {
        if (!virtualMachineRepository.existsById(id)) {
            return "redirect:/system/function/machine";
        }
        Optional<VirtualMachine> virtualMachine = virtualMachineRepository.findById(id);
        ArrayList<VirtualMachine> result = new ArrayList<>();
        virtualMachine.ifPresent(result::add);
        model.addAttribute("virtualMachine", result);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "machines_details";
    }

    @PostMapping("/{id}/remove")//вместо blog-main
    public String managerMachineDelete(
            @PathVariable(value = "id") Long id,
            Model model, @AuthenticationPrincipal MyUser users) {
        VirtualMachine virtualMachine = virtualMachineRepository.findById(id).orElseThrow();
        virtualMachineRepository.delete(virtualMachine);
        model.addAttribute("title", "Главная страница");
        model.addAttribute("currentUser", users); // Добавляем текущего пользователя в модель
        return "redirect:/system/function/machine";
    }
}
