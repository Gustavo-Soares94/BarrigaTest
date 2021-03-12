package com.gustavosoares.rest.tests.refac;
import com.gustavosoares.rest.core.BaseTest;
import com.gustavosoares.rest.tests.Movimentacao;
import com.gustavosoares.rest.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

    @Test
    public void deveIncluirContaComSucesso(){

        given()
                .body("{ \"nome\": \"Nova Conta inserida\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void DeveInserirMovimentacaoSucesso(){
        Movimentacao mov = getMovimentacaoValida();

        given()
                .body(mov)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
        ;
    }

    @Test
    public void deveValidarCamposObrigatoriosMovimentacao(){

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
    public void naoDeveInserirMovimentacaoComDataFutura(){
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
    public void naoDeveRemoverContaComMovimentacao(){
        Integer conta_id = getIdContaPeloNome("Conta com movimentacao");

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
    public void deveRemoverMovimentacao(){
        Integer mov_id = getIdMovimentacaoPelaDescriacao("Movimentacao para exclusao");
        given()
                .pathParam("id", mov_id )
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }

    public Integer getIdContaPeloNome(String nome){
        return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
    }

    public Integer getIdMovimentacaoPelaDescriacao(String desc){
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }

    private Movimentacao getMovimentacaoValida(){
        Movimentacao mov = new Movimentacao();
        mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
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
