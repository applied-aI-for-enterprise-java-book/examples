package org.acme;

import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/person")
public class PersonResource {

    @Startup
    @Transactional
    public void setup() {
        Person p1 = new Person();
        p1.name = "John Smith";
        p1.address = "123 Apple St";
        p1.email= "johndoe@example.com";
        p1.phone= "123-456-7890";
        p1.persist();

        Person p2 = new Person();
        p2.name = "Emily Johnson";
        p2.address = "123 Avenue St";
        p2.email= "emilyj@email.com";
        p2.phone= "987-654-3210";
        p2.persist();
    }

    @Inject
    ChatBot chatBot;

    @GET
    public List<Person> findAll() {
        return Person.listAll();
    }

    @POST
    public PersonsDto chat(String query) {
        return chatBot.chat(query);
    }
}
