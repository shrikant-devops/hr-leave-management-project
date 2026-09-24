package com.hrms.leave.config;

import com.hrms.leave.entity.*;
import com.hrms.leave.repository.LeaveBalanceRepository;
import com.hrms.leave.repository.LeaveTypeRepository;
import com.hrms.leave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    public DataSeeder(UserRepository userRepository, LeaveTypeRepository leaveTypeRepository,
                       LeaveBalanceRepository leaveBalanceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedDemoData || userRepository.count() > 0) {
            return;
        }

        LeaveType casual = leaveTypeRepository.save(new LeaveType("Casual Leave", 12));
        LeaveType sick = leaveTypeRepository.save(new LeaveType("Sick Leave", 10));
        LeaveType earned = leaveTypeRepository.save(new LeaveType("Earned Leave", 15));

        User hr = new User("Hina Roy", "hr@hrms.local", passwordEncoder.encode("password123"),
                Role.HR, "Human Resources", "HR Manager", LocalDate.of(2018, 1, 10), null);
        hr = userRepository.save(hr);

        User manager = new User("Mark Chen", "manager@hrms.local", passwordEncoder.encode("password123"),
                Role.MANAGER, "Engineering", "Engineering Manager", LocalDate.of(2019, 3, 4), null);
        manager = userRepository.save(manager);

        User emp1 = new User("Alice Kumar", "alice@hrms.local", passwordEncoder.encode("password123"),
                Role.EMPLOYEE, "Engineering", "Software Engineer", LocalDate.of(2021, 6, 1), manager);
        User emp2 = new User("Bob Singh", "bob@hrms.local", passwordEncoder.encode("password123"),
                Role.EMPLOYEE, "Engineering", "Software Engineer", LocalDate.of(2022, 2, 15), manager);
        emp1 = userRepository.save(emp1);
        emp2 = userRepository.save(emp2);

        int year = Year.now().getValue();
        for (User u : new User[]{hr, manager, emp1, emp2}) {
            leaveBalanceRepository.save(new LeaveBalance(u, casual, year, casual.getDefaultDaysPerYear(), 0));
            leaveBalanceRepository.save(new LeaveBalance(u, sick, year, sick.getDefaultDaysPerYear(), 0));
            leaveBalanceRepository.save(new LeaveBalance(u, earned, year, earned.getDefaultDaysPerYear(), 0));
        }
    }
}
