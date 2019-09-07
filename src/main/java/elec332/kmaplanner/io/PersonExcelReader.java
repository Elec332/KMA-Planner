package elec332.kmaplanner.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import elec332.kmaplanner.project.KMAPlannerProject;
import elec332.kmaplanner.util.FileHelper;
import elec332.kmaplanner.util.swing.DialogHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 16-8-2019
 */
@SuppressWarnings("WeakerAccess")
public class PersonExcelReader {

    private static final String NOT_SELECTED = "Not Selected";

    public static void importPersons(Component parent, KMAPlannerProject project) {
        JPanel dialog = new JPanel(new BorderLayout());
        JPanel tp = new JPanel();
        tp.add(new JLabel("File location: "));
        JButton fileB = new JButton(NOT_SELECTED);
        File[] fileBf = {null};
        tp.add(fileB);
        fileB.addActionListener(a -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setCurrentDirectory(FileHelper.getExecFolder());
            FileNameExtensionFilter f = new FileNameExtensionFilter("Excel 2007 files (*.xlsx)", "xlsx");
            fc.addChoosableFileFilter(f);
            fc.setFileFilter(f);
            f = new FileNameExtensionFilter("Excel '97 files (*.xls)", "xls");
            fc.addChoosableFileFilter(f);
            fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
            int ret = fc.showOpenDialog(parent);
            if (ret == JFileChooser.APPROVE_OPTION) {
                fileBf[0] = fc.getSelectedFile();
                String file = fileBf[0].getAbsolutePath();
                fileB.setText(file.substring(file.lastIndexOf(File.separator) + 1));
            }
        });
        dialog.add(tp, BorderLayout.NORTH);
        JPanel middle = new JPanel(new GridLayout(3, 1));
        JCheckBox usg = new JCheckBox(PersonExcelReader.Options.USE_SHEET_GROUP.toString(), true);
        JCheckBox ufg = new JCheckBox(PersonExcelReader.Options.USE_FILE_GROUP.toString(), true);

        JCheckBox mfsn = new JCheckBox(PersonExcelReader.Options.MERGE_FILE_SHEET_NAME.toString(), true);
        middle.add(usg);
        middle.add(ufg);
        middle.add(mfsn);
        dialog.add(middle);

        JPanel btm = new JPanel();
        btm.add(new JLabel("Group name: "));
        JTextField name = new JTextField(15);
        btm.add(name);
        dialog.add(btm, BorderLayout.SOUTH);
        name.setEnabled(!ufg.isSelected());
        String[] lastName = {""};

        ufg.addActionListener(a -> {
            if (ufg.isSelected()) {
                lastName[0] = name.getText();
                name.setText("");
                name.setEnabled(false);
            } else {
                name.setText(lastName[0]);
                name.setEnabled(true);
            }
        });

        if (DialogHelper.showDialog(parent, dialog, "Person Importer")) {
            String file = fileB.getText();
            if (file.trim().equals(NOT_SELECTED)) {
                DialogHelper.showErrorMessageDialog(parent, "File not selected!", "Invalid file");
                return;
            }
            String fn;
            if (!ufg.isSelected()) {
                fn = name.getText();
            } else {
                fn = file.substring(file.lastIndexOf(File.separator) + 1);
                fn = fn.substring(0, fn.indexOf('.'));
            }
            Set<Person> people = PersonExcelReader.readPersons(fileBf[0], fn, project.getGroupManager(), getOption(usg, Options.USE_SHEET_GROUP), getOption(ufg, Options.USE_FILE_GROUP), getOption(mfsn, Options.MERGE_FILE_SHEET_NAME));
            people.forEach(project.getPersonManager()::addObject);
        }
    }

    private static Options getOption(JCheckBox checkBox, Options option) {
        if (checkBox.isSelected()) {
            return option;
        }
        return null;
    }

    public static Set<Person> readPersons(File file, String group, GroupManager groupManager, Options... options) {
        return readPersons(ExcelReader.openExcelFile(file), group, groupManager, options);
    }

    public static Set<Person> readPersons(Workbook workbook, String group, GroupManager groupManager, Options... optionz) {
        Set<Person> ret = Sets.newTreeSet();
        if (workbook == null) {
            return ret;
        }
        Set<Options> options = Arrays.stream(optionz)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            List<Pair<Person, List<String>>> data = readSheet(sheet);
            data.forEach(d -> {
                Person p = d.getLeft();
                d.getRight().forEach(n -> p.addToGroup(groupManager.getOrCreate(n)));
                if (options.contains(Options.USE_SHEET_GROUP)) {
                    String g = sheet.getSheetName();
                    if (options.contains(Options.MERGE_FILE_SHEET_NAME) && !Strings.isNullOrEmpty(group) || groupManager.getGroup(g) != null) {
                        g = group + " " + g;
                    }
                    Group group1 = groupManager.getOrCreate(g);
                    group1.setMain(true);
                    p.addToGroup(group1);
                }
                if (options.contains(Options.USE_FILE_GROUP) && !Strings.isNullOrEmpty(group)) {
                    p.addToGroup(groupManager.getOrCreate(group));
                }
                ret.add(p);
            });
        }
        return ret;
    }

    private static List<Pair<Person, List<String>>> readSheet(Sheet sheet) {
        List<Pair<Person, List<String>>> ret = Lists.newArrayList();
        sheet.rowIterator().forEachRemaining(row -> {
            String fn = row.getCell(0).getStringCellValue().trim();
            String ln = row.getCell(1).getStringCellValue().trim();
            List<String> groups = Lists.newArrayList();
            for (int i = 2; i < row.getLastCellNum(); i++) {
                String str = row.getCell(i).getStringCellValue();
                if (!Strings.isNullOrEmpty(str) && !Strings.isNullOrEmpty(str.trim())) {
                    groups.add(str.trim());
                }
            }
            ret.add(Pair.of(new Person(fn, ln), groups));
        });
        return ret;
    }

    public enum Options {

        USE_SHEET_GROUP {
            @Override
            public String toString() {
                return "Add sheet name as a group";
            }

        },
        USE_FILE_GROUP {
            @Override
            public String toString() {
                return "Add file name as a group";
            }

        },
        MERGE_FILE_SHEET_NAME {
            @Override
            public String toString() {
                return "Prepend file name to the sheet group name";
            }

        }

    }

}
