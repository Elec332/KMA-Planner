package elec332.kmaplanner;

import com.google.common.base.Strings;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.gui.ProjectInitializerGui;
import elec332.kmaplanner.gui.StartupFileSelector;
import elec332.kmaplanner.gui.planner.PlannerGuiMain;
import elec332.kmaplanner.io.ProjectData;
import elec332.kmaplanner.io.ProjectReader;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.util.SwingUtils;
import elec332.kmaplanner.util.UpdatableTreeSet;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.Set;

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
        if (startupFileSelector.shouldExit()){
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

        ProjectData projectData = projectReader.getProjectData();
        groupManager.load(projectReader);
        System.out.println(groupManager.getGroups());
        personManager.load(projectReader);
        events.addAll(projectReader.getEvents());

        if (projFile == null)
        debug(personManager, groupManager, events);

        Planner planner = new Planner(personManager, groupManager, events, projectData.seed);
        planner.initialize();
        SwingUtils.openPanelAsDialog(new PlannerGuiMain(planner), "KMAPlanner");
        try {
            projectReader.write(personManager, groupManager, events);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    private static File checkFile(File projFile, boolean load){
        String fName = projFile.getAbsolutePath();
        int fsl = fName.lastIndexOf(File.separatorChar);
        String pureFileName = fName.substring(fsl + 1);
        String preFile = fName.substring(0, fsl);
        int dot = pureFileName.indexOf('.');
        if (dot >= 0){
            pureFileName = pureFileName.substring(0, dot);
        }
        if (Strings.isNullOrEmpty(pureFileName)){
            JOptionPane.showMessageDialog(new JFrame(), "Cannot enter empty file name!", "Failed to open Project", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (!pureFileName.endsWith(".kp")){
            pureFileName += ".kp";
        }
        projFile = new File(preFile + File.separator + pureFileName);
        if ((projFile.exists() && !load) || (load && !projFile.exists())){
            String s2 = load ? "doesn't exist..." : " already exists...";
            JOptionPane.showMessageDialog(new JFrame(), "File " + projFile + s2, "Failed to open Project", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return projFile;
    }

    @SuppressWarnings("all")
    private static void debug(PersonManager personManager, GroupManager groupManager, Set<Event> events) {
        if (true) {
            return;
        }
        Group g1, g2, g3;
        groupManager.addGroup(g1 = new Group("Group1"));
        groupManager.addGroup(g2 = new Group("Group2"));
        groupManager.addGroup(g3 = new Group("Group3"));
        groupManager.addGroup(new Group("Group4"));
        groupManager.addGroup(new Group("Group5"));
        groupManager.addGroup(new Group("Group6"));
        groupManager.addGroup(new Group("Group7"));
        groupManager.addGroup(new Group("Group8"));
        groupManager.addGroup(new Group("Group9"));
        personManager.addPerson(new Person("Test", "1").addToGroup(g1));
        personManager.addPerson(new Person("Test", "2").addToGroup(g1));
        personManager.addPerson(new Person("Test", "3").addToGroup(g1));
        personManager.addPerson(new Person("Test", "4").addToGroup(g1));
        personManager.addPerson(new Person("Test", "5").addToGroup(g1).addToGroup(g3));
        personManager.addPerson(new Person("Test", "6").addToGroup(g1).addToGroup(g3));
        personManager.addPerson(new Person("Test", "7").addToGroup(g2).addToGroup(g3));
        personManager.addPerson(new Person("Test", "8").addToGroup(g2).addToGroup(g3));
        personManager.addPerson(new Person("Test", "9").addToGroup(g2));
        personManager.addPerson(new Person("Test", "10").addToGroup(g2));
        personManager.addPerson(new Person("Test", "11").addToGroup(g2));
        personManager.addPerson(new Person("Test", "12").addToGroup(g2));
        personManager.addPerson(new Person("Test", "13").addToGroup(g2));
        personManager.addPerson(new Person("Test", "14").addToGroup(g2));
        personManager.addPerson(new Person("Test", "15").addToGroup(g2));
        personManager.addPerson(new Person("Test", "16").addToGroup(g2));
        personManager.addPerson(new Person("Test", "17").addToGroup(g2));

        events.add(new Event("Test1", new Date(119, 11, 11, 12, 0), new Date(119, 11, 11, 18, 0), 2));
        events.add(new Event("Test2", new Date(119, 11, 11, 13, 0), new Date(119, 11, 11, 14, 0), 9));
        events.add(new Event("Test3", new Date(119, 11, 11, 13, 0), new Date(119, 11, 11, 15, 0), 5));
    }

}
