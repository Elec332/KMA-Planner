package elec332.kmaplanner;

import elec332.kmaplanner.gui.MainGui;
import elec332.kmaplanner.gui.UISettings;
import elec332.kmaplanner.util.ClassProperties;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Main {

    public static void main(String... args) {
        // Needed cuz MAC is shite...
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        try {
            SwingUtilities.invokeLater(Main::startHandler);
        } catch (Exception e) {
            error(e);
        }
    }

    public static final File UI_SETTINGS_FILE = new File(FileHelper.getExecFolder(), "uisettings.properties");

    private static void error(Throwable e) {
        e.printStackTrace();

        StringBuilder trace = new StringBuilder(e.toString() + "\n\n");
        for (StackTraceElement element : e.getStackTrace()) {
            String s = element.toString();
            if (s.contains("elec332.kmaplanner.Main.startHandler")) {
                break;
            }
            trace.append(s).append("\n");
        }

        JTextArea jta = new JTextArea(trace.toString());
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setPreferredSize(new Dimension(600, 400));
        DialogHelper.showErrorMessageDialog(jsp, "Error");
        System.exit(0);
    }

    private static void startHandler() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> error(e));
        startProgram();
    }

    private static void startProgram() {
        //JFrame.setDefaultLookAndFeelDecorated(true);
        UISettings settings = ClassProperties.readProperties(UISettings.class, UI_SETTINGS_FILE);
        new MainGui(settings);
    }

}
