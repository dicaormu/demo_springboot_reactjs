package com.lucasian.payroll.employee;

import com.jayway.restassured.RestAssured;
import com.lucasian.payroll.ReactAndSpringDataRestApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReactAndSpringDataRestApplication.class)
public class EmployeeSearchControllerIntegrationTest {

    @Value("${local.server.port}")
    int port;

    @Before()
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void
    should_return_content_on_existing_employees() {
        final List<String> firstNames =
                given().param("lastName", "Baggins")
                        .when().get("/employee/_search")
                        .then().assertThat()
                        .statusCode(OK.value())
                        .extract()
                        .path("firstName");

        // then
        assertThat(firstNames).hasSize(2);
        assertThat(firstNames).contains("FRODO", "BILBO");
    }

    @Test
    public void
    should_return_empty_when_no_match() {
        final List<String> lastNames =
                given().param("lastName", "Aragorn")
                        .when().get("/employee/_search")
                        .then().assertThat()
                        .statusCode(OK.value())
                        .extract()
                        .path("lastName");

        // then
        assertThat(lastNames).hasSize(0);
    }
}
