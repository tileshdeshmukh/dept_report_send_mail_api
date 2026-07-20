package com.cloverinfotech.emd_dept.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloverinfotech.emd_dept.modal.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
