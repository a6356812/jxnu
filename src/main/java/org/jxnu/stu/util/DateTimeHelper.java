package org.jxnu.stu.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {

    public static String transform(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(date);
//        DateTime dateTime = new DateTime();
//        time = dateTime.toString("yyyy-MM-dd HH:mm:ss");
        return time;
    }

}
