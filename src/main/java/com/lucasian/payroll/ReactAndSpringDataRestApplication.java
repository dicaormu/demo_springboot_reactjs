package com.lucasian.payroll;

import com.lucasian.payroll.employee.Employee;
import com.lucasian.payroll.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ReactAndSpringDataRestApplication {

    @Autowired
    EmployeeRepository repository;

    /**
     * @see com.lucasian.payroll.common.Preprovisioning
     */
    @Bean
    CommandLineRunner preprovisioning() {
        final List<Employee> initialEmployees = Arrays.asList(
                new Employee("Frodo", "Baggins", "ring bearer"),
                new Employee("Bilbo", "Baggins", "original ring bearer")
        );
        return strings -> initialEmployees.stream().forEach(e -> this.repository.save(e));
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactAndSpringDataRestApplication.class, args);
    }
}