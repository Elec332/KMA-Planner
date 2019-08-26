package elec332.kmaplanner.gui.planner.tabs;

import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.util.PersonSortingType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 26-8-2019
 */
public class SettingsTab extends JPanel {

    public SettingsTab(ProjectSettings settings) {
        super(new BorderLayout());
        JPanel middle = new JPanel();
        JTextField seed = new JTextField("" + settings.seed, 20);
        JComboBox<PersonSortingType> sorting = new JComboBox<>(PersonSortingType.values());
        JPanel se = new JPanel();
        se.add(new JLabel("Seed: "));
        se.add(seed);
        JPanel so = new JPanel();
        so.add(new JLabel("Sorting type: "));
        so.add(sorting);
        middle.add(se);
        middle.add(so);
        add(middle, BorderLayout.CENTER);

        JButton apply = new JButton();
        add(apply, BorderLayout.SOUTH);
        apply.addActionListener(a -> {
            PersonSortingType s = (PersonSortingType) sorting.getSelectedItem();
            if (s != null) {
                settings.sortingType = s;
            }
            Long newSeed = null;
            try {
                newSeed = Long.parseLong(seed.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid seed", "Invalid seed", JOptionPane.ERROR_MESSAGE);
            }
            if (newSeed != null) {
                settings.seed = newSeed;
            }
        });
    }

}
