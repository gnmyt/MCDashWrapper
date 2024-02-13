package de.gnmyt.mcdash.routes;

import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class PingRoute extends DefaultHandler {

    @Override
    public String path() {
        return "ping";
    }

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.text("Pong!");
    }
}
