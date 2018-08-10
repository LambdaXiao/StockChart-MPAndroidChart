package com.github.mikephil.charting.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/18.
 */
public class DataTimeUtil {
    public static int overTime = 6000;

    /**
     * 亿元以上格式
     */
    public static String getPointTwo(double number) {
        if (number == 0) {
            return "0";
        }
        String shuzi = number + "";
        if (shuzi.contains("E")) {
            DecimalFormat df = new DecimalFormat("#.##");
            return df.format(Double.parseDouble(shuzi.substring(0, shuzi.length() - 2))) + shuzi.substring(shuzi.length() - 2, shuzi.length());
        }
        return "0";
    }

    /**
     * 普通double保留两位小数
     */
    public static String getPointTwoNo(Double number) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(number);
    }

    public static String choiceUserLine(int local, int pan) {
        if (pan == 0 && local == 0) {
            return "WMCache";
        } else if (pan == 1 && local == 0) {
            return "WSCache";
        } else if (pan == 0 && local == 1) {
            return "NMCache";
        } else if (pan == 1 && local == 1) {
            return "NSCache";
        }
        return "NMCache";
    }

    //浮点型判断
    public static boolean isDecimal(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    //是否为整形
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    // a integer to xx:xx
    public static String secToTime(long time) {
        String timeStr = null;
        timeStr = new SimpleDateFormat("HH:mm").format(new Date(time));
        return timeStr;
    }

    // a integer to xxxxxx
    public static String secToDate(long time) {
        String timeStr = null;
        timeStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date(time));
        return timeStr;
    }

    // a integer to xxxxxx
    public static String secToDateForFiveDay(long time) {
        String timeStr = null;
        timeStr = new SimpleDateFormat("MM-dd").format(new Date(time));
        return timeStr;
    }

    public static String getStartDate() {
        return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
    }

    public static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    public static String getEndPlusOneDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;//年份加一
        int month = calendar.get(Calendar.MONTH) + 1;//月份为当月
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(year) + String.valueOf(unitFormat(month)) + String.valueOf(unitFormat(day));
    }

    public static String getEndTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String formatPhone(String phone) {
        return phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
    }

    public static String formatAccount(String account) {
        return account.charAt(0) + "***" + account.charAt(account.length() - 1);
    }

    public static String formatName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "*";
        } else {
            String temp = "";
            for (int i = 0; i < name.length() - 1; i++) {
                temp += "*";
            }
            return temp + name.charAt(name.length() - 1);
        }
    }

    public static String formatMail(String mail) {
        String[] mails = mail.split("@");
        if (TextUtils.isEmpty(mails[0])) {
            return "*" + mails[1];
        } else {
            if (mails[0].length() == 1 || mails[0].length() == 2) {
                return mails[0] + "@" + mails[1];
            } else {
                return mails[0].charAt(0) + "****" + mails[0].charAt(mails[0].length() - 1) + "@" + mails[1];
            }
        }
    }

    public static String formatCardID(String cardID) {
        if (TextUtils.isEmpty(cardID)) {
            return "****";
        } else if (cardID.length() <= 6) {
            return cardID;
        } else {
            int length = cardID.length() - 6;
            if (length <= 4) {
                return cardID.substring(0, 2) + "****" + cardID.substring(cardID.length() - 4, cardID.length());
            } else {
                String hide = "";
                for (int i = 0; i < length; i++) {
                    hide += "*";
                }
                return cardID.substring(0, 2) + hide + cardID.substring(cardID.length() - 4, cardID.length());
            }
        }
    }

    public static String formatBankCard(String bankCard) {
        if (TextUtils.isEmpty(bankCard)) {
            return "****";
        } else if (bankCard.length() <= 4) {
            return bankCard;
        } else {
            String temp = "";
            for (int i = 0; i < bankCard.length() - 4; i++) {
                temp += "*";
            }
            temp += bankCard.substring(bankCard.length() - 4, bankCard.length());
            return temp;
        }
    }

