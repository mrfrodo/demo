package com.example.meq;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MeqController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public MeqController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/rag")
    public String generateAnswer(@RequestParam String q) {
        List<Document> similarDocuments = vectorStore.similaritySearch(q);

        String documents = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(
                """
                            You are a helpful assistant.
                            Use only the following documents to answer the question.
                            Do not use any other documents. 
                            If you do not know, you must answer: I DO NOT KNOW.

                            {documents}
                        """);

        Prompt prompt = createPrompt(q, documents, systemPromptTemplate);
        ChatResponse call = chatClient.call(prompt);
        return call.getResult().getOutput().getContent();
    }

    private Prompt createPrompt(String q, String documents, SystemPromptTemplate systemPromptTemplate) {
        return new Prompt(List.of(systemPromptTemplate
                .createMessage(Map.of("documents", documents)), new PromptTemplate("{query}")
                .createMessage(Map.of("query", q))));
    }
}
