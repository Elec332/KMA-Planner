package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.planner.Event;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.util.DateChooserPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

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
        JButton plan = new JButton("Plan!");
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit.doClick();
                }
            }

        });
        add.addActionListener(a -> editEvent(new Event("", planner.getFirstDate(), planner.getFirstDate(), 0), true));
        edit.addActionListener(a -> editEvent(list.getSelectedValue(), false));
        remove.addActionListener(a -> {
            Event e = list.getSelectedValue();
            if (e != null){
                planner.getEvents().remove(e);
                updateList();
            }
        });
        plan.addActionListener(a -> planner.plan());
        bottom.add(add);
        bottom.add(edit);
        bottom.add(remove);
        bottom.add(plan);
        add(bottom, BorderLayout.SOUTH);
    }

    private Planner planner;
    private DefaultListModel<Event> listModel;

    private void updateList(){
        listModel.clear();
        planner.getEvents().forEach(listModel::addElement);
    }

    private void editEvent(Event event, boolean newE){
        if (event == null){
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
        if (event.everyone){
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

        int ret = JOptionPane.showConfirmDialog(PlannerTab.this, panel, "Edit Event", JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.OK_OPTION){
            if (!start.getDate().before(end.getDate()) && !start.getDate().equals(end.getDate())){
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
                if (Strings.isNullOrEmpty(event1.name) || event1.getDuration() == 0){
                    JOptionPane.showMessageDialog(PlannerTab.this, "Failed to add Event! (Perhaps an duplicate name/invalid date?)", "Error adding Event", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                planner.getEvents().add(event1);
            }
            updateList();
        }
    }

}
