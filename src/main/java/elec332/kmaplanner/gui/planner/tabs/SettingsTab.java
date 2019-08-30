package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.io.ProjectSettings;
import elec332.kmaplanner.planner.opta.assignment.PersonSortingType;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 26-8-2019
 */
public class SettingsTab extends JPanel {

    public SettingsTab(ProjectSettings settings, Runnable save) {
        super(new BorderLayout());
        JPanel middle = new JPanel();

        JPanel se = new JPanel();
        se.add(new JLabel("Seed: "));
        JTextField seed = new JTextField("" + settings.seed, 20);
        se.add(seed);

        JPanel so = new JPanel();
        so.add(new JLabel("Sorting type: "));
        JComboBox<PersonSortingType> sorting = new JComboBox<>(PersonSortingType.values());
        sorting.setSelectedItem(settings.sortingType);
        so.add(sorting);

        JPanel td = new JPanel();
        td.add(new JLabel("Time difference Threshold (minutes): "));
        JTextField timeDiffThr = new JTextField("" + settings.timeDiffThreshold, 4);
        td.add(timeDiffThr);

        JPanel uis = new JPanel();
        uis.add(new JLabel("Time (In seconds) the engine will attempt to improve the previous score before giving up: "));
        JTextField unimp = new JTextField("" + settings.unimprovedSeconds, 4);
        uis.add(unimp);

        JPanel gf = new JPanel();
        gf.add(new JLabel("Min. group size: "));
        JTextField mgs = new JTextField("" + settings.mainGroupFactor, 4);
        gf.add(mgs);

        middle.add(se);
        middle.add(so);
        middle.add(td);
        middle.add(uis);
        middle.add(gf);

        add(middle, BorderLayout.CENTER);

        JButton apply = new JButton("Apply");
        add(apply, BorderLayout.SOUTH);
        apply.addActionListener(a -> {
            PersonSortingType s = (PersonSortingType) sorting.getSelectedItem();
            if (s != null) {
                settings.sortingType = s;
            }
            Long newSeed = null;
            Integer timeD = null, unim = null, mgf = null;
            String err = "";
            try {
                newSeed = Long.parseLong(seed.getText());
            } catch (NumberFormatException e) {
                err += "Invalid seed ";
            }
            try {
                timeD = Integer.parseInt(timeDiffThr.getText());
            } catch (Exception e) {
                err += "Invalid time threshold ";
            }
            try {
                unim = Integer.parseInt(unimp.getText());
            } catch (Exception e) {
                err += "Invalid unimproved time ";
            }
            try {
                mgf = Integer.parseInt(mgs.getText());
            } catch (Exception e) {
                err += "Invalid group size ";
            }
            if (!Strings.isNullOrEmpty(err)) {
                JOptionPane.showMessageDialog(this, err, "Invalid number", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newSeed != null && timeD != null && unim != null && mgf != null) {
                settings.seed = newSeed;
                settings.timeDiffThreshold = timeD;
                settings.unimprovedSeconds = unim;
                settings.mainGroupFactor = mgf;
            } else {
                throw new IllegalStateException();
            }
            save.run();
        });
    }

}
