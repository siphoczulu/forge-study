package com.forge;

import com.forge.cli.ForgeCli;
import com.forge.storage.ForgeData;
import com.forge.storage.JsonStore;

public class App {
    public static void main(String[] args) {
        JsonStore store = new JsonStore("forge_data.json");
        ForgeData data = store.load();

        new ForgeCli(data).run();

        store.save(data);
        System.out.println("Saved. Bye.");
    }
}