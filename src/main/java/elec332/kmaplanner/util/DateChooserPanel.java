package elec332.kmaplanner.util;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elec332 on 14-8-2019
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DateChooserPanel extends JPanel {

    public DateChooserPanel(Date date){
        this(date, false, true);
    }

    public DateChooserPanel(Date startDate, boolean dayAllowed, boolean timeAllowed){
        boolean b22 = !dayAllowed && timeAllowed;
        setLayout(new GridLayout(b22 ? 2 : 6, 1));
        if (!b22){
            add(new JPanel());
        }
        dtf = new DateTextField(startDate);
        dtf.setDecorated();
        add(dtf);
        cb = new JCheckBox();
        if (dayAllowed) {
            JPanel pnl = new JPanel();
            pnl.add(cb);
            pnl.add(new JLabel("Hele dag"));
            add(pnl);
        }
        sm = new SpinnerDateModel(startDate, null, null, Calendar.HOUR_OF_DAY);
        JSpinner jSpinner1 = new JSpinner(sm);

        cb.addChangeListener(e -> jSpinner1.setVisible(!cb.isSelected()));
        if (timeAllowed) {
            add(jSpinner1);
            JSpinner.DateEditor de = new JSpinner.DateEditor(jSpinner1, "HH:mm");
            jSpinner1.setEditor(de);
        }
    }

    private static int height = 0;

    private JCheckBox cb;
    private DateTextField dtf;
    private SpinnerDateModel sm;

    @SuppressWarnings("deprecation")
    public Date getDate(){
        Date ret = dtf.getDate();
        Date time = sm.getDate();
        ret.setHours(time.getHours());
        ret.setMinutes(time.getMinutes());
        return ret;
    }

    public boolean isFullDay(){
        return cb.isSelected();
    }

    public static Pair<Date, Boolean> run(Component c, Date date, String text, boolean dayAllowed, boolean timeAllowed){
        JDialog dialog;
        JOptionPane pane = new JOptionPane(text, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);

        DateChooserPanel panel = new DateChooserPanel(date, dayAllowed, timeAllowed);
        pane.add(panel);

        boolean[] ref = {false};

        JPanel panl = ((JPanel) pane.getComponents()[1]); //Find pane with OK button
        pane.remove(panl); //Remove the pane, as it sits halfway the screen
        Component component = panl.getComponents()[0]; //Fetch OK button
        ((AbstractButton) component).addActionListener(a -> ref[0] = true);
        panl = new JPanel();
        panl.add(component); //Add ok-button to new pane
        pane.add(panl); //Add panel to bottom of the dialog

        dialog = pane.createDialog(c, "title");
        if (height != 0) {
            dialog.setSize(dialog.getWidth(), height);
        }
        dialog.setVisible(true);
        if (dayAllowed){
            height = dialog.getHeight();
        }
        dialog.dispose();
        Date d;
        boolean sel;
        if (ref[0]){
            d = panel.getDate();
            sel = panel.isFullDay();
        } else {
            d = date;
            sel = false;
        }

        return new Pair<>(d, sel);
    }

}
