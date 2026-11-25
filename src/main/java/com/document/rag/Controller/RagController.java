package com.document.rag.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

    private final ChatClient chatClient;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultSystem("""
                        You are a helpful insurance assistant.
                        Answer ONLY from the provided context.
                        If the answer is not in the context, say you don't know.
                        """)
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder()
                                .topK(4)
                                .similarityThreshold(0.5)
                                .build())
                        .build())
                .build();
    }

    // GET http://localhost:8080/ask?q=What is a deductible?
    @GetMapping("/ask")
    public String ask(@RequestParam("q") String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
