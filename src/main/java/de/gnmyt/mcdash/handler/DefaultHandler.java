package de.gnmyt.mcdash.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.http.Request;
import de.gnmyt.mcdash.http.ResponseController;
import de.gnmyt.mcdash.http.HTTPMethod;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public abstract class DefaultHandler implements HttpHandler {

    private static final Logger LOG = new Logger(DefaultHandler.class);

    @Override
    public void handle(HttpExchange exchange) {
        Request request = prepareRequest(exchange);
        ResponseController controller = new ResponseController(exchange);
        execute(request, controller);
    }

    public void execute(Request request, ResponseController response) {
        try {
            getClass().getMethod(request.getMethod().toString().toLowerCase(), Request.class, ResponseController.class)
                    .invoke(this, request, response);
        } catch (Exception e) {
            LOG.error("An error occurred while executing the route: {}", e.getMessage());
            response.code(500).message("An internal error occurred");
        }
    }

    public void register() {
        String contextPath = getClass().getPackage().getName()
                .replace(MCDashWrapper.getRoutePackageName(), "")
                .replace(".", "/");
        contextPath += (path().isEmpty() ? "/" : "/" + path());
        MCDashWrapper.getHttpServer().createContext("/api" + contextPath, this);
    }

    protected Request prepareRequest(HttpExchange exchange) {
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(exchange.getRequestBody(), writer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("An error occurred while reading the request body: {}", e.getMessage());
        }

        HTTPMethod method = HTTPMethod.valueOf(exchange.getRequestMethod());

        return new Request()
                .setUri(exchange.getRequestURI())
                .setRemoteAddress(exchange.getRemoteAddress())
                .setMethod(method)
                .setHeaders(exchange.getRequestHeaders())
                .mapBody(writer.toString())
                .mapQuery(exchange.getRequestURI().getQuery());
    }

    public String path() {
        return "";
    }

    public void get(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    public void post(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    public void put(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    public void delete(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    public void patch(Request request, ResponseController response) throws Exception {
        response.code(404).message("Route not found");
    }

    public String getStringFromBody(Request request, String name) {
        return request.getBody().get(name);
    }

    public Integer getIntegerFromBody(Request request, String name) {
        try {
            return Integer.parseInt(getStringFromBody(request, name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean getBooleanFromBody(Request request, String name) {
        return Boolean.parseBoolean(getStringFromBody(request, name));
    }

    public String getStringFromQuery(Request request, String name) {
        return request.getQuery().get(name);
    }

    public Integer getIntegerFromQuery(Request request, String name) {
        try {
            return Integer.parseInt(getStringFromQuery(request, name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isStringInBody(Request request, ResponseController controller, String name) {
        String value = getStringFromBody(request, name);
        if (value == null || value.isEmpty()) {
            controller.code(400).messageFormat("You need to provide %s in your request body", name);
            return false;
        }
        return true;
    }

    public boolean isIntegerInBody(Request request, ResponseController controller, String name) {
        if (!isStringInBody(request, controller, name)) return false;
        Integer value = getIntegerFromBody(request, name);
        if (value == null) {
            controller.code(400).messageFormat("%s must be an integer", name);
        }
        return value != null;
    }

    public boolean isBooleanInBody(Request request, ResponseController controller, String name) {
        if (!isStringInBody(request, controller, name)) return false;
        String value = getStringFromBody(request, name);
        try {
            if (value.equals("true") || value.equals("false")) return true;
            throw new Exception();
        } catch (Exception e) {
            controller.code(400).messageFormat("%s must be an boolean", name);
            return false;
        }
    }

    public boolean isStringInQuery(Request request, ResponseController controller, String name) {
        String value = getStringFromQuery(request, name);
        if (value == null || value.isEmpty()) {
            controller.code(400).messageFormat("You need to provide %s in your request query", name);
            return false;
        }
        return true;
    }
}
