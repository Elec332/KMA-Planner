package elec332.kmaplanner;

import com.google.common.base.Strings;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.gui.ProjectInitializerGui;
import elec332.kmaplanner.gui.StartupFileSelector;
import elec332.kmaplanner.gui.planner.PlannerGuiMain;
import elec332.kmaplanner.io.ProjectReader;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.util.SwingUtils;
import elec332.kmaplanner.util.UpdatableTreeSet;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Elec332 on 14-6-2019
 */
public class Main {

    public static void main(String... args) {
        try {
            SwingUtilities.invokeLater(Main::startHandler);
        } catch (Exception e) {
            error(e);
        }
    }

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
        JOptionPane.showMessageDialog(new JFrame(), jsp, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    private static void startHandler() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> error(e));
        startProgram();
    }

    private static void startProgram() {
        StartupFileSelector startupFileSelector = SwingUtils.openPanelAsDialog(new StartupFileSelector(), "Start");
        if (startupFileSelector.shouldExit()) {
            System.exit(0);
        }
        File projFile = startupFileSelector.getProjectFile();

        GroupManager groupManager = new GroupManager();
        PersonManager personManager = new PersonManager(groupManager);
        UpdatableTreeSet<Event> events = new UpdatableTreeSet<>();

        ProjectReader projectReader;
        try {
            if (projFile == null) {
                ProjectInitializerGui init = SwingUtils.openPanelAsDialog(new ProjectInitializerGui(), "Choose project location");
                projectReader = new ProjectReader(checkFile(init.getProjectFile(), false), init.getProjectData());
                projectReader.write(personManager, groupManager, events);
            } else {
                projectReader = new ProjectReader(checkFile(projFile, true)).read();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ProjectSettings projectData = projectReader.getProjectData();
        groupManager.load(projectReader);
        personManager.load(projectReader);
        events.addAll(projectReader.getEvents());

        Planner planner = new Planner(personManager, groupManager, events, projectData);
        planner.initialize();
        SwingUtils.openPanelAsDialog(new PlannerGuiMain(planner), "KMAPlanner");
        try {
            projectReader.write(personManager, groupManager, events);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    private static File checkFile(File projFile, boolean load) {
        String fName = projFile.getAbsolutePath();
        int fsl = fName.lastIndexOf(File.separatorChar);
        String pureFileName = fName.substring(fsl + 1);
        String preFile = fName.substring(0, fsl);
        int dot = pureFileName.indexOf('.');
        if (dot >= 0) {
            pureFileName = pureFileName.substring(0, dot);
        }
        if (Strings.isNullOrEmpty(pureFileName)) {
            JOptionPane.showMessageDialog(new JFrame(), "Cannot enter empty file name!", "Failed to open Project", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (!pureFileName.endsWith(".kp")) {
            pureFileName += ".kp";
        }
        projFile = new File(preFile + File.separator + pureFileName);
        if ((projFile.exists() && !load) || (load && !projFile.exists())) {
            String s2 = load ? "doesn't exist..." : " already exists...";
            JOptionPane.showMessageDialog(new JFrame(), "File " + projFile + s2, "Failed to open Project", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return projFile;
    }

}
