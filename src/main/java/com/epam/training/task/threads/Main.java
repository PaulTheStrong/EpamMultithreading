package com.epam.training.task.threads;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {

    private static final String INPUT_FILE = "src/main/resources/input.json";

    public static void main(String[] args) throws IOException {

        String tradersString = String.join("", Files.readAllLines(Paths.get(INPUT_FILE)));

        ObjectMapper objectMapper = new ObjectMapper();
        List<Trader> traders = objectMapper.readValue(tradersString, new TypeReference<List<Trader>>() { });

        ExecutorService service = Executors.newFixedThreadPool(traders.size());

        traders.forEach(service::submit);

        service.shutdown();
    }

}
