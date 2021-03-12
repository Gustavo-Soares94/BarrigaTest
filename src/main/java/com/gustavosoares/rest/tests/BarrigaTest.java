package com.gustavosoares.rest.tests;

import com.gustavosoares.rest.core.BaseTest;
import com.gustavosoares.rest.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

     private static String conta_name = "Conta" + System.nanoTime();
     private static Integer conta_id;
     private static Integer mov_id;

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
    }

    @Test
    public void t_02_deveIncluirContaComSucesso(){

        conta_id = given()
                .body("{ \"nome\": \""+conta_name+"\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t_03_deveAlterarContacomSucesso(){
        given()
                .body("{ \"nome\": \""+conta_name+" alterada\"}")
                .pathParam("id", conta_id)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
        ;
    }

    @Test
    public void t_04_naoDeveInserirContaComMesmoNome(){

        given()
                .body("{ \"nome\": \""+conta_name+" alterada\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t_05_DeveInserirMovimentacaoSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        mov_id = given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t_06_naoValidarCamposObrigatoriosMovimentacao(){

        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                       ))
        ;
    }

    @Test
    public void t_07_naoDeveInserirMovimentacaoComDataFutura(){
        Movimentacao mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void t_08_naoDeveRemoverContaComMovimentacao(){
        given()
                .pathParam("id", conta_id)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign") )
        ;
    }

    @Test
    public void t_09_deveCalcularSaldoContas(){
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+conta_id+"}.saldo ", is("100.00"))
        ;
    }

    @Test
    public void t_10_deveRemoverMovimentacao(){
        given()
                .pathParam("id", mov_id )
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }

    @Test
    public void t_11_naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(conta_id);
        //mov.setUsuario_id();
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);
        return mov;
    }



}
