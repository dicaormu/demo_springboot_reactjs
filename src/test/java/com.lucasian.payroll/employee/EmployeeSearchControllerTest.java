package com.lucasian.payroll.employee;

import com.lucasian.payroll.ReactAndSpringDataRestApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReactAndSpringDataRestApplication.class)
public class EmployeeSearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeSearchController searchController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    public void
    should_return_existing_employees() throws Exception {
        // given
        final Employee employee = new Employee("Pedro", "Perez", "sales");
        final List<Employee> oneEmployee = Collections.singletonList(employee);
        // when
        when(employeeRepository
                .findByLastName(employee.getLastName()))
                .thenReturn(oneEmployee);
        // then
        mockMvc.perform(get("/employee/_search")
                .param("lastName", employee.getLastName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(employee.getFirstName().toUpperCase())))
                .andExpect(jsonPath("$[0].lastName", is(employee.getLastName().toUpperCase())))
                .andExpect(jsonPath("$[0].description", is(employee.getDescription())))
        ;
    }

    @Test
    public void
    should_return_empty_response_when_no_employees() throws Exception {
        // given
        final List<Employee> noEmployees = Collections.emptyList();
        // when
        when(employeeRepository.findByLastName("Perez"))
                .thenReturn(noEmployees);
        // then
        mockMvc.perform(get("/employee/_search")
                .param("lastName", "Perez")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
        ;
    }
}
