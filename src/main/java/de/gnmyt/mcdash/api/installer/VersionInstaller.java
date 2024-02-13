package de.gnmyt.mcdash.api.installer;

import de.gnmyt.mcdash.api.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;

public interface VersionInstaller {

    Logger LOG = new Logger(VersionInstaller.class);
    OkHttpClient client = new OkHttpClient();

    /**
     * Installs a specific version of the minecraft server software
     *
     * @param software the software
     * @param version  the version
     * @return <code>true</code> if the installation was successful
     */
    boolean installVersion(String software, String version);

    /**
     * Downloads a file from a specific url and saves it to a specific file
     *
     * @param url  the url of the file
     * @param file the file where the file should be saved
     * @return <code>true</code> if the download was successful
     */
    default boolean downloadFile(String url, File file) {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.code() == 200) {
                FileUtils.copyToFile(response.body().byteStream(), file);

                if (file.length() < 2000000) {
                    LOG.error("The file " + file.getName() + " is too small. It may be corrupted.");
                    file.delete();
                    return false;
                }

                return true;
            }

            response.close();
        } catch (Exception e) {
            LOG.error("An error occurred while downloading the file", e);
        }

        return false;
    }

}
