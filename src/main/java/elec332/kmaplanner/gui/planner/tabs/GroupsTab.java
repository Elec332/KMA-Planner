package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.gui.planner.filter.JFilterPanel;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Elec332 on 13-8-2019
 */
public class GroupsTab extends JPanel {

    public GroupsTab(final GroupManager groupManager, Runnable dirtyMarker) {
        super(new BorderLayout());
        this.groupManager = groupManager;
        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");

        JPanel centerPanel = new JPanel();
        JList<Group> list = new JList<>(listModel = new DefaultListModel<>());
        updateList();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setFixedCellWidth(30 * 10);
        list.setFixedCellHeight(20);
        JScrollPane listScroller = new JScrollPane(list);
        centerPanel.add(listScroller);
        add(centerPanel, BorderLayout.CENTER);
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit.doClick();
                }
            }

        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(add);
        bottomPanel.add(edit);
        bottomPanel.add(remove);
        add.addActionListener(a -> editGroup(new Group(""), true, dirtyMarker));
        edit.addActionListener(e -> editGroup(list.getSelectedValue(), false, dirtyMarker));
        remove.addActionListener(a -> {
            Group g = list.getSelectedValue();
            if (g != null) {
                groupManager.removeObject(g);
                updateList();
            }
        });
        add(bottomPanel, BorderLayout.SOUTH);

        groupManager.addCallback(this, this::updateList);
    }

    private DefaultListModel<Group> listModel;
    private GroupManager groupManager;

    private void updateList() {
        listModel.clear();
        groupManager.forEach(listModel::addElement);
    }

    private void editGroup(Group group, boolean newG, Runnable dirtyMarker) {
        if (group == null) {
            return;
        }
        JPanel dialog = new JPanel(new BorderLayout());

        JPanel top = new JPanel();
        top.add(new JLabel("Name: "));
        JTextField name = new JTextField(group.getName(), 20);
        //DialogHelper.requestFocusInDialog(name);
        top.add(name);

        JCheckBox checkBox = new JCheckBox("Is main group? ");
        checkBox.setSelected(group.isMainGroup());
        top.add(checkBox);

        dialog.add(top, BorderLayout.NORTH);

        dialog.add(new JFilterPanel(group, dirtyMarker), BorderLayout.CENTER);

        if (DialogHelper.showDialog(GroupsTab.this, dialog, "Edit Group")) {
            if (!newG) {
                groupManager.updateObject(group, group1 -> {
                    group1.setMain(checkBox.isSelected());
                    group1.setName(name.getText());
                });
            } else {
                Group group1 = new Group(name.getText());
                group1.setMain(checkBox.isSelected());
                group1.getModifiableFilters().addAll(group.getFilters());
                if (Strings.isNullOrEmpty(group1.getName()) || !groupManager.addObjectNice(group1)) {
                    DialogHelper.showErrorMessageDialog(GroupsTab.this, "Failed to add Group! (Perhaps an invalid/duplicate name?)", "Error adding Group");
                    return;
                }
            }
            updateList();
        }
    }

}
