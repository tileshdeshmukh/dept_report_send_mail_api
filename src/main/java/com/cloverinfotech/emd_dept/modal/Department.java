package com.cloverinfotech.emd_dept.modal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.*;

@Entity
@Table(name="department")
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String dpet_name;
	


    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return dpet_name;
	}


	public void setName(String name) {
		this.dpet_name = name;
	}


	public List<Employee> getEmployees() {
		return employees;
	}


	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}


	public Department(long id, String name, List<Employee> employees) {
		super();
		this.id = id;
		this.dpet_name = name;
		this.employees = employees;
	}


	public Department() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + dpet_name + "]";
	}
    
//  @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true) 

}
