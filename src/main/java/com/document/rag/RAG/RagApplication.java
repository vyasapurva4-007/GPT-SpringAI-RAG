package com.document.rag.RAG;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

@SpringBootApplication(scanBasePackages = "com.document.rag")
public class RagApplication {

	private static final Logger log = LoggerFactory.getLogger(RagApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RagApplication.class, args);
	}

	/** In-memory vector store, good for demos. Swap for PgVector, Chroma, Qdrant, etc. in real apps. */
	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel) {
		return SimpleVectorStore.builder(embeddingModel).build();
	}

	/** Ingestion: read -> split into chunks -> embed -> store. Runs once at startup. */
	@Bean
	ApplicationRunner ingest(VectorStore vectorStore,
							 @Value("classpath:insurance-faq.txt") Resource knowledgeFile) {
		return args -> {
			List<Document> docs = new TextReader(knowledgeFile).get();
			List<Document> chunks = TokenTextSplitter.builder().build().apply(docs);
			vectorStore.add(chunks);
			log.info("Ingested {} chunks into the vector store", chunks.size());
		};
	}
}