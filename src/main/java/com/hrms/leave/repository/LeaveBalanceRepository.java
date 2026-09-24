package com.hrms.leave.repository;

import com.hrms.leave.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    @Query("select b from LeaveBalance b join fetch b.user join fetch b.leaveType where b.user.id = :userId and b.year = :year")
    List<LeaveBalance> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

    Optional<LeaveBalance> findByUserIdAndLeaveTypeIdAndYear(Long userId, Long leaveTypeId, int year);

    @Query("select b from LeaveBalance b join fetch b.user join fetch b.leaveType where b.year = :year")
    List<LeaveBalance> findByYear(@Param("year") int year);
}
