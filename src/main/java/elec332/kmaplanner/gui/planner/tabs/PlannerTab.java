package elec332.kmaplanner.gui.planner.tabs;

import com.google.common.base.Strings;
import elec332.kmaplanner.planner.Planner;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.opta.RosterScoreCalculator;
import elec332.kmaplanner.planner.opta.assignment.PersonSortingType;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.project.PlannerSettings;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by Elec332 on 26-8-2019
 */
public class PlannerTab extends JPanel {

    public PlannerTab(KMAPlannerProject project) {
        super(new BorderLayout());
        PlannerSettings settings = project.getPlannerSettings();
        Planner planner = project.getPlanner().orElseThrow(NullPointerException::new);
        planner.initialize();
        JPanel middle = new JPanel(new BorderLayout());
        JPanel settingsP = new JPanel(new GridLayout(5, 1));
        settingsP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Planner settings: "));


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

        JPanel sc = new JPanel();
        sc.add(new JLabel("Current roster score: "));
        JLabel score = new JLabel("NaN");
        sc.add(score);
        planner.addRosterCallback(this, () -> {
            Roster roster = planner.getRoster();
            String txt;
            sorting.setEnabled(roster == null);
            if (roster == null) {
                txt = "NaN";
            } else {
                txt = RosterScoreCalculator.calculateScore(roster, false).toString();
            }
            score.setText(txt);
        });

        settingsP.add(so);
        settingsP.add(se);
        settingsP.add(td);
        settingsP.add(uis);
        settingsP.add(gf);
        settingsP.setPreferredSize(settingsP.getMinimumSize());
        middle.add(settingsP, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new GridLayout(3, 1));
        bottom.add(new JPanel());
        bottom.add(sc);
        bottom.add(new JPanel());
        middle.add(bottom, BorderLayout.SOUTH);

        add(middle, BorderLayout.CENTER);

        JButton apply = new JButton("Apply planner settings");

        JPanel b2 = new JPanel();
        JButton plan = new JButton("Plan!");
        b2.add(apply);
        b2.add(plan);
        plan.addActionListener(a -> planner.plan(PlannerTab.this));
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

}
