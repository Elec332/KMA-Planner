package elec332.kmaplanner.gui.dialogs;

import elec332.kmaplanner.Main;
import elec332.kmaplanner.gui.UISettings;
import elec332.kmaplanner.util.ClassProperties;
import elec332.kmaplanner.util.swing.DialogHelper;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 3-9-2019
 */
public class UISettingsPanel extends AbstractDialogPanel {

    public static void openDialog(UISettings settings, JFrame owner) {
        UISettingsPanel settingsPanel = new UISettingsPanel(settings);
        if (DialogHelper.showDialog(owner, settingsPanel, "UI Settings")) {
            settingsPanel.apply();
            ClassProperties.writeProperties(settings, Main.UI_SETTINGS_FILE, "UI Settings");
        }
        settings.apply(owner);
    }

    private UISettingsPanel(UISettings settings) {
        Class clazz = settings.getClass();
        List<Field> fields = ClassProperties.getValidFields(clazz).collect(Collectors.toList());
        setLayout(new GridLayout(fields.size(), 1));
        for (Field f : fields) {
            addField(f, settings);
        }

    }

    private void addField(Field field, Object owner) {
        addPanel(setting -> {
            setting.setLayout(new GridLayout(1, 2));
            setting.add(new JLabel(ClassProperties.getPropertyName(field) + " "));
            JComponent component = null;
            ClassProperties.PropertyData propertyData = field.getAnnotation(ClassProperties.PropertyData.class);
            field.setAccessible(true);
            String value;
            try {
                value = (String) field.get(owner);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Supplier<String> s = null;
            if (propertyData != null) {
                String[] valid = ClassProperties.getValidValues(field);
                if (valid != null && valid.length > 0) {
                    JComboBox<String> comboBox = new JComboBox<>(valid);
                    comboBox.setSelectedItem(value);
                    component = comboBox;
                    s = () -> (String) comboBox.getSelectedItem();
                } else if (propertyData.number() != Number.class) {
                    NumberFormat format = NumberFormat.getIntegerInstance();
                    NumberFormatter numberFormatter = new NumberFormatter(format);
                    numberFormatter.setValueClass(propertyData.number());
                    numberFormatter.setAllowsInvalid(false);

                    JFormattedTextField textField = new JFormattedTextField(numberFormatter);
                    textField.setValue(value);
                    component = textField;
                    s = textField::getText;
                }
            }
            if (component == null) {
                JTextField textField = new JTextField(value);
                component = textField;
                s = textField::getText;
            }
            final Supplier<String> v = s;
            setting.add(component);
            return () -> {
                try {
                    field.set(owner, v.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        });
    }

}
