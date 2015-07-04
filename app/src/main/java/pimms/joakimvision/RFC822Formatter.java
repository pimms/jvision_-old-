package pimms.joakimvision;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pimms on 04.07.15.
 */
public class RFC822Formatter {
    public static final SimpleDateFormat[] rfc822DateFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss z",    Locale.US),
            new SimpleDateFormat("EEE, dd MMM yy HH:mm z",       Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",  Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm z",     Locale.US),
            new SimpleDateFormat("dd MMM yy HH:mm z",            Locale.US),
            new SimpleDateFormat("dd MMM yy HH:mm:ss z",         Locale.US),
            new SimpleDateFormat("dd MMM yyyy HH:mm z",          Locale.US),
            new SimpleDateFormat("dd MMM yyyy HH:mm:ss z",       Locale.US),

            new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss Z",    Locale.US),
            new SimpleDateFormat("EEE, dd MMM yy HH:mm Z",       Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z",  Locale.US),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm Z",     Locale.US),
            new SimpleDateFormat("dd MMM yy HH:mm Z",            Locale.US),
            new SimpleDateFormat("dd MMM yy HH:mm:ss Z",         Locale.US),
            new SimpleDateFormat("dd MMM yyyy HH:mm Z",          Locale.US),
            new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z",       Locale.US)
    };

    public static Date stringToDate(String string) {
        for (int i=0; i<rfc822DateFormats.length; i++) {
            Date date = null;

            try {
                date = rfc822DateFormats[i].parse(string);
            } catch (ParseException ex) {
                /* Care! */
            }

            if (date != null) {
                return date;
            }
        }

        return null;
    }

    public static String dateToString(Date date) {
        // Any format is valid.
        return rfc822DateFormats[0].format(date);
    }
}
