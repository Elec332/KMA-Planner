package elec332.kmaplanner.util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 13-8-2019
 * <p>
 * Placeholder
 */
public class JPanelBase extends JPanel {

    public JPanelBase(LayoutManager layout) {
        super(layout);
    }

    @SuppressWarnings("all")
    public JPanelBase() {
        super();
    }

    Dialog d;
    boolean noOpen;

}