package elec332.kmaplanner.gui.planner.filter;

import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.filters.IFilterable;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Elec332 on 26-8-2019
 */
public class JFilterPanel extends JPanel {

    public JFilterPanel(IFilterable filterable) {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Constrictions: "));

        JButton edit = new JButton("Edit");
        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");

        //add(new JLabel("Constrictions: "), BorderLayout.NORTH);

        this.filterable = filterable;
        list = new JList<>(listModel = new DefaultListModel<>());
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setFixedCellWidth(25 * 10);
        JScrollPane listScroller = new JScrollPane(list);
        add(listScroller, BorderLayout.CENTER);
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    edit.doClick();
                }
            }

        });

        JPanel bottom = new JPanel();
        bottom.add(add);
        bottom.add(edit);
        bottom.add(remove);
        add.addActionListener(a -> {
            AbstractFilter filter = EditFilterDialog.getFilter(this);
            if (filter == null){
                return;
            }
            filterable.getFilters().add(filter);
            update();
        });
        edit.addActionListener(a -> {
            AbstractFilter filter = getSelectedFilter();
            if (filter == null){
                return;
            }
            filterable.getFilters().remove(filter);
            filter = filter.copy();
            AbstractFilter e = EditFilterDialog.getFilter(filter, this);
            if (e == null){
                e = filter;
            }
            filterable.getFilters().add(e);
            update();
        });
        remove.addActionListener(a -> {
            AbstractFilter filter = getSelectedFilter();
            if (filter == null){
                return;
            }
            filterable.getFilters().remove(filter);
            update();
        });
        add(bottom, BorderLayout.SOUTH);
    }
    private final JList<AbstractFilter> list;
    private final DefaultListModel<AbstractFilter> listModel;
    private final IFilterable filterable;

    public AbstractFilter getSelectedFilter(){
        return list.getSelectedValue();
    }

    public void update(){
        listModel.clear();
        filterable.getFilters().forEach(listModel::addElement);
    }


}
