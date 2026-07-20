package com.cloverinfotech.emd_dept.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloverinfotech.emd_dept.modal.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();

}
