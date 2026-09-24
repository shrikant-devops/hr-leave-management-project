package com.hrms.leave.service;

import com.hrms.leave.entity.*;
import com.hrms.leave.repository.LeaveBalanceRepository;
import com.hrms.leave.repository.LeaveRequestRepository;
import com.hrms.leave.repository.LeaveTypeRepository;
import com.hrms.leave.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository,
                         LeaveBalanceRepository leaveBalanceRepository,
                         LeaveTypeRepository leaveTypeRepository,
                         UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.userRepository = userRepository;
    }

    public static double countWorkingDays(LocalDate start, LocalDate end) {
        double days = 0;
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            if (cursor.getDayOfWeek() != DayOfWeek.SATURDAY && cursor.getDayOfWeek() != DayOfWeek.SUNDAY) {
                days++;
            }
            cursor = cursor.plusDays(1);
        }
        return days;
    }

    public List<LeaveType> allLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public List<LeaveBalance> balancesFor(User user, int year) {
        return leaveBalanceRepository.findByUserIdAndYear(user.getId(), year);
    }

    public List<LeaveRequest> historyFor(User user) {
        return leaveRequestRepository.findByEmployeeIdOrderByAppliedAtDesc(user.getId());
    }

    @Transactional
    public String applyForLeave(User employee, Long leaveTypeId, LocalDate startDate, LocalDate endDate, String reason) {
        if (startDate.isAfter(endDate)) {
            return "Start date must be before or equal to end date.";
        }
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type"));

        double requestedDays = countWorkingDays(startDate, endDate);
        int year = startDate.getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeIdAndYear(employee.getId(), leaveTypeId, year)
                .orElseGet(() -> {
                    LeaveBalance fresh = new LeaveBalance(employee, leaveType, year, leaveType.getDefaultDaysPerYear(), 0);
                    return leaveBalanceRepository.save(fresh);
                });

        if (balance.getRemainingDays() < requestedDays) {
            return "Insufficient leave balance. Remaining " + balance.getRemainingDays() + " day(s), requested " + requestedDays + ".";
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setNumberOfDays(requestedDays);
        request.setReason(reason);
        request.setStatus(LeaveStatus.PENDING);
        request.setAppliedAt(LocalDateTime.now());
        leaveRequestRepository.save(request);
        return null;
    }

    public List<LeaveRequest> pendingForManager(Long managerId) {
        return leaveRequestRepository.findByEmployeeManagerIdAndStatusOrderByAppliedAtAsc(managerId, LeaveStatus.PENDING);
    }

    public List<LeaveRequest> allForManagerTeam(Long managerId) {
        return leaveRequestRepository.findByEmployeeManagerIdOrderByAppliedAtDesc(managerId);
    }

    public List<User> teamOf(Long managerId) {
        return userRepository.findByManagerId(managerId);
    }

    @Transactional
    public String decide(Long requestId, User decidingManager, boolean approve, String comment) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            return "This request has already been " + request.getStatus().name().toLowerCase() + ".";
        }

        if (decidingManager.getRole() != Role.HR) {
            Long employeeManagerId = request.getEmployee().getManager() != null ? request.getEmployee().getManager().getId() : null;
            if (employeeManagerId == null || !employeeManagerId.equals(decidingManager.getId())) {
                return "You are not authorized to decide this request.";
            }
        }

        if (approve) {
            int year = request.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository
                    .findByUserIdAndLeaveTypeIdAndYear(request.getEmployee().getId(), request.getLeaveType().getId(), year)
                    .orElseThrow(() -> new IllegalStateException("Balance record missing"));
            if (balance.getRemainingDays() < request.getNumberOfDays()) {
                return "Employee no longer has sufficient balance to approve this request.";
            }
            balance.setUsedDays(balance.getUsedDays() + request.getNumberOfDays());
            leaveBalanceRepository.save(balance);
            request.setStatus(LeaveStatus.APPROVED);
        } else {
            request.setStatus(LeaveStatus.REJECTED);
        }

        request.setDecidedBy(decidingManager);
        request.setDecidedAt(LocalDateTime.now());
        request.setManagerComment(comment);
        leaveRequestRepository.save(request);
        return null;
    }

    @Transactional
    public String cancel(Long requestId, User employee) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        if (!request.getEmployee().getId().equals(employee.getId())) {
            return "You can only cancel your own requests.";
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            return "Only pending requests can be cancelled.";
        }
        request.setStatus(LeaveStatus.CANCELLED);
        leaveRequestRepository.save(request);
        return null;
    }

    public List<LeaveRequest> allRequests() {
        return leaveRequestRepository.findAllByOrderByAppliedAtDesc();
    }

    public List<LeaveBalance> allBalances(int year) {
        return leaveBalanceRepository.findByYear(year);
    }

    public int currentYear() {
        return Year.now().getValue();
    }
}
