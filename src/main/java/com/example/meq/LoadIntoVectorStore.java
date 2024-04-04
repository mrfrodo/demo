package com.example.meq;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadIntoVectorStore {
    private static final Logger logger = LoggerFactory.getLogger(LoadIntoVectorStore.class);
    private final VectorStore vectorStore;

    @Value("classpath:meq_rules_pdf.pdf")
    private Resource meqRulesPdf;

    @Value("classpath:dummy_pdf.pdf")
    private Resource dummyPdf;

    public LoadIntoVectorStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {

        long startTime = System.currentTimeMillis();

        List<Document> documents = readDummy();
        vectorStore.accept(documents);

        long endTime = System.currentTimeMillis();
        logger.info("   **** TIME TO LOAD EMBEDDINGS **** " + (endTime - startTime) + " ms.");

    }

    private List<Document> readMeqRules() {
        return new TokenTextSplitter()
                .apply(new PagePdfDocumentReader(meqRulesPdf, PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(
                                new ExtractedTextFormatter.Builder()
                                        .build())
                        .build()).get());
    }

    private List<Document> readDummy() {
        return new TokenTextSplitter()
                .apply(new PagePdfDocumentReader(dummyPdf, PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(
                                new ExtractedTextFormatter.Builder()
                                        .build())
                        .build()).get());
    }
}
