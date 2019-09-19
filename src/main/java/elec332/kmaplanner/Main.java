package elec332.kmaplanner;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.gui.MainGui;
import elec332.kmaplanner.gui.UISettings;
import elec332.kmaplanner.util.ClassProperties;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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

    public static final File UI_SETTINGS_FILE;
    public static final String ABOUT_TEXT;

    private static final ObjectReference<Boolean> stopKA = new ObjectReference<>(false), KAActive = new ObjectReference<>(false);

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

    public static void stopKeepAlive() {
        if (!KAActive.get()) {
            return;
        }
        stopKA.set(true);
    }

    public static void preventComputerSleepHack() {
        if (KAActive.get()) {
            return;
        }
        new Thread(() -> {
            try {
                Robot hal = new Robot();
                while (true) {
                    if (stopKA.get()) {
                        stopKA.set(false);
                        break;
                    }
                    hal.delay(1000 * 30);
                    Point pObj = MouseInfo.getPointerInfo().getLocation();
                    //System.out.println(pObj.toString() + "x>>" + pObj.x + "  y>>" + pObj.y);
                    hal.mouseMove(pObj.x, pObj.y);
                    //hal.mouseMove(pObj.x + 1, pObj.y + 1);
                    //hal.mouseMove(pObj.x - 1, pObj.y - 1);
                    //pObj = MouseInfo.getPointerInfo().getLocation();
                    //System.out.println(pObj.toString() + "x>>" + pObj.x + "  y>>" + pObj.y);
                }
            } catch (Exception e) {
                error(e);
            }
        }).start();
        KAActive.set(true);
    }

    static {
        UI_SETTINGS_FILE = new File(FileHelper.getExecFolder(), "uisettings.properties");
        InputStream in = Main.class.getClassLoader().getResourceAsStream("about.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Preconditions.checkNotNull(in)));
        ABOUT_TEXT = reader.lines().collect(Collectors.joining("\n"));
    }

}
