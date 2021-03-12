package com.gustavosoares.rest.tests.refac.suite;
import com.gustavosoares.rest.core.BaseTest;
import com.gustavosoares.rest.tests.refac.AuthTest;
import com.gustavosoares.rest.tests.refac.ContasTest;
import com.gustavosoares.rest.tests.refac.MovimentacaoTest;
import com.gustavosoares.rest.tests.refac.SaldoTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class

})
public class Suite extends BaseTest {

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

}
