package elec332.kmaplanner.util.swing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Elec332 on 29-8-2019
 */
public interface IDefaultListCellRenderer<T> extends ListCellRenderer<T> {

    static <T extends JComponent> ListCellRenderer<T> getDefault() {
        return new IDefaultListCellRenderer<T>() {

        };
    }

    Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    @Override
    default Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof JComponent) {
            return applyList(list, (JComponent) value, isSelected);
        } else {
            return applyList(list, createComponent(list, value, index, isSelected, cellHasFocus), isSelected);
        }
    }

    default JComponent createComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        throw new UnsupportedOperationException();
    }

    default <E extends JComponent> E applyList(JList<? extends T> list, E component, boolean isSelected) {
        component.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        component.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        component.setEnabled(list.isEnabled());
        component.setFont(list.getFont());
        if (component instanceof AbstractButton) {
            ((AbstractButton) component).setFocusPainted(false);
            ((AbstractButton) component).setBorderPainted(true);
        }
        component.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        return component;
    }

}
