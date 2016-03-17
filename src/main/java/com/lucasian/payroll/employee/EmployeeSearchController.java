package com.lucasian.payroll.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Note: this is NOT a HATEOAS compliant rest service
 * For a better example go to <a href="https://spring.io/guides/gs/rest-hateoas/">official documentation</a>
 */
@RequestMapping("/employee")
@Controller
public class EmployeeSearchController {

    @Autowired
    private EmployeeRepository repository;

    @RequestMapping(value = "/_search", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Employee> findByLastNameAndCapitalize(@RequestParam(value = "lastName", required = false, defaultValue = "") String lastName) {
        return repository
                .findByLastName(lastName)
                .stream()
                .map(e ->
                        new Employee.EmployeeBuilder()
                                .withId(e.getId())
                                .withFirstName(e.getFirstName().toUpperCase())
                                .withLastName(e.getLastName().toUpperCase())
                                .withDescription(e.getDescription())
                                .build()
                ).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {EmptyResultDataAccessException.class, EntityNotFoundException.class})
    public void handleNotFound() {
    }
}