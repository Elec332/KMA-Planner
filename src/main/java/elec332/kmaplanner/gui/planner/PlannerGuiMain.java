package elec332.kmaplanner.gui.planner;

import elec332.kmaplanner.gui.planner.tabs.GroupsTab;
import elec332.kmaplanner.gui.planner.tabs.PlannerTab;
import elec332.kmaplanner.gui.planner.tabs.SettingsTab;
import elec332.kmaplanner.gui.planner.tabs.UsersTab;
import elec332.kmaplanner.project.KMAPlannerProject;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 14-6-2019
 */
public class PlannerGuiMain extends JPanel {

    public PlannerGuiMain(KMAPlannerProject project) {
        super(new GridLayout(1, 1));
        setPreferredSize(new Dimension(600, 400));
        JTabbedPane tabbedPane = new JTabbedPane();

        UsersTab usersTab = new UsersTab(project.getPersonManager(), project.getGroupManager(), project::markDirty);
        GroupsTab groupsTab = new GroupsTab(project.getGroupManager(), usersTab::updateGroups, project::markDirty);
        usersTab.setGroupTab(groupsTab);


        tabbedPane.addTab("Planner", new PlannerTab(project));
        tabbedPane.addTab("Persons", usersTab);
        tabbedPane.addTab("Groups", groupsTab);
        tabbedPane.addTab("Settings", new SettingsTab(project));

        add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setOpaque(true);
    }

}
