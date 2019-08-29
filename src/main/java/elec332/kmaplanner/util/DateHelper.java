package elec332.kmaplanner.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 29-8-2019
 */
public class DateHelper {

    private static final Supplier<Calendar> CALENDAR_SUPPLIER = Calendar::getInstance;

    private static final String SPLITTER = ",";
    private static final String DAYS = "Zondag,Maandag,Dinsdag,Woensdag,Donderdag,Vrijdag,Zaterdag";
    private static final String MONTHS = "Januari,Februari,Maart,April,Mei,Juni,Juli,Augustus,September,Oktober,November,December";

    private static final SimpleDateFormat SD_ADDITIONAL = new SimpleDateFormat("yyyy HH:mm");
    private static final SimpleDateFormat SHORT_DATE = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

    public static String getShortDate(Date date) {
        return SHORT_DATE.format(date);
    }

    public static Calendar getCalendar(Date date) {
        Calendar ret = getCalendar();
        ret.setTime(date);
        return ret;
    }

    public static Calendar getCalendar() {
        return CALENDAR_SUPPLIER.get();
    }

    public static String getNiceString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String ret = "";
        ret += DAYS.split(SPLITTER)[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        ret += " ";
        ret += calendar.get(Calendar.DAY_OF_MONTH);
        ret += " ";
        ret += MONTHS.split(SPLITTER)[calendar.get(Calendar.MONTH)];
        ret += " ";
        ret += SD_ADDITIONAL.format(date);
        return ret;
    }

    public static boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = getCalendar(date1);
        Calendar cal2 = getCalendar(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    static {
        try {
            for (Field f : DateHelper.class.getDeclaredFields()) {
                if (f.getType() == SimpleDateFormat.class) {
                    ((SimpleDateFormat) f.get(null)).setCalendar(getCalendar());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DateHelper!");
        }
    }

}
