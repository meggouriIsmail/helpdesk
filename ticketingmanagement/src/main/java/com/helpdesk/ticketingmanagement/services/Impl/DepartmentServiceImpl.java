package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.dto.DepartmentDto;
import com.helpdesk.ticketingmanagement.entities.Department;
import com.helpdesk.ticketingmanagement.repositories.DepartmentRepository;
import com.helpdesk.ticketingmanagement.services.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void addDepartment(DepartmentDto departmentDto) {
        Department department = new Department();
        department.setName(department.getName());
        departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        department.ifPresent(departmentRepository::delete);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
