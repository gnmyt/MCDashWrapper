package de.gnmyt.mcdash.routes;

import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;

public class DefaultRoute extends DefaultHandler {

    @Override
    public void get(Request request, ResponseController response) throws Exception {
        response.json()
                .add("version", MCDashWrapper.getConfig().getVersion())
                .add("author", "GNM")
                .finish();
    }
}
