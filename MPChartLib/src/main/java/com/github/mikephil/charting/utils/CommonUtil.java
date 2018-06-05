package com.github.mikephil.charting.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextPaint;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	public static int getWindowWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	public static int getWindowHeight(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

    /**
     * 功能：判断一个字符串是否包含特殊字符
     * @param string 要判断的字符串
     * @return true 提供的参数string不包含特殊字符
     * @return false 提供的参数string包含特殊字符
     */
    public static boolean isConSpeCharacters(String string) {
        if(string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()==0){
            //如果不包含特殊字符
            return true;
        }
        return false;
    }

    public static boolean isConCharacters(String string){
        if(string.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()==0){
            return true;
        }else{
            return false;
        }
    }

    //浮点型判断
	public static boolean isDecimal(String str) {
		if(str==null || "".equals(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
		return pattern.matcher(str).matches();
	}

    //整形判断
	public static boolean isNumeric(String str){
		for (int i = str.length();--i>=0;){
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("[1][34578]\\d{9}");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isChar(String mobiles) {
		Pattern p = Pattern.compile("[a-zA-Z]");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isNUM(String mobiles) {
		Pattern p = Pattern.compile("[1-9]");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isZeroString(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		Pattern p = Pattern.compile("[0]+");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 计算TextView 的宽度
	 * @param textView
	 * @param text
	 * @return
	 */
	public static float getTextViewLength(TextView textView,String text){  
		TextPaint paint = textView.getPaint();  
		// 得到使用该paint写上text的时候,像素为多少  
		float textLength = paint.measureText(text);  
		return textLength;  
	}

    /**根据最大长度获取文本需要设置的字体*/
    public static int getTextSize(TextView textView,String text,int allLength){
        int size = 13;
        TextPaint paint = textView.getPaint();
        while (paint.measureText(text) >= allLength){
            size -= 1;
            textView.setTextSize(size);
        }
        return size;
    }


}
