package com.hrms.leave.controller;

import com.hrms.leave.config.UserPrincipal;
import com.hrms.leave.entity.User;
import com.hrms.leave.service.LeaveService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final LeaveService leaveService;

    public ManagerController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/requests")
    public String pendingRequests(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User manager = principal.getUser();
        model.addAttribute("pending", leaveService.pendingForManager(manager.getId()));
        model.addAttribute("teamHistory", leaveService.allForManagerTeam(manager.getId()));
        model.addAttribute("team", leaveService.teamOf(manager.getId()));
        return "manager/requests";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id,
                           @RequestParam(required = false) String comment, Model model) {
        String error = leaveService.decide(id, principal.getUser(), true, comment);
        return redirectWithMessage(error);
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id,
                          @RequestParam(required = false) String comment, Model model) {
        String error = leaveService.decide(id, principal.getUser(), false, comment);
        return redirectWithMessage(error);
    }

    private String redirectWithMessage(String error) {
        if (error != null) {
            return "redirect:/manager/requests?error=" + java.net.URLEncoder.encode(error, java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/manager/requests";
    }
}
