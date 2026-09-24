package com.hrms.leave.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    private int defaultDaysPerYear;

    public LeaveType() {
    }

    public LeaveType(String name, int defaultDaysPerYear) {
        this.name = name;
        this.defaultDaysPerYear = defaultDaysPerYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultDaysPerYear() {
        return defaultDaysPerYear;
    }

    public void setDefaultDaysPerYear(int defaultDaysPerYear) {
        this.defaultDaysPerYear = defaultDaysPerYear;
    }
}
