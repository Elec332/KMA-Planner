package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.gui.planner.filter.JFilterPanel;
import elec332.kmaplanner.io.PersonExcelReader;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.util.DialogHelper;
import elec332.kmaplanner.util.IOUtils;
import elec332.kmaplanner.util.JCheckBoxList;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 13-8-2019
 */
public class UsersTab extends JPanel {

    public UsersTab(PersonManager personManager, GroupManager groupManager) {
        super(new BorderLayout());
        this.personManager = personManager;
        this.groupManager = groupManager;

        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");
        JButton importB = new JButton("Import");

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
        bottomPanel.add(importB);
        add.addActionListener(e -> editUser(new Person("", ""), true));
        edit.addActionListener(e -> editUser(list.getSelectedValue(), false));
        remove.addActionListener(a -> {
            Person person = list.getSelectedValue();
            if (person != null){
                personManager.removePerson(person);
                updateList();
            }
        });
        importB.addActionListener(a -> importPersons());
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private static final String NOT_SELECTED = "Not Selected";

    private JComboBox<Group> groupFilter;
    private JTextField nameFilter;
    private DefaultListModel<Person> listModel;
    private DefaultComboBoxModel<Group> listModelG;
    private PersonManager personManager;
    private GroupManager groupManager;
    private Runnable groupCallback;

    private boolean refreshingGroups;

    public void setGroupTab(GroupsTab groupTab){
        this.groupCallback = groupTab::updateList;
    }

    private void updateList(){
        if (refreshingGroups){
            return;
        }
        listModel.clear();
        personManager.getPersons().stream()
                .filter(((Group) Objects.requireNonNull(groupFilter.getSelectedItem()))::containsPerson)
                .filter(p -> p.toString().contains(nameFilter.getText()))
                .forEach(listModel::addElement);
    }

    public void updateGroups(){
        refreshingGroups = true;
        listModelG.removeAllElements();
        Set<Group> groups = Sets.newTreeSet(groupManager.getGroups());
        groups.add(GroupManager.EVERYONE);
        groups.forEach(listModelG::addElement);
        refreshingGroups = false;
    }

    private void editUser(final Person person, boolean newP) {
        if (person == null){
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

        Vector<JCheckBox> gVec = groupManager.getGroups().stream().map(g -> {
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


        JFilterPanel filterPanel = new JFilterPanel(person);
        JScrollPane groupScroller = new JScrollPane(groupList);
        JCheckBox prot = new JCheckBox("Sample Text");
        prot.setPreferredSize(new Dimension(filterPanel.getPreferredSize().width, prot.getPreferredSize().height));
        groupList.setPrototypeCellValue(prot);


        group.add(groupScroller);

        middle.add(group);
        middle.add(filterPanel);

        dialog.add(middle, BorderLayout.CENTER);
        if (DialogHelper.showDialog(UsersTab.this, dialog, "Edit Person")){
            if (!newP) {
                personManager.updatePerson(person, person1 -> {
                    groupManager.getGroups().forEach(person1::removeFromGroup);
                    person1.setName(fnf.getText(), lnf.getText());
                });
            } else {
                Person person1 = new Person(fnf.getText(), lnf.getText());
                person1.getFilters().addAll(person.getFilters());
                if (Strings.isNullOrEmpty(person1.getFirstName()) || !personManager.addPersonNice(person1)){
                    JOptionPane.showMessageDialog(UsersTab.this, "Failed to add Person! (Perhaps an invalid/duplicate name?)", "Error adding Person", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            gVec.stream().filter(JCheckBox::isSelected).forEach(c -> person.addToGroup(groupManager.getGroup(c.getText())));
            updateList();
        }

    }

    private void importPersons(){
        JPanel dialog = new JPanel(new BorderLayout());
        JPanel tp = new JPanel();
        tp.add(new JLabel("File location: "));
        JButton fileB = new JButton(NOT_SELECTED);
        File[] fileBf = {null};
        tp.add(fileB);
        fileB.addActionListener(a -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setCurrentDirectory(IOUtils.getExecFolder());
            FileNameExtensionFilter f = new FileNameExtensionFilter("Excel 2007 files (*.xlsx)", "xlsx");
            fc.addChoosableFileFilter(f);
            fc.setFileFilter(f);
            f = new FileNameExtensionFilter("Excel '97 files (*.xls)", "xls");
            fc.addChoosableFileFilter(f);
            fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
            int ret = fc.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                fileBf[0] = fc.getSelectedFile();
                String file = fileBf[0].getAbsolutePath();
                fileB.setText(file.substring(file.lastIndexOf(File.separator) + 1));
            }
        });
        dialog.add(tp, BorderLayout.NORTH);
        JPanel middle = new JPanel(new GridLayout(3, 1));
        JCheckBox usg = new JCheckBox(PersonExcelReader.Options.USE_SHEET_GROUP.toString());
        JCheckBox ufg = new JCheckBox(PersonExcelReader.Options.USE_FILE_GROUP.toString());
        JCheckBox mfsn = new JCheckBox(PersonExcelReader.Options.MERGE_FILE_SHEET_NAME.toString());
        middle.add(usg);
        middle.add(ufg);
        middle.add(mfsn);
        dialog.add(middle);

        JPanel btm = new JPanel();
        btm.add(new JLabel("Group name: "));
        JTextField name = new JTextField(15);
        btm.add(name);
        dialog.add(btm, BorderLayout.SOUTH);

        String[] lastName = {""};

        ufg.addActionListener(a -> {
            if (ufg.isSelected()){
                lastName[0] = name.getText();
                name.setText("");
                name.setEnabled(false);
            } else {
                name.setText(lastName[0]);
                name.setEnabled(true);
            }
        });

        if (DialogHelper.showDialog(UsersTab.this, dialog, "Person Importer")){
            String file = fileB.getText();
            if (file.trim().equals(NOT_SELECTED)){
                JOptionPane.showMessageDialog(UsersTab.this, "File not selected!", "Invalid file", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fn;
            if (!ufg.isSelected()){
                fn = name.getText();
            } else {
                fn = file.substring(file.lastIndexOf(File.separator) + 1);
                fn = fn.substring(0, fn.indexOf('.'));
            }
            Set<Person> people = PersonExcelReader.readPersons(fileBf[0], fn, groupManager, getOption(usg, PersonExcelReader.Options.USE_SHEET_GROUP), getOption(ufg, PersonExcelReader.Options.USE_FILE_GROUP), getOption(mfsn, PersonExcelReader.Options.MERGE_FILE_SHEET_NAME));
            people.forEach(personManager::addPerson);
            updateList();
            groupCallback.run();
        }
    }

    private static PersonExcelReader.Options getOption(JCheckBox checkBox, PersonExcelReader.Options option){
        if (checkBox.isSelected()){
            return option;
        }
        return null;
    }

}
