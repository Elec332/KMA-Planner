package elec332.kmaplanner.util;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

/**
 * Created by Elec332 on 4-9-2019
 */
@SuppressWarnings("WeakerAccess")
public class DesktopHelper {

    public static void openWebPage(String page) {
        try {
            openWebPage(new URL(page));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openWebPage(URL url) {
        try {
            openWebPage(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openWebPage(URI uri) {
        getDesktop().ifPresent(desktop -> {
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static boolean isDesktopActionSupported(Desktop.Action action) {
        return getDesktop()
                .map(desktop -> desktop.isSupported(action))
                .orElse(false);
    }

    public static Optional<Desktop> getDesktop() {
        if (Desktop.isDesktopSupported()) {
            return Optional.of(Desktop.getDesktop());
        }
        return Optional.empty();
    }

}
