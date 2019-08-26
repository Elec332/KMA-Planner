package elec332.kmaplanner.util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 26-8-2019
 */
@SuppressWarnings("all")
public class DialogHelper {

    public static boolean showDialog(Component parent, Object data, String title) {
        return showDialog(parent, data, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public static int showDialog(Component parent, Object data, String title, int optionType) {
        return JOptionPane.showConfirmDialog(parent, data, title, optionType, JOptionPane.PLAIN_MESSAGE);
    }

}
