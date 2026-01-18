package com.forge;

import com.forge.storage.ForgeData;
import com.forge.storage.JsonStore;

public class App {
    public static void main(String[] args) {
        JsonStore store = new JsonStore("forge_data.json");

        ForgeData data = store.load();
        System.out.println("Forge v0.1 — loaded courses: " + data.getCourses().size());

        // Save immediately (creates file on first run)
        store.save(data);
        System.out.println("Forge v0.1 — saved to forge_data.json");
    }
}