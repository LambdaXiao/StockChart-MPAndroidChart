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
	Toast toast;
	public static long lTimeMill;
	public static String basepath = Environment.getExternalStorageDirectory().getPath() + "/stxy";
	public static String bgDir = "bgimage";
	public static String adDir = "adimage";
	public static String ugcDir = "ugc";
	public static String sendPicDir = "sendpic";
	public static String againstDir = "against";
	public static String userheadDir = "userhead";
	public static String takephotopath = basepath + "/" + ugcDir + "/takephototemp.jpg";
	public static String takephotoagainstpath = basepath + "/" + againstDir + "/takephototemp.jpg";
	public static String takephotouserheadpath = basepath + "/" + userheadDir + "/userheadtemp.jpg";
	public static String userheadpath = basepath + "/" + userheadDir + "/userhead.jpg";
	public static String shareToken = null;
	public static String shareTID = null;
	public static boolean bChangeHead = false;
	public static boolean bTestFrame = true; // 测试最新框架
	public static boolean bYaoKanHasLogin = false;
	public static boolean bYuLeChanel = true; // 摇一摇和摇看切换标志
	public static boolean bYaoYiYao = true; // 摇一摇和摇看切换标志
	public static boolean bYaoKanEmbed = false; // 是否摇看的嵌入版本
	public static boolean bHNJSYaoYaoKan = false; // 是否湖南经视摇摇看
	public static boolean bAiShenZhen = false;// 是否是爱深圳，true表示是

	public static String cacheName = "responseCache";

	public CommonUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 对网络连接状态进行判断
	 * 
	 * @return true, 可用； false， 不可用
	 */
	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	/**截取名字*/
