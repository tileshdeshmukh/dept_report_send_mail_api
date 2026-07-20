package com.cloverinfotech.emd_dept.modal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
@Table(name="employee")
public class Employee {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
	@Column(name = "emp_name")
    private String emp_name;
	
	@Column(name = "salary")
    private long salary;
	
	@Column(name = "shift")
    private String shift;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // FK column
    @JsonBackReference // prevents infinite JSON recursion
    private Department department;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getEmp_name() {
		return emp_name;
	}


	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}


	public long getSalary() {
		return salary;
	}


	public void setSalary(long salary) {
		this.salary = salary;
	}


	public String getShift() {
		return shift;
	}


	public void setShift(String shift) {
		this.shift = shift;
	}


	public Department getDepartment() {
		return department;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}


	public Employee(long id, String emp_name, long salary, String shift, Department department) {
		super();
		this.id = id;
		this.emp_name = emp_name;
		this.salary = salary;
		this.shift = shift;
		this.department = department;
	}


	public Employee() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Employee [id=" + id + ", emp_name=" + emp_name + ", salary=" + salary + ", shift=" + shift
				+ ", department=" + department + "]";
	}


	
    
    

}
