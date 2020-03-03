package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.events.Event;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.util.DateHelper;
import elec332.kmaplanner.util.swing.DateChooserPanel;
import elec332.kmaplanner.util.swing.DialogHelper;
import elec332.kmaplanner.util.swing.IDefaultListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

/**
 * Created by Elec332 on 13-8-2019
 */
public class EventsTab extends JPanel {

    public EventsTab(KMAPlannerProject project) {
        super(new BorderLayout());
        this.project = project;

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
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit.doClick();
                }
            }

        });
        add.addActionListener(a -> editEvent(new Event("", project.getEventManager().getLastDate(), project.getEventManager().getLastDate(), 0), true));
        edit.addActionListener(a -> editEvent(list.getSelectedValue(), false));
        remove.addActionListener(a -> {
            Event e = list.getSelectedValue();
            if (e != null) {
                project.getEventManager().removeObject(e);
                updateList();
            }
        });

        project.getEventManager().addCallback(this, this::updateList);

        JPanel bottom = new JPanel();
        bottom.add(add);
        bottom.add(edit);
        bottom.add(remove);
        add(bottom, BorderLayout.SOUTH);
    }

    private KMAPlannerProject project;
    private DefaultListModel<Event> listModel;

    private void updateList() {
        listModel.clear();
        project.getEventManager().forEach(listModel::addElement);
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

        if (DialogHelper.showDialog(EventsTab.this, panel, "Edit Event")) {
            if (!start.getDate().before(end.getDate()) && !start.getDate().equals(end.getDate())) {
                DialogHelper.showErrorMessageDialog(EventsTab.this, "End date must be after the starting date!", "Invalid date!");
            } else if (!newE) {
                project.getEventManager().updateObject(event, event1 -> {
                    event1.name = nameField.getText();
                    event1.start = start.getDate();
                    event1.end = end.getDate();
                    event1.everyone = everyone.isSelected();
                    event1.requiredPersons = event.everyone ? 0 : (int) snm.getNumber();
                });
            } else {
                Event event1 = new Event(nameField.getText(), start.getDate(), end.getDate(), everyone.isSelected() ? 0 : (int) snm.getNumber(), event.getUuid());
                event1.everyone = everyone.isSelected();
                if (Strings.isNullOrEmpty(event1.name) || event1.getDuration() == 0 || !project.getEventManager().addObjectNice(event1)) {
                    DialogHelper.showErrorMessageDialog(EventsTab.this, "Failed to add Event! (Perhaps an duplicate name/invalid date?)", "Error adding Event");
                    return;
                }
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
            dummy.setColumns(24);
            time = new JLabel("");
            time.setPreferredSize(dummy.getPreferredSize());
            add(name);
            add(pers);
            add(time);
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
