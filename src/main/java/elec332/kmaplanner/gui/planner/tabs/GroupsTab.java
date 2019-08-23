package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Elec332 on 13-8-2019
 */
public class GroupsTab extends JPanel {

    public GroupsTab(final GroupManager groupManager, Runnable callback) {
        super(new BorderLayout());
        this.groupManager = groupManager;
        this.callback = callback;
        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");

        JPanel centerPanel = new JPanel();
        JList<Group> list = new JList<>(listModel = new DefaultListModel<>());
        updateList();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setFixedCellWidth(30 * 10);
        list.setFixedCellHeight(30);
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
        add.addActionListener(a -> editGroup(new Group(""), true));
        edit.addActionListener(e -> editGroup(list.getSelectedValue(), false));
        remove.addActionListener(a -> {
            Group g = list.getSelectedValue();
            if (g != null){
                groupManager.removeGroup(g);
                updateList();
            }
        });
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private DefaultListModel<Group> listModel;
    private GroupManager groupManager;
    private final Runnable callback;

    void updateList(){
        listModel.clear();
        groupManager.getGroups().forEach(listModel::addElement);
        callback.run();
    }

    private void editGroup(Group group, boolean newG){
        if (group == null){
            return;
        }
        JPanel dialog = new JPanel(new BorderLayout());

        JPanel top = new JPanel();
        top.add(new JLabel("Name: "));
        JTextField name = new JTextField(group.getName(), 20);
        top.add(name);
        dialog.add(top, BorderLayout.NORTH);

        int ret = JOptionPane.showConfirmDialog(GroupsTab.this, dialog, "Edit Group", JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.OK_OPTION){
            if (!newG) {
                groupManager.updateGroup(group, group1 -> group1.setName(name.getText()));
            } else {
                Group group1 = new Group(name.getText());
                if (Strings.isNullOrEmpty(group1.getName()) || !groupManager.addGroupNice(group1)){
                    JOptionPane.showMessageDialog(GroupsTab.this, "Failed to add Group! (Perhaps an invalid/duplicate name?)", "Error adding Group", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            updateList();
        }
    }

}