//    public static boolean isEmail(String email){
//        //正则表达式
//        String regex = "^[A-Za-z]{1,40}@[A-Za-z0-9]{1,40}\\.[A-Za-z]{2,3}$";
//        return email.matches(regex);
//    }

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

    public static String getMonthDay(int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -before);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        return sdf.format(date);
    }

    public static List<String> formatDate(List<String> xValues, boolean isAddFirst) {
        if (isAddFirst || xValues.size() == 1) {
            String today = xValues.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyymmDD");
            Calendar calendar = Calendar.getInstance();//获取日历实例
            try {
                calendar.setTime(sdf.parse(today));
                calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天
                String yesterday = sdf.format(calendar.getTime());//获得前一天
                xValues.add(0, yesterday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < xValues.size(); i++) {
            String temp = xValues.get(i);
            if (temp.length() == 8) {
                xValues.set(i, temp.substring(4, 6) + "/" + temp.substring(6, 8));
            }
        }
        return xValues;
    }

    //获得当天的日期
    public static String lastDay() {
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return dateString;
    }

    public static String yesterday() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    //之前的几周
    public static String getWeekBefore(int week) {
//        String paramStartDate = "";
//        String paramEndDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateNow = new Date();
        Date dateBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateNow);
        cal.add(Calendar.DAY_OF_MONTH, -week * 7);
        dateBefore = cal.getTime();
//        paramEndDate = sdf.format(dateNow);
        return sdf.format(dateBefore);
    }

    //之前的几个月
    public static String getMonthBefore(int month) {
//        String paramStartDate = "";
//        String paramEndDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateNow = new Date();
        Date dateBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateNow);
        cal.add(Calendar.MONTH, -1 * month);
        dateBefore = cal.getTime();
//        paramEndDate = sdf.format(dateNow);
        return sdf.format(dateBefore);
    }

    public static String getYesterday(String today) {
        String yesterday = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmDD");
        Calendar calendar = Calendar.getInstance();//获取日历实例
        try {
            calendar.setTime(sdf.parse(today));
            calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天
            yesterday = sdf.format(calendar.getTime());//获得前一天
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yesterday;
    }

    public static float formatYValue(float value, float axisMaximum) {
        if (axisMaximum >= 100 && axisMaximum < 10000) {
            return ((int) (value / 10)) * 10;
        } else if (axisMaximum >= 10000) {
            return ((int) (value / 100)) * 100;
        }
        return value;
    }

    public static int[] resetMaxValue(float axisMaximum, float axisMinimum) {
        float space = axisMaximum - axisMinimum;
        int[] maxMin = new int[2];
        maxMin[0] = (int) axisMaximum;
        maxMin[1] = (int) axisMinimum;
        if (space <= 6000 && space > 60) {//10的倍数最大间隔200,最小间隔20
            if (space % 60 > 0) {
                space += 60;
            }
            int min = ((int) (axisMinimum / 10)) * 10;
            maxMin[0] = (int) space + min;
            maxMin[1] = min;
        } else if (space < 60000 && space > 6000) {//最小间隔300
            if (space % 600 > 0) {
                space += 600;
            }
            int min = ((int) (axisMinimum / 100)) * 100;
            maxMin[0] = (int) space + min;
            maxMin[1] = min;
        } else if (space > 60000) {
            if (space % 6000 > 0) {
                space += 6000;
            }
            int min = ((int) (axisMinimum / 1000)) * 1000;
            maxMin[0] = (int) space + min;
            maxMin[1] = min;
        }
        return maxMin;
    }

    public static List<Float> formatYValue(List<Float> yValues, boolean isAddFirst) {
        if (isAddFirst || yValues.size() == 1) {
            yValues.add(0, 0f);
        }
//        for(int i = 1;i < yValues.size();i++){
//            yValues.set(i,yValues.get(i)+yValues.get(i-1));
//        }
        return yValues;
    }

}
