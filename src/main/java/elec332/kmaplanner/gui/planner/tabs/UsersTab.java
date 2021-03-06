package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.gui.planner.filter.JFilterPanel;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.util.JCheckBoxList;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 13-8-2019
 */
public class UsersTab extends JPanel {

    public UsersTab(PersonManager personManager, GroupManager groupManager, Runnable dirtyMarker) {
        super(new BorderLayout());
        this.personManager = personManager;
        this.groupManager = groupManager;

        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");

        JPanel top = new JPanel(new GridLayout(2, 1));
        JPanel p = new JPanel();
        p.add(new JLabel("Filters"));
        top.add(p);
        JPanel topFilters = new JPanel();
        topFilters.add(new JLabel("Group"));
        groupFilter = new JComboBox<>(listModelG = new DefaultComboBoxModel<>());
        updateGroups();
        groupFilter.setSelectedItem(GroupManager.EVERYONE);
        groupFilter.addActionListener(a -> updateList());
        topFilters.add(groupFilter);
        topFilters.add(new JPanel());
        topFilters.add(new JLabel("Name"));
        nameFilter = new JTextField("", 15);
        topFilters.add(nameFilter);
        top.add(topFilters);
        add(top, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        JList<Person> list = new JList<>(listModel = new DefaultListModel<>());
        updateList();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setFixedCellWidth(30 * 10);
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
        add.addActionListener(e -> editUser(new Person("", ""), true, dirtyMarker));
        edit.addActionListener(e -> editUser(list.getSelectedValue(), false, dirtyMarker));
        remove.addActionListener(a -> {
            Person person = list.getSelectedValue();
            if (person != null) {
                personManager.removeObject(person);
                updateList();
            }
        });

        personManager.addCallback(this, this::updateList);
        groupManager.addCallback(this, this::updateGroups);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JComboBox<Group> groupFilter;
    private JTextField nameFilter;
    private DefaultListModel<Person> listModel;
    private DefaultComboBoxModel<Group> listModelG;
    private PersonManager personManager;
    private GroupManager groupManager;

    private boolean refreshingGroups;

    private void updateList() {
        if (refreshingGroups) {
            return;
        }
        listModel.clear();
        personManager.stream()
                .filter(((Group) Objects.requireNonNull(groupFilter.getSelectedItem()))::containsPerson)
                .filter(p -> p.toString().contains(nameFilter.getText()))
                .forEach(listModel::addElement);
    }

    private void updateGroups() {
        refreshingGroups = true;
        listModelG.removeAllElements();
        Set<Group> groups = Sets.newTreeSet(groupManager.getObjects());
        groups.add(GroupManager.EVERYONE);
        groups.forEach(listModelG::addElement);
        refreshingGroups = false;
    }

    private void editUser(final Person person, boolean newP, Runnable dirtyMarker) {
        if (person == null) {
            return;
        }
        JPanel dialog = new JPanel(new BorderLayout());

        JPanel names = new JPanel(new GridLayout(2, 1));
        JPanel fn = new JPanel();
        fn.add(new JLabel("First Name: "));
        JTextField fnf = new JTextField(person.getFirstName(), 8);
        fn.add(fnf);
        JPanel ln = new JPanel();
        JTextField lnf = new JTextField(person.getLastName(), 8);
        ln.add(new JLabel("Last Name: "));
        ln.add(lnf);
        names.add(fn);
        names.add(ln);
        dialog.add(names, BorderLayout.NORTH);

        JPanel middle = new JPanel(new GridLayout(2, 1));

        JPanel group = new JPanel();
        group.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Groups: "));

        Vector<JCheckBox> gVec = groupManager.stream().map(g -> {
            JCheckBox ret = new JCheckBox(g.toString());
            ret.setSelected(person.getGroups().contains(g));
            return ret;
        }).distinct().collect(Collectors.toCollection(Vector::new));

        JCheckBoxList<JCheckBox> groupList = new JCheckBoxList<>();
        groupList.setModel(new AbstractListModel<JCheckBox>() {

            @Override
            public int getSize() {
                return gVec.size();
            }

            @Override
            public JCheckBox getElementAt(int i) {
                return gVec.elementAt(i);
            }

        });


        JFilterPanel filterPanel = new JFilterPanel(person, dirtyMarker);
        JScrollPane groupScroller = new JScrollPane(groupList);
        JCheckBox prot = new JCheckBox("Sample Text");
        prot.setPreferredSize(new Dimension(filterPanel.getPreferredSize().width, prot.getPreferredSize().height));
        groupList.setPrototypeCellValue(prot);

        group.add(groupScroller);

        middle.add(group);
        middle.add(filterPanel);

        dialog.add(middle, BorderLayout.CENTER);
        if (DialogHelper.showDialog(UsersTab.this, dialog, "Edit Person")) {
            if (!newP) {
                personManager.updateObject(person, person1 -> {
                    groupManager.forEach(person1::removeFromGroup);
                    person1.setName(fnf.getText(), lnf.getText());
                });
            } else {
                Person person1 = new Person(fnf.getText(), lnf.getText());
                person1.getModifiableFilters().addAll(person.getFilters());
                if (Strings.isNullOrEmpty(person1.getFirstName()) || !personManager.addObjectNice(person1)) {
                    DialogHelper.showErrorMessageDialog(UsersTab.this, "Failed to add Person! (Perhaps an invalid/duplicate name?)", "Error adding Person");
                    return;
                }
            }
            gVec.stream().filter(JCheckBox::isSelected).forEach(c -> person.addToGroup(groupManager.getGroup(c.getText())));
            updateList();
        }

    }

}