//	public static String getSubInstrumentName(String name){
//		return name.substring(0,name.length()-7);
//	}

	public static int getWindowWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	public static int getWindowHeight(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	/** 获取手机的IMEI 例如：865769028271447 */
	public static String getImei(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
        return imei;
	}

    /** 获取手机型号 */
    public static String getmType(Context context){
        return Build.MODEL; // 手机型号
    }

    /** 获取手机型号 */
    public static String getmNumber(Context context){
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTm.getLine1Number(); // 手机型号
    }

    //文件存储根目录
    public static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return context.getFilesDir().getAbsolutePath();
    }

	public void hideSoftInputMethod(EditText ed,Activity activity){
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		String methodName = null;
		int currentVersion = Build.VERSION.SDK_INT;
		if(currentVersion >= 16){
			// 4.2
			methodName = "setShowSoftInputOnFocus";  //
		}else if(currentVersion >= 14){
			// 4.0
			methodName = "setSoftInputShownOnFocus";
		}
		if(methodName == null){
			//最低级最不济的方式，这个方式会把光标给屏蔽
			ed.setInputType(InputType.TYPE_NULL);
		}else{
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(ed, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean saveImageToPhone(Bitmap bitmap,Activity activity){
		//如果状态不是mounted，无法读写
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BYLH/";
		//通过UUID生成字符串文件名
//		String fileName1 = UUID.randomUUID().toString();
		//通过Random()类生成数组命名
//		Random random = new Random();
//		String fileName2 = String.valueOf(random.nextInt(Integer.MAX_VALUE));

		Calendar now = new GregorianCalendar();
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		String fileName = simpleDate.format(now.getTime());
		try {
			File file = getFilePath(dir,fileName+".jpg");
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			//保存图片后发送广播通知更新数据库
			Uri uri = Uri.fromFile(file);
			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

//	public static void freeToast() {
//		toast = null;
//		System.gc();
//	}


	public static boolean createSDCardDir(String dirName) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + dirName;
			File path1 = new File(path);
			if (!path1.exists()) {
				path1.mkdirs();
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}

		return false;
	}

    public static Bitmap drawableToBitmapGet(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Bitmap drawableToBitmapCreate(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

	private static final String SUFFIX = ".java";
	public static String classLineMethod() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement targetElement = stackTrace[3];
		String className = targetElement.getClassName();
		String[] classNameInfo = className.split("\\.");
		if (classNameInfo.length > 0) {
			className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
		}

		if (className.contains("$")) {
			className = className.split("\\$")[0] + SUFFIX;
		}

		String methodName = targetElement.getMethodName();
		int lineNumber = targetElement.getLineNumber();

		if (lineNumber < 0) {
			lineNumber = 0;
		}
		return " [ (" + className + ":" + lineNumber + ")#" + methodName + " ] ";
	}

    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

	/**
	 * 判断qq是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
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

	public static String getStrByTime(){
		Date d = new Date();
		int hours = d.getHours();
		if(hours >0 &&hours<12){
			return "早上好";
		}else if(hours>12&&hours<18){
			return "下午好";
		}else {
			return "晚上好";
		}
	}



//	public static void playVideo(Activity activity, String source) {

//		Log.i("playVideo", "playVideo:" + source);

//		if (source != null && !source.equals("")) {
//			Intent intent = new Intent(activity, VideoViewPlayingActivity.class);
//			intent.setData(Uri.parse(source));
//			activity.startActivity(intent);
//			activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//		}
//	}

//	public static void playVideo(Activity activity, String source, String title, String fid, String tid) {
//
//		Log.i("playVideo", "playVideo --- " + source);
//
//		if (source != null && !source.equals("")) {
//			Intent intent = new Intent(activity, VideoViewPlayingActivity.class);
//			intent.setData(Uri.parse(source));
//			if (title != null && !"".equals(title)) {
//				intent.putExtra("title", title);
//			}
//			if (fid != null && !"".equals(fid)) {
//				intent.putExtra("fid", fid);
//			}
//			if (tid != null && !"".equals(tid)) {
//				intent.putExtra("tid", tid);
//			}
//			activity.startActivity(intent);
//			activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//		}
//	}

//	public static void playVideo(Activity activity, String source, String title, String type, String fid, String tid) {
//
//		Log.i("playVideo", "playVideo --- " + source);
//
//		if (source != null && !source.equals("")) {
//			Intent intent = new Intent(activity, VideoViewPlayingActivity.class);
//			intent.setData(Uri.parse(source));
//			if (title != null && !"".equals(title)) {
//				intent.putExtra("title", title);
//			}
//			if (type != null && !"".equals(type)) {
//				intent.putExtra("type", type);
//			}
//			if (fid != null && !"".equals(fid)) {
//				intent.putExtra("fid", fid);
//			}
//			if (tid != null && !"".equals(tid)) {
//				intent.putExtra("tid", tid);
//			}
//			activity.startActivity(intent);
//			activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//		} else {
//			makeToast(activity, "抱歉，该视频无法播放！");
//		}
//	}
	
//	public static void playAudio(Activity activity, String source, String title, String type, String fid, String tid) {
//
////		if (ProfileUtil.get_LoginState(activity) < 0) {
////			makeToast(activity, "抱歉，请登录后收听，谢谢");
////			login(activity);
////			return;
////		}
//		Log.i("playVideo", "playVideo --- " + source);
//
//		if (source != null && !source.equals("")) {
//			Intent intent = new Intent();
//			intent.setClass(activity, AudioPlayingActivity.class);
//			// intent.setData(Uri.parse(source));
//			intent.putExtra("audiosource", source);
//			if (title != null && !"".equals(title)) {
//				intent.putExtra("title", title);
//			}
//			if (type != null && !"".equals(type)) {
//				intent.putExtra("type", type);
//			}
//			if (fid != null && !"".equals(fid)) {
//				intent.putExtra("fid", fid);
//			}
//			if (tid != null && !"".equals(tid)) {
//				intent.putExtra("tid", tid);
//			}
//			activity.startActivity(intent);
//			activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//		}
//	}


//	public static boolean existImage(String url, String dir) {
//		String fileName = MD5Util.getMD5Encoding(url);
//		File file = new File(basepath + "/" + dir + "/" + fileName);
//		if (file.exists()) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	public static boolean saveImage(final String url, final String dir) {
//
//		if (existImage(url, dir)) {
//			return false;
//		}
//
//		Thread thread = new Thread() {
//			public void run() {
//				try {
//					BitmapDrawable bd = (BitmapDrawable) Drawable.createFromStream(new URL(url).openStream(), "image.jpg");
//					Bitmap bm = bd.getBitmap();
//					String fileName = MD5Util.getMD5Encoding(url);
//					File file = new File(basepath + "/" + dir + "/" + fileName);
//					boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//
//					if (sdCardExist) {
//						File maiduo = new File(basepath);
//						File ad = new File(basepath + "/" + dir);
//						// 如果文件夹不存在
//						if (!maiduo.exists()) {
//							// 按照指定的路径创建文件夹
//							maiduo.mkdirs();
//							// 如果文件夹不存在
//						} else if (!ad.exists()) {
//							// 按照指定的路径创建文件夹
//							ad.mkdirs();
//						}
//						if (!file.exists()) {
//							file.createNewFile();
//						}
//						BufferedOutputStream bos;
//						bos = new BufferedOutputStream(new FileOutputStream(file));
//						boolean bol = bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//						bos.flush();
//						bos.close();
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//
//		thread.start();
//		thread = null;
//		return true;
//	}

	public static Drawable getHomePageImg(String dir) {
		Drawable drawable = null;
		String path = basepath + "/" + dir + "/";
		File file = new File(path);
		if (!file.exists()) {
			return drawable;
		}
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				Bitmap bmp = BitmapFactory.decodeFile(path + files[i].getName());
				drawable = new BitmapDrawable(bmp);
				break;
			}
		}
		return drawable;
	}

//	public static void DeleteAllPic(String dir, Context context) {
//		BitmapHelp.getBitmapUtils(context).clearCache();
//		String path = basepath + "/" + dir + "/";
//		File file = new File(path);
//		if (!file.exists()) {
//			return;
//		}
//		File[] files = file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isFile()) {
//				files[i].delete();
//			}
//		}
//	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				return true;
			}
		} catch (Exception e) {
			System.out.println("复制文件操作出错");
			e.printStackTrace();
		}
		return false;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 压缩图片
	 * 
	 * @param imagePath
	 * @param sampleSize
	 * @return
	 */
	public static Bitmap compressPic(String imagePath, int sampleSize) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		int ori = readPictureDegree(imagePath);
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (options.outHeight > 1500 || options.outWidth > 1500) {
			sampleSize = 3;
		}
		if (options.outHeight > 3000 || options.outWidth > 3000) {
			sampleSize = 4;
		}
		if (options.outHeight > 4000 || options.outWidth > 4000) {
			sampleSize = 5;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (ori != 0) {
			Matrix m = new Matrix();
			m.setRotate(ori);
			Bitmap bmpTemp = bitmap;
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			if (bmpTemp != null && !bmpTemp.isRecycled()) {
				bmpTemp.recycle();
			}
		}
		return bitmap;
	}

	public static Bitmap getImageThumbnail(String imagePath, int width, int height, boolean bthumbnail) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		int ori = readPictureDegree(imagePath);

		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		if (ori == 90 || ori == 270) {
			h = options.outWidth;
			w = options.outHeight;
		}
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth + 1;
		} else {
			be = beHeight + 1;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (bthumbnail) {
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}

		if (ori != 0) {
			Matrix m = new Matrix();
			m.setRotate(ori);
			Bitmap bmpTemp = bitmap;
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			if (bmpTemp != null && !bmpTemp.isRecycled()) {
				bmpTemp.recycle();
			}
		}
		// Log.i("getImageThumbnail",
		// "getImageThumbnail--h="+h+"-w="+w+"-bitmap.getWidth()="+
		// bitmap.getWidth()+"-bitmap.getHeight()="+bitmap.getHeight());
		return bitmap;
	}

	/**
	 * 保存图片到指定路径，指定文件名
	 * 
	 * @param bitmap
	 * @param dir
	 *            如: /sdcard/zsnt
	 *            如： temp.jpg
	 * @return
	 */
	public static boolean saveBitmap(Bitmap bitmap, String dir, String filename) {
		try {
			File fileDir = new File(dir);
			File file = new File(dir + "/" + filename);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			if (bitmap != null) {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
				bos.flush();
				bos.close();

				return true;
			}
		} catch (Exception e) {
			//
		}

		return false;
	}

	public static boolean deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists())
			return file.delete();

		return true;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static void createVideoThumbnail(String filePath, String dir, String thumbname) {
		Bitmap videoThumb = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
		// 压缩图
		videoThumb = extractThumbnail(videoThumb, 120, 90);
		saveBitmap(videoThumb, dir, thumbname);
	}

	public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
		int options = ThumbnailUtils.OPTIONS_RECYCLE_INPUT;
		// 利用Bitmpap对象创建缩略图<br>
		return ThumbnailUtils.extractThumbnail(source, width, height, options);
	}

	public static boolean saveVideoPath(String vPath, String content) {
		File file = new File(vPath);
		try {
			file.createNewFile();
			FileWriter fWriter = new FileWriter(file);
			fWriter.write(content);
			fWriter.flush();
			fWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String getVideoPath(String vPath) {
		File file = new File(vPath);
		if (!file.exists()) {
			return "";
		}
		String content = "";
		try {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// 分行读取
				while ((line = buffreader.readLine()) != null) {
					content += line;
				}
				inputreader.close();
				instream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return content;

	}

	/**
	 * 质量压缩压缩到200k
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 图片压缩800*480
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/** 把Uri转化成文件路径 */
	public static String uri2filePath(Context context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
		String picturePath = "";
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
			}
			cursor.close();
		} else {
			if (uri != null) {
				String tmpPath = uri.getPath();
				picturePath = tmpPath;
			}
		}
		return picturePath;
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

    /**
	 * 文件大小转换
	 * @param size
	 * @return
	 */
	public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
 
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

}
