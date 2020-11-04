package com.moherdi.fastfood_app.controllers;

import java.util.Map;

import com.moherdi.fastfood_app.DAOs.interfaces.IStaffDAO;
import com.moherdi.fastfood_app.DAOs.interfaces.IUsuarioRepo;
import com.moherdi.fastfood_app.entities.Staff;
import com.moherdi.fastfood_app.entities.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/staff")
public class StaffController {

    @Autowired
    private IStaffDAO repoStaff;

    @Autowired
    private IUsuarioRepo repoUser;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
    public String listarStaff(Model model) {
        model.addAttribute("titulo", "Staff (Personal Registrado)");
        model.addAttribute("username", repoUser.findByNombre(elUsuarioActual()).getNombre());
        model.addAttribute("staffs", repoStaff.findAll());
        return "staff/lista";
    }

    // Usuario
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String requestMethodName(Map<String, Object> map) {
        Staff staff = new Staff();
        map.put("staff", staff);
        return "usuarios/form_staff";
    }

    @PostMapping(value = "/signup")
    public String guardarStaff(Staff staff, @RequestParam(name = "username") String username,
            @RequestParam(name = "contrasenia") String contrasenia) {
        Usuario us = new Usuario();
        us.setNombre(username);
        us.setContrasenia(encoder.encode(contrasenia));
        repoUser.save(us);
        // Guardar en el repositorio
        staff.setUser(us);
        repoStaff.save(staff);
        return "redirect:/staff/list";
    }

    private String elUsuarioActual() {
        String user;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Obtener usuario Logeado
        if (principal instanceof UserDetails) {
            user = ((UserDetails) principal).getUsername();
        } else {
            user = principal.toString();
        }
        return user;
    }
}
