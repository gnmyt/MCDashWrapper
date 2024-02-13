package de.gnmyt.mcdash.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

public class ResponseController {

    private HttpExchange exchange;
    private Response response = new Response();

    /**
     * Basic constructor of the {@link ResponseController}
     *
     * @param exchange The exchange from {@link de.gnmyt.mcdash.handler.DefaultHandler#handle}
     */
    public ResponseController(HttpExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Gets the current exchange
     *
     * @return the current exchange
     */
    public HttpExchange getExchange() {
        return exchange;
    }

    /**
     * Writes a string to the output
     *
     * @param output The output you want to add
     * @return the current {@link ResponseController} instance
     */
    public ResponseController writeToOutput(String output) {
        response.addOutput(output);
        return this;
    }

    /**
     * Sets the response code
     *
     * @param code The new code
     * @return the current {@link ResponseController} instance
     */
    public ResponseController code(int code) {
        response.setCode(code);
        return this;
    }

    /**
     * Adds a response header
     *
     * @param name  The name of the header
     * @param value The value of the header
     * @return the current {@link ResponseController} instance
     */
    public ResponseController header(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    /**
     * Sets the response content type
     *
     * @param type The new content type
     * @return the current {@link ResponseController} instance
     */
    public ResponseController type(ContentType type) {
        response.setContentType(type);
        return this;
    }

    /**
     * Sends a text response to the client
     *
     * @param output The output you want to send
     */
    public void text(String output) {
        writeToOutput(output);
        send();
    }

    /**
     * Sends a byte response to the client
     *
     * @param bytes The bytes you want to send
     */
    public void bytes(byte[] bytes) {
        response.setBinaryOutput(bytes);
        send();
    }

    /**
     * Sends a json response to the client
     *
     * @param values The values you want to send
     */
    @SafeVarargs
    public final void json(HashMap<String, Object>... values) {
        response.setContentType(ContentType.JSON);

        Gson gson = new Gson();

        String json = gson.toJson(values.length == 1 ? values[0] : values);

        writeToOutput(json);
        send();
    }

    public JSONBuilder json() {
        return new JSONBuilder(this);
    }

    /**
     * Sends a single json message to the client
     *
     * @param key   The name of the json response
     * @param value The value of the json response
     */
    public void jsonMessage(String key, String value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        json(map);
    }

    /**
     * Sends a json message to the client
     *
     * @param message The message you want to send
     */
    public void message(String message) {
        jsonMessage("message", message);
    }

    /**
     * Sends a formatted json message to the client
     *
     * @param message The formatted message you want to send
     * @param values  The values you want to put in the format
     */
    public void messageFormat(String message, Object... values) {
        message(String.format(message, values));
    }

    /**
     * Sends the current response
     */
    public void send() {
        OutputStream os = exchange.getResponseBody();
        response
                .addHeader("Server", "DashboardWrapper")
                .addHeader("Content-Type", response.getContentType().getType())
                .addHeader("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS"))
            response.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, POST")
                    .addHeader("Access-Control-Allow-Headers", "*");

        response.getHeaders().forEach((key, value) -> exchange.getResponseHeaders().put(key, Collections.singletonList(value)));

        byte[] bs = response.getBinaryOutput() == null ? response.getOutput().getBytes(StandardCharsets.UTF_8)
                : response.getBinaryOutput();

        try {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1L);
            } else {
                exchange.sendResponseHeaders(response.getCode(), bs.length);
                os.write(bs);
            }
            os.close();
        } catch (IOException ignored) {
        }
    }
}
