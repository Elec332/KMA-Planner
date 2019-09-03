package elec332.kmaplanner.gui;

import elec332.kmaplanner.util.ClassProperties;

import javax.swing.*;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 3-9-2019
 */
public class UISettings {

    @ClassProperties.PropertyData(value = "Look and Feel", dynamicValidValues = LookAndFeelFilter.class)
    private String lookAndFeel = UIManager.getLookAndFeel().getName();

    public void apply(JFrame frame) {
        try {
            UIManager.setLookAndFeel(Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter(lfi -> lfi.getName().equals(lookAndFeel))
                    .map(UIManager.LookAndFeelInfo::getClassName)
                    .findFirst()
                    .orElse(UIManager.getLookAndFeel().getClass().getCanonicalName())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
    }

    public static class LookAndFeelFilter implements Supplier<String[]> {

        @Override
        public String[] get() {
            return Arrays.stream(UIManager.getInstalledLookAndFeels()).map(UIManager.LookAndFeelInfo::getName).toArray(String[]::new);
        }

    }

}

