package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

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
    public void deveCalcularSaldoContas(){
        Integer conta_id = getIdContaPeloNome("Conta para saldo");
        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+conta_id+"}.saldo ", is("534.00"))
        ;
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

}