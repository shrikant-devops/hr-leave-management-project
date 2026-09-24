package com.hrms.leave.controller;

import com.hrms.leave.service.LeaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hr")
public class HrController {

    private final LeaveService leaveService;

    public HrController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("requests", leaveService.allRequests());
        model.addAttribute("balances", leaveService.allBalances(leaveService.currentYear()));
        return "hr/reports";
    }
}
