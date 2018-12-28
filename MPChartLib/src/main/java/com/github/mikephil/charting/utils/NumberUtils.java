package com.github.mikephil.charting.utils;

/**
 * Created by Administrator on 2016/8/4.
 */

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.github.mikephil.charting.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数字格式化工具类
 *
 * @author LiuJunGuang
 * @date 2013-1-26上午10:44:08
 */
public class NumberUtils {
    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
     * <pre>
     * 	"3.1415926", 1			--> 3.1
     * 	"3.1415926", 3			--> 3.142
     * 	"3.1415926", 4			--> 3.1416
     * 	"3.1415926", 6			--> 3.141593
     * 	"1234567891234567.1415926", 3	--> 1234567891234567.142
     * </pre>
     *
     * @param //String类型的数字对象
     * @param precision       小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    public static String keepPrecision(String number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 对float类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return float 如果数值较大，则使用科学计数法表示
     */
    public static String keepPrecision(float number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 用于计算
     * 对double类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return double 如果数值较大，则使用科学计数法表示
     */
    public static double keepPrecision(double number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 用于格式化显示
     *
     * @param number
     * @param precision
     * @return
     */
    public static String keepPrecisionR(double number, int precision) {
        String str = "######0";
        for (int i = 0; i < precision; i++) {
            if (i == 0) {
                str += ".0";
            } else {
                str += "0";
            }
        }
        DecimalFormat df = new DecimalFormat(str);
        return df.format(number);
    }

    public static String keepPrecision2(double number) {
        DecimalFormat mFormat = new DecimalFormat("#0.00");
        return mFormat.format(number);
    }


    public static void limitDecimal(CharSequence s, EditText view, int limitLen) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > limitLen) {
                s = s.toString().subSequence(0, s.toString().indexOf(".") + limitLen + 1);
                view.setText(s);
                view.setSelection(s.length());
            }
        }
        //处理首个数字是.的问题
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            view.setText(s);
            view.setSelection(2);
        }
        //处理最后数字是.的问题
//        if(s.toString().trim().substring(s.length()-1).equals(".")){
//            s = s.subSequence(0,s.toString().indexOf("."));
//            view.setText(s);
//            view.setSelection(s.length());
//        }
        //处理首个数字是0，第二个数字不是.的问题
        if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                view.setText(s.subSequence(0, 1));
                view.setSelection(1);
                return;
            }
        }
    }

    public static String stringNoE10(double double1) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");//格式化设置
        return decimalFormat.format(double1);
    }

    public static double stringNoE10ForVol(double double1) {
        BigDecimal d1 = new BigDecimal(Double.toString(double1));
        BigDecimal d2 = new BigDecimal(Integer.toString(1));
        // 四舍五入,保留2位小数
        return d1.divide(d2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

        });
    }

    /**
     * 格式化成交量,(开启千位分隔符)
     *
     * @param vol 成交量
     * @return
     */
    public static String formatVol(Context context, String assetId, double vol) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setGroupingUsed(true);
        String str;
        if (vol >= 100000000) {
            str = df.format(vol / 100000000) + (assetId.endsWith(".HK") ? context.getResources().getString(R.string.billions_gu) : context.getResources().getString(R.string.billions_shou));
        } else if (vol >= 10000) {
            str = df.format(vol / 10000) + (assetId.endsWith(".HK") ? context.getResources().getString(R.string.millions_gu) : context.getResources().getString(R.string.millions_shou));
        } else {
            str = df.format(Math.round(vol)) + (assetId.endsWith(".HK") ? context.getResources().getString(R.string.gu) : context.getResources().getString(R.string.shou));
        }
        return str;
    }

    public static double String2Double(String strVal) {
        try {
            double doubleVal = Double.parseDouble(strVal);
            return doubleVal;
        } catch (Exception e) {

        }
        return 0;
    }
}

