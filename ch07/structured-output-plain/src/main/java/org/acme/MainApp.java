package org.acme;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.tool.ToolExecutor;

import java.time.LocalDate;
import java.util.Map;

import static dev.ai4j.openai4j.Json.fromJson;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class MainApp {

    public record TransactionInfo
        (
        @Description("full name") String name, // <1>
        @Description("IBAN value") String iban,
        @Description("Date of the transaction") LocalDate transactionDate,
        @Description("Amount in dollars of the transaction")  double amount
        ) { }

    public interface Transaction {
        @UserMessage("Extract information about a transaction from {{it}}") // <1>
        TransactionInfo extract(String message);
    }

    public static void main(String[] args) {

        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey("demo")
            .modelName(GPT_4_O_MINI)
            .build(); // <1>

        Transaction transaction = AiServices.builder(Transaction.class)
            .chatLanguageModel(model)
            .build(); // <2>

        TransactionInfo transactionInfo =
            transaction.extract("My name is Alex; "
            + "I did a transaction on July 4th, 2023 from my account "
                + "with IBAN 123456789 of $25.5");

        System.out.println(transactionInfo);





        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            Map<String, Object> arguments = fromJson(toolExecutionRequest.arguments(), Map.class);
            String bookingNumber = arguments.get("bookingNumber").toString();
            return "";
        };

    }
}
