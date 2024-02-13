package de.gnmyt.mcdash;

import com.sun.net.httpserver.HttpServer;
import de.gnmyt.mcdash.api.ConfigurationManager;
import de.gnmyt.mcdash.api.DashShutdownHook;
import de.gnmyt.mcdash.api.Logger;
import de.gnmyt.mcdash.api.ServerManager;
import de.gnmyt.mcdash.handler.DefaultHandler;
import de.gnmyt.mcdash.handler.StaticHandler;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MCDashWrapper {

    private static final File DATA_FOLDER = new File("data");
    private static final Logger LOG = new Logger(MCDashWrapper.class);
    private static final ConfigurationManager config = new ConfigurationManager();
    private static final int SERVER_PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 7865;
    private static final ServerManager serverManager = new ServerManager();

    private static HttpServer server;

    /**
     * The main method of the MCDash Wrapper.
     * This method will start the server and initialize the server manager
     * <p>
     * The server will also be stopped when the application is being shut down
     * (This is done by the {@link DashShutdownHook})
     * </p>
     *
     * @param args The arguments of the application
     */
    public static void main(String[] args) {
        LOG.info("Starting MCDash Wrapper v{} on port {}", config.getVersion(), SERVER_PORT);
        createDataFolder();
        serverManager.initialize();

        Runtime.getRuntime().addShutdownHook(new DashShutdownHook());

        try {
            server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
            server.setExecutor(Executors.newCachedThreadPool());
            server.createContext("/", new StaticHandler());
            registerRoutes();
            server.start();
        } catch (IOException e) {
            LOG.error("An error occurred while starting the server: {}", e.getMessage());
            System.exit(1);
        }

        serverManager.startServers();

        LOG.info("MCDash Wrapper v{} is now running on port {}", config.getVersion(), SERVER_PORT);
    }

    /**
     * Creates the data folder
     */
    public static void createDataFolder() {
        if (DATA_FOLDER.exists()) return;

        if (DATA_FOLDER.mkdirs()) {
            LOG.info("The data folder has been created");
        } else {
            LOG.error("An error occurred while creating the data folder");
        }
    }

    /**
     * Gets a data source by its name
     *
     * @param name The name of the data source
     * @return the data source
     */
    public static File getDataSource(String name) {
        return new File(DATA_FOLDER, name);
    }

    /**
     * Gets the name of the route package
     *
     * @return the name of the route package
     */
    public static String getRoutePackageName() {
        return MCDashWrapper.class.getPackage().getName() + ".routes";
    }

    /**
     * Registers all routes in the {@link de.gnmyt.mcdash.routes} package
     */
    private static void registerRoutes() {
        Reflections reflections = new Reflections(getRoutePackageName());
        reflections.getSubTypesOf(DefaultHandler.class).forEach(clazz -> {
            try {
                clazz.getDeclaredConstructor().newInstance().register();
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Gets the http server
     *
     * @return the http server
     */
    public static HttpServer getHttpServer() {
        return server;
    }

    /**
     * Gets the configuration manager
     *
     * @return the configuration manager
     */
    public static ConfigurationManager getConfig() {
        return config;
    }

    /**
     * Gets the data folder
     *
     * @return the data folder
     */
    public static File getDataFolder() {
        return DATA_FOLDER;
    }

    /**
     * Gets the server manager
     *
     * @return the server manager
     */
    public static ServerManager getServerManager() {
        return serverManager;
    }
}

