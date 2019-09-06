package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterIO;
import elec332.kmaplanner.planner.opta.assignment.PersonSortingType;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.PlannerSettings;
import elec332.kmaplanner.util.swing.DialogHelper;
import elec332.kmaplanner.util.swing.FileChooserHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Optional;

/**
 * Created by Elec332 on 26-8-2019
 */
public class PlannerTab extends JPanel {

    public PlannerTab(KMAPlannerProject project) {
        super(new BorderLayout());
        this.project = project;
        PlannerSettings settings = project.getPlannerSettings();
        Planner planner = project.getPlanner().orElseThrow(NullPointerException::new);
        planner.initialize();
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
        uis.add(new JLabel("The amount of steps the engine will attempt to improve the previous score before giving up: "));
        JTextField unimp = new JTextField("" + settings.unimprovedSteps, 4);
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

        JPanel b2 = new JPanel();
        JButton plan = new JButton("Plan!");
        JButton continueB = new JButton("Continue planning!");
        b2.add(apply);
        b2.add(plan);
        b2.add(continueB);
        plan.addActionListener(a -> planner.plan(PlannerTab.this));
        continueB.addActionListener(a -> openRoster(planner).ifPresent(roster -> planner.plan(PlannerTab.this, () -> roster)));
        add(b2, BorderLayout.SOUTH);

        apply.addActionListener(a -> {
            PersonSortingType s = (PersonSortingType) sorting.getSelectedItem();
            if (s != null) {
                settings.sortingType = s;
                project.markDirty();
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
                DialogHelper.showErrorMessageDialog(this, err, "Invalid number");
                return;
            }
            if (newSeed != null && timeD != null && unim != null && mgf != null) {
                settings.seed = newSeed;
                settings.timeDiffThreshold = timeD;
                settings.unimprovedSteps = unim;
                settings.mainGroupFactor = mgf;
            } else {
                throw new IllegalStateException();
            }
            project.markDirty();
        });
    }

    private KMAPlannerProject project;

    private Optional<Roster> openRoster(Planner planner) {
        project.saveIfPossible();
        File file = FileChooserHelper.openFileChooser(PlannerTab.this, new FileNameExtensionFilter("KMAPlanner Assignments file (*.kpa)", "kpa"), "Open");
        if (file != null) {
            Roster roster;
            try {
                roster = RosterIO.readRoster(file, planner);
                planner.roster = roster;
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog(PlannerTab.this, "Failed to import assignments from file: " + file.getAbsolutePath(), "Import failed!");
                return Optional.empty();
            }
            return Optional.ofNullable(roster);
        }
        return Optional.empty();
    }

}
