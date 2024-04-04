package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final ChatClient openAIClient;

    public Controller(ChatClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    @GetMapping("/qq")
    public String quote() {
        File quoteFile = new File("qq.txt");
        long startTime = System.currentTimeMillis();
        String quoteOfTheDay = openAIClient.call("Quote of the day is..");
        long endTime = System.currentTimeMillis();
        logger.info("   **** TIME TO FETCH QUOTE OF THE DAY  **** " + (endTime - startTime) + " MILLISECONDS");
        System.out.println("  *****   Quote of the day is: " + quoteOfTheDay);
        writeToFile(quoteFile, quoteOfTheDay);
        return quoteOfTheDay;
    }

    @GetMapping("/query")
    public String query(@RequestParam String q) {
        String answer = openAIClient.call(q);
        return answer;
    }

    private static void writeToFile(File myFile, String quote) {
        try (FileWriter fw = new FileWriter(myFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(quote);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
