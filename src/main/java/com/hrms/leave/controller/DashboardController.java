package com.hrms.leave.controller;

import com.hrms.leave.config.UserPrincipal;
import com.hrms.leave.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User user = principal.getUser();
        model.addAttribute("user", user);
        return "dashboard";
    }
}
