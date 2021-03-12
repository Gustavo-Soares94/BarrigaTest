package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

    @BeforeClass
    public  static void login(){
        Map<String, String> login = new HashMap<>();
        login.put("email", "gustavosoares.adm@Outlook.com");
        login.put("senha", "123456");

        String token = given()
                .body(login)
        .when()
                .post("/signin")
        .then()
                .statusCode(200)
                .extract().path("token")
                ;

        RestAssured.requestSpecification.header("Authorization", "JWT " + token);

        RestAssured.get("/reset").then().statusCode(200);
    }

    @Test
    public void deveIncluirContaComSucesso(){

         given()
                .body("{ \"nome\": \"Conta inserida\"}")
         .when()
                .post("/contas")
         .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveAlterarContacomSucesso(){
        Integer conta_id = getIdContaPeloNome("Conta para alterar");

        given()
                .body("{ \"nome\": \"Conta alterada\"}")
                .pathParam("id", conta_id)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void naoDeveInserirContaComMesmoNome(){

        given()
                .body("{ \"nome\": \"Conta mesmo nome\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

}
