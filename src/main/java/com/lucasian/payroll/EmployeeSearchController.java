package com.lucasian.payroll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
    @ExceptionHandler(value = { EmptyResultDataAccessException.class, EntityNotFoundException.class })
    public void handleNotFound() { }
}