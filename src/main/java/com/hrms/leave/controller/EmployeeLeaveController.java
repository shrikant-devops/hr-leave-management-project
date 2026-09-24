package com.hrms.leave.controller;

import com.hrms.leave.config.UserPrincipal;
import com.hrms.leave.entity.User;
import com.hrms.leave.service.LeaveService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/leave")
public class EmployeeLeaveController {

    private final LeaveService leaveService;

    public EmployeeLeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/apply")
    public String applyForm(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("leaveTypes", leaveService.allLeaveTypes());
        return "leave/apply";
    }

    @PostMapping("/apply")
    public String submitApplication(@AuthenticationPrincipal UserPrincipal principal,
                                     @RequestParam Long leaveTypeId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                     @RequestParam(required = false) String reason,
                                     Model model) {
        User employee = principal.getUser();
        String error = leaveService.applyForLeave(employee, leaveTypeId, startDate, endDate, reason);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("leaveTypes", leaveService.allLeaveTypes());
            return "leave/apply";
        }
        return "redirect:/leave/history?applied";
    }

    @GetMapping("/balance")
    public String balance(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User employee = principal.getUser();
        model.addAttribute("balances", leaveService.balancesFor(employee, leaveService.currentYear()));
        return "leave/balance";
    }

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User employee = principal.getUser();
        model.addAttribute("requests", leaveService.historyFor(employee));
        return "leave/history";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id, Model model) {
        leaveService.cancel(id, principal.getUser());
        return "redirect:/leave/history";
    }
}
