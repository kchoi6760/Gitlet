package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    static String timeStamp() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return simpleDateFormat.format(date);
    }

    static String initTime() {
        Date date = new Date(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        return simpleDateFormat.format(date);
    }
}
