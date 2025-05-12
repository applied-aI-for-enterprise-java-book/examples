package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/chat")
public class ChatBotResource {

    public record Response(String response, String id){}

    @Inject
    Assistant assistant;

    @POST
    public Response chat(String msg) {
        System.out.println(msg);
        return new Response("Hello from Quarkus REST", "1");
    }
}
