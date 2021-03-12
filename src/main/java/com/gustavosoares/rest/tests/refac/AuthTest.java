package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;


public class AuthTest extends BaseTest {

    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<>();
        login.put("email", "gustavosoares.adm@Outlook.com");
        login.put("senha", "123456");

        String token = given()
                .body(login)
        .when()
                .post("/signin")
        .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + token);

        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

}