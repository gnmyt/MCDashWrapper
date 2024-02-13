package de.gnmyt.mcdash.api;

import de.gnmyt.mcdash.MCDashWrapper;

import java.io.File;

public class ServerVersionManager {

    private static final Logger Logger = new Logger(ServerVersionManager.class);
    private final File versionFolder;

    /**
     * Creates a new ServerVersionManager.
     * <p>
     * This will create the version folder if it doesn't exist
     * </p>
     */
    public ServerVersionManager() {
        versionFolder = MCDashWrapper.getDataSource("versions");

        if (!versionFolder.exists() && !versionFolder.mkdirs()) {
            Logger.error("An error occurred while creating the version folder");
        }
    }

    /**
     * Installs a specific version of the minecraft server software
     *
     * @param software the software
     * @param version  the version
     */
    public void installVersion(String software, String version) {
        // TODO: Implement this method
    }

    /**
     * Gets the path of a specific version of a software
     *
     * @param software the software
     * @param version  the version
     * @return the path of the version
     */
    public String getPath(String software, String version) {
        File file = new File(versionFolder, software + "-" + version + ".jar");

        if (!file.exists()) {
            installVersion(software, version);
            return getPath(software, version);
        }

        return file.getAbsolutePath();
    }

}
