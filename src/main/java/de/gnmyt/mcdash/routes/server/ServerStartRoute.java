package de.gnmyt.mcdash.routes.server;

import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class ServerStartRoute extends DefaultHandler {

    @Override
    public String path() {
        return "start";
    }

    @Override
    public void post(Request request, ResponseController response) throws Exception {
        response.text("The server is already running");
    }
}
