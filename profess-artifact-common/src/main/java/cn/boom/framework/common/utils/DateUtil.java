package cn.boom.framework.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {


    public static String dateToStamp(String bgtime, String edtime) throws ParseException {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long from = simpleFormat.parse(bgtime).getTime();
        long to = simpleFormat.parse(edtime).getTime();
        long hour =(to - from) % nd / nh;
        long minute =(to - from) % nd % nh/nm;
        double hours=(double)hour;
        double minutes=(double)minute;
        int a=(int)hours;
        int b=(int)minutes;
        return a+":"+b;
    }


    public static String getNowTime(){

        Date date = new Date();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleFormat.format(date);
    }
}
