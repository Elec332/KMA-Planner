package elec332.kmaplanner.planner;

import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.persons.PersonManager;
import elec332.kmaplanner.planner.opta.Roster;
import elec332.kmaplanner.planner.print.DaySheetPrinter;
import elec332.kmaplanner.planner.print.ExportPrinter;
import elec332.kmaplanner.planner.print.ICalPrinter;
import elec332.kmaplanner.planner.print.PersonPrinter;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.IObjectPrinter;
import elec332.kmaplanner.util.ZipHelper;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 30-8-2019
 */
public class RosterPrinter {

    public static void printRoster(Roster roster, Window parent, Consumer<Roster> printer) {
        if (roster == null) {
            return;
        }
        JDialog frame = new JDialog(parent, "Printer", Dialog.DEFAULT_MODALITY_TYPE);
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new JPanel());
        panel.add(new JLabel("Printing timetables..."));
        panel.add(new JPanel());
        panel.setOpaque(true);
        frame.setContentPane(panel);
        frame.setLocationRelativeTo(parent);
        frame.pack();
        new Thread(() -> {
            try {
                printer.accept(roster);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showErrorMessageDialog("Failed to export planner.", "Export failed!");
            }
            frame.dispose();
        }).start();
        frame.setVisible(true);
    }

    public static void printAll(Roster roster) {
        printDefault(roster);
        printDays(roster);
        printPrivateRosters(roster);
    }

    public static void printDefault(Roster roster) {
        roster.apply();
        print(FileHelper.getExecFolder(), "export", roster, new ExportPrinter());
    }

    public static void printDays(Roster roster) {
        roster.apply();
        print(FileHelper.getExecFolder(), "days", roster, new DaySheetPrinter());
    }

    public static void printPrivateRosters(Roster roster) {
        roster.apply();

        File tempFolder = new File(FileHelper.getExecFolder(), UUID.randomUUID().toString());
        if (!tempFolder.mkdirs()) {
            throw new RuntimeException(new IOException()); //No, this doesn't exist, look away please....
        }

        IObjectPrinter<Person> iCal = new ICalPrinter();
        IObjectPrinter<Person> excel = new PersonPrinter();

        roster.getPersons().stream()
                .filter(p -> p != PersonManager.NULL_PERSON)
                .forEach(p -> print(tempFolder, p.getLastName() + " - " + p.getFirstName(), p, excel, iCal));

        try {
            File dest = new File(FileHelper.getExecFolder(), "user_programs.zip");
            Files.deleteIfExists(dest.toPath());
            ZipHelper.zip(tempFolder, dest);
            FileHelper.deleteDir(tempFolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    private static <O> void print(File location, String baseName, O object, IObjectPrinter<? super O>... printers) {
        try {
            IObjectPrinter.print(location, baseName, object, printers);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof FileNotFoundException && e.getCause().getMessage().contains("is being used by another process")) {
                DialogHelper.showErrorMessageDialog("Failed to write to file: " + baseName + "\n This file is open in another program, please close it and try again.", "File IO failed");
                return;
            }
            throw e;
        }
    }

}
