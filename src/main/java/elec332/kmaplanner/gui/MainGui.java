package elec332.kmaplanner.gui;

import com.google.common.base.Preconditions;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.gui.dialogs.AboutPanel;
import elec332.kmaplanner.gui.dialogs.ProjectSettingsPanel;
import elec332.kmaplanner.gui.dialogs.UISettingsPanel;
import elec332.kmaplanner.gui.planner.PlannerGuiMain;
import elec332.kmaplanner.io.PersonExcelReader;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.RosterPrinter;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.ProjectManager;
import elec332.kmaplanner.util.DesktopHelper;
import elec332.kmaplanner.util.ObjectReference;
import elec332.kmaplanner.util.swing.DialogHelper;
import elec332.kmaplanner.util.swing.FileChooserHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Created by Elec332 on 2-9-2019
 */
public class MainGui extends JFrame {

    public MainGui(UISettings settings) {
        project = new ObjectReference<>(ProjectManager.createNewProject());

        ImageIcon img = new ImageIcon(Preconditions.checkNotNull(getClass().getClassLoader().getResource("icons/icon.png")));
        setIconImage(img.getImage());

        settings.apply(this);
        setTitle("KMAPlanner");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        add(mainUI = new JPanel());
        mainUI.setPreferredSize(new Dimension(600, 400));
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newP = new JMenuItem("New");
        JMenuItem load = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save As");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(newP);
        fileMenu.addSeparator();
        fileMenu.add(load);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        newP.addActionListener(a -> {
            if (project.get().isDirty() && !wantsToOverride("start a new project")) {
                return;
            }
            setProject(ProjectManager.createNewProject());
        });
        load.addActionListener(a -> {
            if (project.get().isDirty() && !wantsToOverride("load another project")) {
                return;
            }
            File f = FileChooserHelper.openFileProjectChooser(this);
            if (f == null) {
                return;
            }
            KMAPlannerProject project;
            try {
                project = ProjectManager.loadProject(f);
            } catch (IOException e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog(MainGui.this, "Failed to load project file " + f.getAbsolutePath(), "Failed to open project!");
                project = null;
            }
            if (project != null) {
                setProject(project);
            }
        });
        save.addActionListener(a -> {
            try {
                project.get().save(() -> FileChooserHelper.openFileProjectChooser(MainGui.this, null, "Save"));
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog(MainGui.this, "Failed to save project to file " + project.get().getSaveLocation().orElseThrow(NullPointerException::new).toString(), "Failed to save project!");
            }
        });
        saveAs.addActionListener(a -> {
            File file = FileChooserHelper.openFileProjectChooser(MainGui.this, project.get().getSaveLocation().map(Path::toFile).orElse(null), "Save");
            try {
                project.get().save(file);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog(MainGui.this, "Failed to save project project to file " + file.getAbsolutePath(), "Failed to save project!");
            }
        });
        exit.addActionListener(a -> exit());

        JMenu stuff = new JMenu("Settings");
        JMenuItem uiSettings = new JMenuItem("UI Settings");
        JMenuItem projectSettings = new JMenuItem("Project Settings");
        stuff.add(uiSettings);
        stuff.addSeparator();
        stuff.add(projectSettings);
        uiSettings.addActionListener(a -> UISettingsPanel.openDialog(settings, this));
        projectSettings.addActionListener(a -> ProjectSettingsPanel.openDialog(project.get(), this));

        JMenu importM = new JMenu("Import");
        JMenuItem events = new JMenuItem("Import events");
        JMenuItem person = new JMenuItem("Import persons");
        JMenuItem roster = new JMenuItem("Import previously saved roster");
        importM.add(events);
        importM.add(person);
        importM.addSeparator();
        importM.add(roster);
        events.setToolTipText("Import events from another project.");
        person.setToolTipText("Import persons from an excel file.");
        events.addActionListener(a -> {
            File file = FileChooserHelper.openFileProjectChooser(MainGui.this);
            if (file != null) {
                project.get().saveIfPossible();
                Set<Event> newEvents = null;
                try {
                    newEvents = Preconditions.checkNotNull(ProjectManager.loadFile(file)).getEvents();
                } catch (Exception e) {
                    DialogHelper.showErrorMessageDialog(MainGui.this, "Failed to import events from file: " + file.getAbsolutePath(), "Import failed!");
                }
                if (newEvents != null) {
                    newEvents.forEach(project.get().getEventManager()::addObjectNice);
                }
            }
        });
        person.addActionListener(a -> PersonExcelReader.importPersons(MainGui.this, project.get()));
        roster.addActionListener(a -> project.get().getPlanner().ifPresent(Planner::openRosterFromUI));

        JMenu export = new JMenu("Export");
        JMenuItem printAll = new JMenuItem("Print all");
        JMenuItem printDays = new JMenuItem("Print days");
        JMenuItem printExport = new JMenuItem("Print default export");
        JMenuItem printIndividual = new JMenuItem("Print individual rosters");
        export.add(printAll);
        export.addSeparator();
        export.add(printDays);
        export.add(printExport);
        export.add(printIndividual);
        printAll.addActionListener(a -> RosterPrinter.printRoster(project.get().getPlanner().map(Planner::getRoster).orElse(null), MainGui.this, RosterPrinter::printAll));
        printDays.addActionListener(a -> RosterPrinter.printRoster(project.get().getPlanner().map(Planner::getRoster).orElse(null), MainGui.this, RosterPrinter::printDays));
        printExport.addActionListener(a -> RosterPrinter.printRoster(project.get().getPlanner().map(Planner::getRoster).orElse(null), MainGui.this, RosterPrinter::printDefault));
        printIndividual.addActionListener(a -> RosterPrinter.printRoster(project.get().getPlanner().map(Planner::getRoster).orElse(null), MainGui.this, RosterPrinter::printPrivateRosters));

        JMenu help = new JMenu("Help");
        JMenuItem bug = new JMenuItem("Report a bug");
        JMenuItem projectPage = new JMenuItem("Project page");
        JMenuItem guide = new JMenuItem("Help");
        JMenuItem about = new JMenuItem("About");
        help.add(projectPage);
        help.add(bug);
        help.addSeparator();
        help.add(about);
        help.add(guide);
        about.addActionListener(a -> AboutPanel.openDialog(this));
        bug.addActionListener(a -> DesktopHelper.openWebPage("https://github.com/Elec332/KMA-Planner/issues"));
        projectPage.addActionListener(a -> DesktopHelper.openWebPage("https://github.com/Elec332/KMA-Planner"));
        guide.addActionListener(a -> DesktopHelper.openWebPage("https://github.com/Elec332/KMA-Planner/wiki"));

        menuBar.add(fileMenu);
        menuBar.add(stuff);
        menuBar.add(importM);
        menuBar.add(export);
        menuBar.add(help);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }

        });

        setJMenuBar(menuBar);
        setProject(project.get());
        pack();
        setVisible(true);
    }

    private JPanel mainUI;
    private ObjectReference<KMAPlannerProject> project;

    private void setProject(KMAPlannerProject project) {
        this.project.set(project);
        if (mainUI.getComponentCount() != 0) {
            if (mainUI.getComponentCount() == 1) {
                mainUI.remove(0);
            } else {
                throw new IllegalStateException();
            }
        }
        JPanel view = new PlannerGuiMain(this.project.get());
        mainUI.add(view);
        validate(); //pack?
        view.setVisible(true);
    }

    private void exit() {
        if (project.get().isDirty() && !wantsToOverride("exit this program")) {
            return;
        }
        System.exit(0);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean wantsToOverride(String action) {
        int ret = JOptionPane.showConfirmDialog(this, "You have unsaved changes, are you sure you want to " + action + " without saving your changes first?", "Unsaved changes", JOptionPane.YES_NO_OPTION);
        return ret == JOptionPane.YES_OPTION;
    }

}
