package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.dto.DepartmentDto;
import com.helpdesk.ticketingmanagement.entities.Department;

import java.util.List;

public interface DepartmentService {
    void addDepartment(DepartmentDto departmentDto);
    void deleteDepartment(Long id);
    List<Department> getAllDepartments();
}
