package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


@QuarkusTest
class ChatBotTest {

    @Inject
    ChatBot chatBot;

    @Test
    void shouldReturnInformationOfOneUser() {
        PersonsDto chat = chatBot.chat("What is the information of John Smith?");
        assertThat(chat.persons()).hasSize(1);
        PersonsDto.PersonDto personDto = chat.persons().getFirst();

        assertThat(personDto.email())
                .isEqualTo("johndoe@example.com");
    }

    @Test
    void shouldReturnAJSONDocument() {
        given()
                .body("What is the information of John Smith?")
                .when()
                .post("/person")
                .then()
                .statusCode(200)
                .body("persons[0].email",
                        equalTo("johndoe@example.com")
                )
                .body(
                        matchesJsonSchemaInClasspath("person-schema.json")
                );
    }

}