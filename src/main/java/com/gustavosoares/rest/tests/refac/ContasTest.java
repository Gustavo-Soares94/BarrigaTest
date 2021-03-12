package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import com.gustavosoares.rest.utils.BuscasCamposUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ContasTest extends BaseTest {

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
        Integer conta_id = BuscasCamposUtils.getIdContaPeloNome("Conta para alterar");

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


}
