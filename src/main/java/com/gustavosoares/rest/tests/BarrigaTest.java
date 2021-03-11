package com.gustavosoares.rest.tests;

import com.gustavosoares.rest.core.BaseTest;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {

     private String token;

    @Before
    public void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "gustavosoares.adm@Outlook.com");
        login.put("senha", "123456");

        token = given()
                    .body(login)
                .when()
                    .post("/signin")
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;
    }

    @Test

    public void naoDeveAcessarAPISemToken(){
        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .log().all()
                .header("Authorization", "JWT " + token)
                .body("{ \"nome\": \"conta raj\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
        ;

    }

}
