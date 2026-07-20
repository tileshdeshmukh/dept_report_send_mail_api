package com.cloverinfotech.emd_dept.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloverinfotech.emd_dept.modal.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
