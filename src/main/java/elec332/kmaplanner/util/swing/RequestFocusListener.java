package elec332.kmaplanner.util.swing;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Created by Elec332 on 3-9-2019
 */
public class RequestFocusListener implements AncestorListener {

    @Override
    public void ancestorAdded(AncestorEvent event) {
        event.getComponent().requestFocusInWindow();
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {

    }

    @Override
    public void ancestorMoved(AncestorEvent event) {

    }

}
