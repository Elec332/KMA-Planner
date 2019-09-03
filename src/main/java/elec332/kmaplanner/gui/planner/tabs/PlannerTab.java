package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.RosterPrinter;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterIO;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.ProjectManager;
import elec332.kmaplanner.util.DateHelper;
import elec332.kmaplanner.util.swing.DateChooserPanel;
import elec332.kmaplanner.util.swing.DialogHelper;
import elec332.kmaplanner.util.swing.FileChooserHelper;
import elec332.kmaplanner.util.swing.IDefaultListCellRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Elec332 on 13-8-2019
 */
public class PlannerTab extends JPanel {

    public PlannerTab(KMAPlannerProject project) {
        super(new BorderLayout());
        this.project = project;
        Planner planner = new Planner(project);
        planner.initialize();

        JPanel middle = new JPanel();
        JList<Event> list = new JList<>(listModel = new DefaultListModel<>());
        list.setCellRenderer(new EventCellRenderer());
        updateList();
        JScrollPane eventScroller = new JScrollPane(list);
        {
            JList<Event> dummy = new JList<>(new Event[]{new Event("dummy", new Date(), new Date(), 0)});
            dummy.setCellRenderer(new EventCellRenderer());
            eventScroller.setPreferredSize(new JScrollPane(dummy).getPreferredSize());
        }
        middle.add(eventScroller);
        add(middle);

        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");
        JButton import_ = new JButton("Import events");
        JButton plan = new JButton("Plan!");
        JButton continueB = new JButton("Continue planning!");
        JButton print = new JButton("Print previous run");
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
                project.getEvents().remove(e);
                updateList();
            }
        });
        import_.addActionListener(a -> {
            File file = FileChooserHelper.openFileProjectChooser(PlannerTab.this);
            if (file != null) {
                project.saveIfPossible();
                Set<Event> newEvents = null;
                try {
                    newEvents = Preconditions.checkNotNull(ProjectManager.loadFile(file)).getEvents();
                } catch (Exception e) {
                    DialogHelper.showErrorMessageDialog(PlannerTab.this, "Failed to import events from file: " + file.getAbsolutePath(), "Import failed!");
                }
                if (newEvents != null) {
                    project.getEvents().addAll(newEvents);
                    updateList();
                }
            }
        });
        plan.addActionListener(a -> planner.plan(PlannerTab.this));
        continueB.addActionListener(a -> openRoster(planner).ifPresent(roster -> planner.plan(PlannerTab.this, () -> roster)));
        print.addActionListener(a -> openRoster(planner).ifPresent(roster -> RosterPrinter.printRoster(roster, SwingUtilities.getWindowAncestor(PlannerTab.this))));

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        JPanel b1 = new JPanel();
        JPanel b2 = new JPanel();

        b1.add(add);
        b1.add(edit);
        b1.add(remove);
        b1.add(import_);
        b2.add(plan);
        b2.add(continueB);
        b2.add(print);
        bottom.add(b1);
        bottom.add(b2);

        add(bottom, BorderLayout.SOUTH);
    }

    //private Planner planner;
    private KMAPlannerProject project;
    private DefaultListModel<Event> listModel;

    private Optional<Roster> openRoster(Planner planner) {
        project.saveIfPossible();
        File file = FileChooserHelper.openFileChooser(PlannerTab.this, new FileNameExtensionFilter("KMAPlanner Assignments file (*.kpa)", "kpa"), "Open");
        if (file != null) {
            Roster roster;
            try {
                roster = RosterIO.readRoster(file, planner);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog(PlannerTab.this, "Failed to import assignments from file: " + file.getAbsolutePath(), "Import failed!");
                return Optional.empty();
            }
            return Optional.ofNullable(roster);
        }
        return Optional.empty();
    }

    private void updateList() {
        listModel.clear();
        project.getEvents().forEach(listModel::addElement);
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
                DialogHelper.showErrorMessageDialog(PlannerTab.this, "End date must be after the starting date!", "Invalid date!");
            } else if (!newE) {
                event.name = nameField.getText();
                event.start = start.getDate();
                event.end = end.getDate();
                event.everyone = everyone.isSelected();
                event.requiredPersons = event.everyone ? 0 : (int) snm.getNumber();
                project.getEvents().markObjectUpdated(event);
            } else {
                Event event1 = new Event(nameField.getText(), start.getDate(), end.getDate(), everyone.isSelected() ? 0 : (int) snm.getNumber());
                event1.everyone = everyone.isSelected();
                if (Strings.isNullOrEmpty(event1.name) || event1.getDuration() == 0) {
                    DialogHelper.showErrorMessageDialog(PlannerTab.this, "Failed to add Event! (Perhaps an duplicate name/invalid date?)", "Error adding Event");
                    return;
                }
                project.getEvents().add(event1);
            }
            updateList();
        }
    }

    private static class EventCellRenderer extends JPanel implements IDefaultListCellRenderer<Event> {

        private EventCellRenderer() {
            JTextField dummy = new JTextField(12);
            name = new JLabel("");
            name.setPreferredSize(dummy.getPreferredSize());
            dummy.setColumns(7);
            pers = new JLabel("");
            pers.setPreferredSize(dummy.getPreferredSize());
            dummy.setColumns(25);
            time = new JLabel("");
            time.setPreferredSize(dummy.getPreferredSize());
            add(name);
            add(pers);
            add(time);
            //Dimension pref = getPreferredSize();
            //pref.height = (int) (dummy.getPreferredSize().height);
            //setPreferredSize(pref);
        }

        private final JLabel name, pers, time;

        @Override
        public JComponent createComponent(JList<? extends Event> list, Event value, int index, boolean isSelected, boolean cellHasFocus) {
            name.setText(value.name);
            pers.setText((value.everyone ? "Everyone" : "Pers: " + value.requiredPersons));
            time.setText(DateHelper.getShortDate(value.start) + " -> " + DateHelper.getShortDate(value.end));
            return this;
        }

    }

}
