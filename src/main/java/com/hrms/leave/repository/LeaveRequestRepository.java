package com.hrms.leave.repository;

import com.hrms.leave.entity.LeaveRequest;
import com.hrms.leave.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("select r from LeaveRequest r join fetch r.employee e left join fetch r.leaveType left join fetch r.decidedBy " +
            "where e.id = :employeeId order by r.appliedAt desc")
    List<LeaveRequest> findByEmployeeIdOrderByAppliedAtDesc(@Param("employeeId") Long employeeId);

    @Query("select r from LeaveRequest r join fetch r.employee e left join fetch r.leaveType left join fetch r.decidedBy " +
            "where e.manager.id = :managerId and r.status = :status order by r.appliedAt asc")
    List<LeaveRequest> findByEmployeeManagerIdAndStatusOrderByAppliedAtAsc(@Param("managerId") Long managerId, @Param("status") LeaveStatus status);

    @Query("select r from LeaveRequest r join fetch r.employee e left join fetch r.leaveType left join fetch r.decidedBy " +
            "where e.manager.id = :managerId order by r.appliedAt desc")
    List<LeaveRequest> findByEmployeeManagerIdOrderByAppliedAtDesc(@Param("managerId") Long managerId);

    @Query("select r from LeaveRequest r join fetch r.employee left join fetch r.leaveType left join fetch r.decidedBy " +
            "order by r.appliedAt desc")
    List<LeaveRequest> findAllByOrderByAppliedAtDesc();

    List<LeaveRequest> findByStatusOrderByAppliedAtAsc(LeaveStatus status);
}
