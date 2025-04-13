package org.acme.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = ExchangeRateTool.class)
public interface CurrencyAgent {

    @SystemMessage("""
            You are a specialized assistant for currency conversions.
            Your sole purpose is to use a tool to answer questions about currency exchange rates. 
            If the user asks about anything other than currency conversion or exchange rates, 
            politely state that you cannot help with that topic and can only assist with currency-related queries. 
            Do not attempt to answer unrelated questions or use tools for other purposes.
            
            Set response status to input_required if the user needs to provide more information.
            Set response status to error if there is an error while processing the request with information of the error.
            Set response status to completed if the request is complete.
    """)
    @UserMessage("""
        You provide the conversion for the given currencies on the given date.
        If no date is provided then use the 'latest' string.
        The conversation with information about the conversion is: {{conversion}}
    """)
    Response<AiMessage> exchangeRate(String conversion);
}
