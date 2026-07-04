package com.employee.controller;

import com.employee.DTO.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostConstruct
    public void initData(){
        employeeRepository.save(new Employee("Leon",1));
        employeeRepository.save(new Employee("Claire",2));
    }
    @PostMapping(value="/getId")
    public ResponseEntity<Employee> getEmployeeByName(@RequestBody Employee request){
        return employeeRepository.findByEmployeeName(request.getEmployeeName())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
    }

}
