package com.lucasian.payroll;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static com.jayway.restassured.RestAssured.given;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

//import static org.assertj.core.api.Assertions.assertThat;
import com.jayway.restassured.*;
import com.jayway.*;

//@WebIntegrationTest
@WebAppConfiguration
//@IntegrationTest({"server.port=0"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReactAndSpringDataRestApplication.class)
public class EmployeeSearchControllerIntegrationTest {

    @Before()
    public void setUp() {
        //RestAssured.port = 8086;
    }

    @Test
    public void
    should_return_content_on_existing_employees() {
        // given
        final List<Employee> result =
                Collections.singletonList(new Employee("Pedro", "Perez", "sales"));

        // when
        given().param("lastName", "Toto")
                .when().get("/employee/_search")
                .then().assertThat()
                .statusCode(OK.value())
                //.contentType(ContentType.XML)
                .body(equalTo("<Response></Response>"));

        // then
        /*assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull().isEqualTo(1L);
        assertThat(result.getName()).isNotNull().isEqualTo("customer 1");
        assertThat(result.getCustomerNumber()).isNotNull().isEqualTo("1");*/
    }
}
