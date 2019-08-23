package elec332.kmaplanner.gui.planner;

import elec332.kmaplanner.gui.planner.tabs.GroupsTab;
import elec332.kmaplanner.gui.planner.tabs.PlannerTab;
import elec332.kmaplanner.gui.planner.tabs.UsersTab;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.util.JPanelBase;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 14-6-2019
 */
public class PlannerGuiMain extends JPanelBase {

    public PlannerGuiMain(Planner planner) {
        super(new GridLayout(1, 1));
        setPreferredSize(new Dimension(600, 400));
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Planner", new PlannerTab(planner));
        UsersTab usersTab = new UsersTab(planner.getPersonManager(), planner.getGroupManager());
        tabbedPane.addTab("Persons", usersTab);
        GroupsTab groupsTab = new GroupsTab(planner.getGroupManager(), usersTab::updateGroups);
        tabbedPane.addTab("Groups", groupsTab);
        usersTab.setGroupTab(groupsTab);

        add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

}
