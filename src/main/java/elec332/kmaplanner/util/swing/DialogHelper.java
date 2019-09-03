package elec332.kmaplanner.util.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 26-8-2019
 */
@SuppressWarnings("all")
public class DialogHelper {

    public static void showErrorMessageDialog(Object message, String title) {
        showErrorMessageDialog(null, message, title);
    }

    public static void showErrorMessageDialog(Component parent, Object message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showDialog(Component parent, Object data, String title) {
        return showDialog(parent, data, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public static int showDialog(Component parent, Object data, String title, int optionType) {
        return JOptionPane.showConfirmDialog(parent, data, title, optionType, JOptionPane.PLAIN_MESSAGE);
    }

}
