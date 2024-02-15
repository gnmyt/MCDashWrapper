package de.gnmyt.mcdash.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.gnmyt.mcdash.MCDashWrapper;
import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.api.UserManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class ProxyHandler implements HttpHandler {

    private final static Logger LOG = new Logger(ProxyHandler.class);
    private final OkHttpClient client = new OkHttpClient();
    private final ServerManager serverManager = MCDashWrapper.getServerManager();
    private final UserManager userManager = MCDashWrapper.getUserManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uuid = exchange.getRequestURI().getPath().split("/")[2];
        String pathWithoutUuid = exchange.getRequestURI().getPath().replace("/proxy/" + uuid, "")
                + (exchange.getRequestURI().getQuery() != null ? "?" + exchange.getRequestURI().getQuery() : "");

        if (pathWithoutUuid.startsWith("/api")) {
            List<String> authHeader = exchange.getRequestHeaders().get("Authorization");
            if (authHeader == null) {
                exchange.sendResponseHeaders(401, 0);
                return;
            }

            String[] authCredentials;
            try {
                authCredentials = new String(Base64.getDecoder().decode(authHeader.get(0)
                        .replace("Basic ", ""))).split(":");
            } catch (Exception e) {
                exchange.sendResponseHeaders(401, 0);
                return;
            }

            if (authCredentials.length != 2) {
                exchange.sendResponseHeaders(401, 0);
                return;
            }

            if (!userManager.isPasswordCorrect(authCredentials[0], authCredentials[1])) {
                exchange.sendResponseHeaders(401, 0);
                return;
            }

            if (serverManager.getServer(uuid) == null) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }
        }

        RequestBody requestBody = exchange.getRequestMethod().equals("GET") ? null : RequestBody.create(IOUtils.toByteArray(exchange.getRequestBody()), null);

        String serverKey = serverManager.getServer(uuid).getConfiguration().getProxyKey();

        Request request = new Request.Builder()
                .url("http://localhost:" + serverManager.getServer(uuid).getDashPort() + pathWithoutUuid)
                .method(exchange.getRequestMethod(), requestBody)
                .header("User-Agent", "MCDash-Wrapper")
                .header("Authorization", String.format("Basic %s", Base64.getEncoder().encodeToString(("PROXY:" + serverKey).getBytes())))
                .build();

        try (Response response = client.newCall(request).execute()) {
            response.headers().toMultimap().forEach((key, value) -> exchange.getResponseHeaders().put(key, value));

            exchange.sendResponseHeaders(response.code(), 0);

            if (pathWithoutUuid.endsWith(".js") || pathWithoutUuid.endsWith(".css") || !pathWithoutUuid.contains(".")) {
                String htmlContent = response.body().string();
                htmlContent = modifyContent(htmlContent, uuid);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(htmlContent.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                try (InputStream is = response.body().byteStream(); OutputStream os = exchange.getResponseBody()) {
                    IOUtils.copy(is, os);
                }
            }

        } catch (IOException e) {
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }

    private String modifyContent(String htmlContent, String uuid) {
        htmlContent = htmlContent.replaceAll("/assets/", "/proxy/" + uuid + "/assets/");
        htmlContent = htmlContent.replaceAll("/api/", "/proxy/" + uuid + "/api/");
        htmlContent = htmlContent.replaceAll("t\\(\"/files\"", "t(\"/proxy/" + uuid + "/files\"");
        htmlContent = htmlContent.replaceAll("r.pathname===\"/\"", "r.pathname===\"/proxy/" + uuid + "/\"");
        htmlContent = htmlContent.replaceAll("a=>a===\"/\"", "a=>a===\"/proxy/" + uuid + "/\"");

        htmlContent = htmlContent.replaceAll("path:\"/", "path: \"/proxy/" + uuid + "/");
        return htmlContent;
    }
}
