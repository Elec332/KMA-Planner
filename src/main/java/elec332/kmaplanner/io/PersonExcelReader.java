package elec332.kmaplanner.io;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.kmaplanner.group.Group;
import elec332.kmaplanner.group.GroupManager;
import elec332.kmaplanner.persons.Person;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 16-8-2019
 */
public class PersonExcelReader {

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
            String fn = row.getCell(0).getStringCellValue();
            String ln = row.getCell(1).getStringCellValue();
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
