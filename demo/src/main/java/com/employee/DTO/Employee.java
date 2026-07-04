package com.employee.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "employee")
public class Employee {


    @Column(name= "employee_name")
    public String employeeName;

    @Id
    @Column(name = "employee_id")
    public Integer employeeId;
}
