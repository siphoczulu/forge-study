package com.forge.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;

public class JsonStore {
    private final File file;
    private final ObjectMapper mapper;

    public JsonStore(String filePath) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public ForgeData load() {
        try {
            if (!file.exists()) {
                return new ForgeData();
            }
            return mapper.readValue(file, ForgeData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load data from " + file.getAbsolutePath(), e);
        }
    }

    public void save(ForgeData data) {
        try {
            mapper.writeValue(file, data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data to " + file.getAbsolutePath(), e);
        }
    }
}