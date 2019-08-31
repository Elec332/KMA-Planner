package elec332.kmaplanner.gui.planner;

import elec332.kmaplanner.gui.planner.tabs.GroupsTab;
import elec332.kmaplanner.gui.planner.tabs.PlannerTab;
import elec332.kmaplanner.gui.planner.tabs.SettingsTab;
import elec332.kmaplanner.gui.planner.tabs.UsersTab;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.util.swing.SwingHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 14-6-2019
 */
public class PlannerGuiMain extends JPanel {

    public PlannerGuiMain(Planner planner) {
        super(new GridLayout(1, 1));
        setPreferredSize(new Dimension(600, 400));
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel usersTab, groupsTab, settingsTab;
        if (planner != null) {
            UsersTab usersTab_ = new UsersTab(planner.getPersonManager(), planner.getGroupManager());
            GroupsTab groupsTab_ = new GroupsTab(planner.getGroupManager(), usersTab_::updateGroups);
            usersTab_.setGroupTab(groupsTab_);
            usersTab = usersTab_;
            groupsTab = groupsTab_;
            settingsTab = new SettingsTab(planner.getSettings(), planner::saveProject);
        } else {
            usersTab = new JPanel();
            groupsTab = new JPanel();
            settingsTab = new JPanel();
        }

        tabbedPane.addTab("Planner", new PlannerTab(planner));
        tabbedPane.addTab("Persons", usersTab);
        tabbedPane.addTab("Groups", groupsTab);
        tabbedPane.addTab("Settings", settingsTab);

        add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        if (planner == null) {
            SwingHelper.setEnabledAll(this, false);
        }
        setOpaque(true);
    }

}
