package de.gnmyt.mcdash.http;

import java.util.HashMap;

public class JSONBuilder {

    private final HashMap<String, Object> json = new HashMap<>();
    private final ResponseController response;

    /**
     * Constructor of the {@link JSONBuilder}
     *
     * @param response The response controller
     */
    public JSONBuilder(ResponseController response) {
        this.response = response;
    }

    /**
     * Adds a new value to the JSON
     *
     * @param key   The key of the value
     * @param value The value you want to add
     * @return the current {@link JSONBuilder} instance
     */
    public JSONBuilder add(String key, Object value) {
        json.put(key, value);
        return this;
    }

    /**
     * Finishes the JSON and sends it to the client
     */
    public void finish() {
        response.json(json);
    }

}
