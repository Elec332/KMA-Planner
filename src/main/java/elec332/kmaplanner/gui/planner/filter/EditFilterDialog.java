package elec332.kmaplanner.gui.planner.filter;

import elec332.kmaplanner.filters.AbstractFilter;
import elec332.kmaplanner.filters.FilterManager;
import elec332.kmaplanner.util.DialogHelper;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 24-8-2019
 */
public class EditFilterDialog extends JPanel {

    @Nullable
    public static AbstractFilter getFilter(Component parent) {
        return getFilter(null, parent);
    }

    @Nullable
    public static AbstractFilter getFilter(AbstractFilter original, Component parent) {
        EditFilterDialog dialog = new EditFilterDialog(original);
        if (DialogHelper.showDialog(parent, dialog, "Edit Event")) {
            dialog.cB.forEach(Runnable::run);
            if (!dialog.filter.isValid()) {
                JOptionPane.showMessageDialog(parent, "Filter settings invalid, please check the settings.", "Invalid filter", JOptionPane.ERROR_MESSAGE);
                return getFilter(dialog.filter, parent);
            }
            return dialog.filter;
        }
        return null;
    }

    private EditFilterDialog(AbstractFilter filter) {
        super(new BorderLayout());
        this.initNull = filter == null;

        this.filter = filter;
        DefaultComboBoxModel<Supplier<AbstractFilter>> filterModel = new DefaultComboBoxModel<>();
        filterModel.addElement(null);
        FilterManager.INSTANCE.getFilters().forEach(filterModel::addElement);
        filterModel.setSelectedItem(null);

        JPanel middle = new JPanel();
        middle.setSize(300, 400);
        cB = new ArrayList<>();
        this.middle = filter == null ? new JPanel() : filter.createConfigPanel(cB::add);
        middle.add(this.middle);

        JPanel top = new JPanel();
        JComboBox<Supplier<AbstractFilter>> select = new JComboBox<>(filterModel);
        top.add(select);
        select.addActionListener(a -> {
            @SuppressWarnings("unchecked")
            Supplier<AbstractFilter> sel = (Supplier<AbstractFilter>) select.getSelectedItem();
            JPanel newPanel = null;
            if (sel == null) {
                if (initNull) {
                    newPanel = new JPanel();
                } else {
                    return;
                }
            }
            if (newPanel == null) {
                AbstractFilter newF = sel.get();
                if (this.filter != null && newF.getClass() == this.filter.getClass()) {
                    return;
                }
                cB = new ArrayList<>();
                newPanel = newF.createConfigPanel(cB::add);
                this.filter = newF;
            }
            middle.remove(this.middle);
            this.middle = newPanel;
            middle.add(this.middle);
            //middle.validate();
            //middle.repaint();

            Window win = SwingUtilities.getWindowAncestor(middle);
            win.setSize(win.getPreferredSize());
            win.validate();
            win.repaint();

        });

        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);

    }

    private AbstractFilter filter;
    private JPanel middle;
    private List<Runnable> cB;
    private final boolean initNull;

}
