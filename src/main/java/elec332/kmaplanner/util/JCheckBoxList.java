package elec332.kmaplanner.util;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Elec332 on 13-8-2019
 */
public class JCheckBoxList<E extends JCheckBox> extends JList<E> {

    public JCheckBoxList() {
        setCellRenderer(IDefaultListCellRenderer.getDefault());

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox checkbox = getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                }
            }

        });

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}