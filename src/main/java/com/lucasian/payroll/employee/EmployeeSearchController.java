package com.lucasian.payroll.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Note: this is NOT a HATEOAS compliant rest service
 * For a correct example go to <a href="https://spring.io/guides/gs/rest-hateoas/">official documentation</a>
 */
@RequestMapping("/employee")
@Controller
public class EmployeeSearchController {

    @Autowired
    private EmployeeRepository repository;

    @RequestMapping(value = "/_search", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Employee> findByLastName(@RequestParam(value = "lastName", required = false, defaultValue = "") String lastName) {
        return repository
                .findByLastName(lastName);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {EmptyResultDataAccessException.class, EntityNotFoundException.class})
    public void handleNotFound() {
    }
}