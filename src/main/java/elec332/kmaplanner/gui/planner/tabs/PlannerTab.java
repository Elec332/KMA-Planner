package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.Main;
import elec332.kmaplanner.io.ProjectReader;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.util.DateChooserPanel;
import elec332.kmaplanner.util.DialogHelper;
import elec332.kmaplanner.util.FileChooserHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

/**
 * Created by Elec332 on 13-8-2019
 */
public class PlannerTab extends JPanel {

    public PlannerTab(Planner planner) {
        super(new BorderLayout());
        this.planner = planner;

        JPanel middle = new JPanel();
        JList<Event> list = new JList<>(listModel = new DefaultListModel<>());
        updateList();
        middle.add(list);
        add(middle);

        JPanel bottom = new JPanel();
        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");
        JButton import_ = new JButton("Import events");
        JButton plan = new JButton("Plan!");
        JButton continueB = new JButton("Continue planning!");
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit.doClick();
                }
            }

        });
        add.addActionListener(a -> editEvent(new Event("", planner.getLastDate(), planner.getLastDate(), 0), true));
        edit.addActionListener(a -> editEvent(list.getSelectedValue(), false));
        remove.addActionListener(a -> {
            Event e = list.getSelectedValue();
            if (e != null) {
                planner.getEvents().remove(e);
                updateList();
            }
        });
        import_.addActionListener(a -> {
            File file = FileChooserHelper.openFileProjectChooser(PlannerTab.this);
            if (file != null) {
                planner.saveProject();
                file = Main.checkFile(file, true, ".kp");
                Set<Event> newEvents = null;
                try {
                    newEvents = new ProjectReader(file).read().getEvents();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PlannerTab.this, "Failed to import events from file: " + file.getAbsolutePath(), "Import failed!", JOptionPane.ERROR_MESSAGE);
                }
                if (newEvents != null) {
                    planner.getEvents().addAll(newEvents);
                    updateList();
                }
            }
        });
        plan.addActionListener(a -> planner.plan(PlannerTab.this));
        continueB.addActionListener(a -> {
            planner.saveProject();
            File file = FileChooserHelper.openFileChooser(PlannerTab.this, new FileNameExtensionFilter("KMAPlanner Assignments file (*.kpa)", "kpa"));
            if (file != null){
                file = Main.checkFile(file, true, ".kpa");
                Roster roster;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    roster = Roster.readRoster(fis, planner);
                    fis.close();
                } catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PlannerTab.this, "Failed to import assignments from file: " + file.getAbsolutePath(), "Import failed!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                planner.plan(PlannerTab.this, () -> roster);
            }
        });

        bottom.add(add);
        bottom.add(edit);
        bottom.add(remove);
        bottom.add(import_);
        bottom.add(plan);
        bottom.add(continueB);
        add(bottom, BorderLayout.SOUTH);
    }

    private Planner planner;
    private DefaultListModel<Event> listModel;

    private void updateList() {
        listModel.clear();
        planner.getEvents().forEach(listModel::addElement);
    }

    private void editEvent(Event event, boolean newE) {
        if (event == null) {
            return;
        }
        JPanel panel = new JPanel(new BorderLayout());
        JPanel name = new JPanel();
        name.add(new JLabel("Name: "));
        JTextField nameField = new JTextField(event.name, 15);
        name.add(nameField);
        name.add(new JPanel());
        name.add(new JLabel("Persons: "));
        SpinnerNumberModel snm = new SpinnerNumberModel(event.requiredPersons, 0, Short.MAX_VALUE, 1);
        JSpinner spinner = new JSpinner(snm);
        name.add(spinner);
        name.add(new JPanel());
        JCheckBox everyone = new JCheckBox("Everyone");
        everyone.addActionListener(a1 -> spinner.setEnabled(!everyone.isSelected()));
        if (event.everyone) {
            spinner.setEnabled(false);
            everyone.setSelected(true);
        }
        name.add(everyone);
        panel.add(name, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3));
        DateChooserPanel start = new DateChooserPanel(event.start);
        DateChooserPanel end = new DateChooserPanel(event.end);
        JPanel cl = new JPanel();
        JPanel cr = new JPanel();
        cl.add(new JLabel("Start date: "));
        cl.add(start);
        cr.add(new JLabel("End date:"));
        cr.add(end);
        center.add(cl);
        center.add(new JPanel()); //spacer
        center.add(cr);

        panel.add(center);

        if (DialogHelper.showDialog(PlannerTab.this, panel, "Edit Event")) {
            if (!start.getDate().before(end.getDate()) && !start.getDate().equals(end.getDate())) {
                JOptionPane.showMessageDialog(PlannerTab.this, "End date must be after the starting date!", "Invalid date!", JOptionPane.ERROR_MESSAGE);
            } else if (!newE) {
                event.name = nameField.getText();
                event.start = start.getDate();
                event.end = end.getDate();
                event.everyone = everyone.isSelected();
                event.requiredPersons = event.everyone ? 0 : (int) snm.getNumber();
                planner.getEvents().markObjectUpdated(event);
            } else {
                Event event1 = new Event(nameField.getText(), start.getDate(), end.getDate(), everyone.isSelected() ? 0 : (int) snm.getNumber());
                event1.everyone = everyone.isSelected();
                if (Strings.isNullOrEmpty(event1.name) || event1.getDuration() == 0) {
                    JOptionPane.showMessageDialog(PlannerTab.this, "Failed to add Event! (Perhaps an duplicate name/invalid date?)", "Error adding Event", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                planner.getEvents().add(event1);
            }
            updateList();
        }
    }

}
