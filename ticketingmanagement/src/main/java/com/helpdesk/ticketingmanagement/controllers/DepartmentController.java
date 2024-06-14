package com.helpdesk.ticketingmanagement.controllers;


import com.helpdesk.ticketingmanagement.dto.DepartmentDto;
import com.helpdesk.ticketingmanagement.entities.Department;
import com.helpdesk.ticketingmanagement.repositories.DepartmentRepository;
import com.helpdesk.ticketingmanagement.services.DepartmentService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@EnableMethodSecurity
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping
    public void addDepartment(@RequestBody DepartmentDto departmentDto) {
        departmentService.addDepartment(departmentDto);
    }
    @DeleteMapping("/{id}")
    public void addDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}
