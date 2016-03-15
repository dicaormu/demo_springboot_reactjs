package com.lucasian.payroll.common;

import com.lucasian.payroll.employee.Employee;
import com.lucasian.payroll.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Preprovisioning implements CommandLineRunner {

    private final EmployeeRepository repository;

    @Autowired
    public Preprovisioning(EmployeeRepository repository) {
        this.repository = repository;
    }

    /**
     * @see com.lucasian.payroll.ReactAndSpringDataRestApplication
     */
    @Override
    public void run(String... strings) throws Exception {
        this.repository.save(
                new Employee("Gandalf", "The gray", "magician without magic")
        );
    }
}