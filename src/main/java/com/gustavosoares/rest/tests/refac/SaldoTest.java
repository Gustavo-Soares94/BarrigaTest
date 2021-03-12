package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import com.gustavosoares.rest.utils.BuscasCamposUtils;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SaldoTest extends BaseTest {

    @Test
    public void deveCalcularSaldoContas(){
        Integer conta_id = BuscasCamposUtils.getIdContaPeloNome("Conta para saldo");
        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == "+conta_id+"}.saldo ", is("534.00"))
        ;
    }



}