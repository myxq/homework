package edu.neu.homework.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Util {

    /**
     * @description 字符串处理，防止SQL注入
     * @param input
     * @return
     */
    public static String StringHandle(String input){
        String output;
        // 将包含有 单引号(')，分号(;) 和 注释符号(--)的语句替换掉
        output = input.trim().replaceAll(".*([';]+|(--)+).*", " ");
        return output;
    }

    /**
     * Toast封装
     * @param context
     * @param msg
     */
    public static void makeToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 根据生日推算年龄
     * @param birthDay
     * @return
     */
    public static int getAgeFromDate(Date birthDay){
        int age;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthDay);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * Date中提取年月日
     * @param date
     * @return
     */
    public static int getDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static int getMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
    public static int getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据日期构造Date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date formDate(int year,int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        return calendar.getTime();
    }
    public static Date formDate(com.haibin.calendarview.Calendar cal){
        Calendar calendar = Calendar.getInstance();
        calendar.set(cal.getYear(),cal.getMonth()-1,cal.getDay());
        return calendar.getTime();
    }
    public static Date formDate(int year,int month,int day,int hour,int min,int sec){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,min,sec);
        return calendar.getTime();
    }

    /**
     * 将date转换为calendar
     * @param date
     * @return
     */
    public static com.haibin.calendarview.Calendar dateToCalendar(Date date){
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setDay(getDay(date));
        calendar.setMonth(getMonth(date)+1);
        calendar.setYear(getYear(date));
        calendar.setSchemeColor(Color.WHITE);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme("");
        return calendar;
    }

    /**
     * 判断sd卡是否存在
     * @return
     */
    public static boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
