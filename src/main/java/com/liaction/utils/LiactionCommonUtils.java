package com.liaction.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by CHEN.SI LIACTION on 2015/4/5 0005.
 * 此工具类包含了一些网上的开源工具,感谢原作者,本人只是做了简单收集
 * 并加入一些自己开发中用到的一些方法
 */
public class LiactionCommonUtils {
    /**
     * Android App 相关辅助类
     */
    public static class AppUtils {

        private AppUtils() {
        /* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");

        }

        /**
         * 获取应用程序名称
         */
        public static String getAppName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        context.getPackageName(), 0);
                int labelRes = packageInfo.applicationInfo.labelRes;
                return context.getResources().getString(labelRes);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * [获取应用程序版本名称信息]
         *
         * @param context
         * @return 当前应用的版本名称
         */
        public static String getVersionName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        context.getPackageName(), 0);
                return packageInfo.versionName;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * [获取应用程序版本Code]
         *
         * @param context
         * @return 当前应用的版本名称
         */
        public static int getVersionCode(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        context.getPackageName(), 0);
                return packageInfo.versionCode;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return -1;
        }


        /**
         * 获得apk签名信息*
         *
         * @param context
         */
        public static String getAPKSingInfo(Context context) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                Signature[] signs = packageInfo.signatures;
                Signature sign = signs[0];
                return parseSignature(sign.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private static String parseSignature(byte[] signature) {
            try {
                CertificateFactory certFactory = CertificateFactory
                        .getInstance("X.509");
                X509Certificate cert = (X509Certificate) certFactory
                        .generateCertificate(new ByteArrayInputStream(signature));
                String publickey = cert.getPublicKey().toString();
                String signNumber = cert.getSerialNumber().toString();
                String sigAlgName = cert.getSigAlgName();
                return publickey + signNumber + sigAlgName;
            } catch (CertificateException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * 常用单位转换的辅助类
     */

    public static class DensityUtils {
        private DensityUtils() {
        /* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * dp转px
         *
         * @param context
         * @param dpVal
         * @return
         */
        public static int dp2px(Context context, float dpVal) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, context.getResources().getDisplayMetrics());
        }

        /**
         * sp转px
         *
         * @param context
         * @param spVal
         * @return
         */
        public static int sp2px(Context context, float spVal) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spVal, context.getResources().getDisplayMetrics());
        }

        /**
         * px转dp
         *
         * @param context
         * @param pxVal
         * @return
         */
        public static float px2dp(Context context, float pxVal) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (pxVal / scale);
        }

        /**
         * px转sp
         *
         * @param context
         * @param pxVal
         * @return
         */
        public static float px2sp(Context context, float pxVal) {
            return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
        }

    }


    /**
     * Json结果解析类
     */
    public static class JsonParser {

        public static String parseIatResult(String json) {
            StringBuffer ret = new StringBuffer();
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    // 转写结果词，默认使用第一个结果
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret.toString();
        }

        public static String parseGrammarResult(String json) {
            StringBuffer ret = new StringBuffer();
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject obj = items.getJSONObject(j);
                        if (obj.getString("w").contains("nomatch")) {
                            ret.append("没有匹配结果.");
                            return ret.toString();
                        }
                        ret.append("【结果】" + obj.getString("w"));
                        ret.append("【置信度】" + obj.getInt("sc"));
                        ret.append("\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ret.append("没有匹配结果.");
            }
            return ret.toString();
        }
    }

    /**
     * 打开或关闭软键盘
     */
    public static class KeyBoardUtils {
        /**
         * 打卡软键盘
         *
         * @param mEditText 输入框
         * @param mContext  上下文
         */
        public static void openKeybord(EditText mEditText, Context mContext) {
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        /**
         * 关闭软键盘
         *
         * @param mEditText 输入框
         * @param mContext  上下文
         */
        public static void closeKeybord(EditText mEditText, Context mContext) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
    }

    /**
     * Logcat统一管理类
     */
    public static class L {

        private L() {
        /* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
        private static final String TAG = "test";

        // 下面四个是默认tag的函数
        public static void i(String msg) {
            if (isDebug)
                Log.i(TAG, msg);
        }

        public static void d(String msg) {
            if (isDebug)
                Log.d(TAG, msg);
        }

        public static void e(String msg) {
            if (isDebug)
                Log.e(TAG, msg);
        }

        public static void v(String msg) {
            if (isDebug)
                Log.v(TAG, msg);
        }

        // 下面是传入自定义tag的函数
        public static void i(String tag, String msg) {
            if (isDebug)
                Log.i(tag, msg);
        }

        public static void d(String tag, String msg) {
            if (isDebug)
                Log.i(tag, msg);
        }

        public static void e(String tag, String msg) {
            if (isDebug)
                Log.i(tag, msg);
        }

        public static void v(String tag, String msg) {
            if (isDebug)
                Log.i(tag, msg);
        }
    }

    /**
     * 自定义一些常用方法
     */
    public static class LiactionUtils {

        public static boolean OPEN_TEST_LOG = true;//是否打开控制台log

        /**
         * 判断给定字符串是否为空
         * <p/>
         * 空 : null "" " "
         * <p/>
         * 2015-2-27 11:49:20
         *
         * @param charSequence
         * @return 为空返回true, 否则返回false
         */
        public static boolean isEmpty(CharSequence charSequence) {
            String string = "";
            if (charSequence instanceof String) {
                string = (String) charSequence;
                string = string.trim();

                if (TextUtils.isEmpty(string)) {
                    return true;
                }

                if ("".equals(string)) {
                    return true;
                }

                if (" ".equals(string)) {
                    return true;
                }
            } else {
                if (TextUtils.isEmpty(charSequence)) {
                    return true;
                }
            }


            return false;
        }

        public static void showTestLog(String logText) {
            if (OPEN_TEST_LOG) {
                Log.i("test", logText);
            }
        }

        public static void setListViewHeight(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }

        /**
         * 隐藏软键盘*
         *
         * @param context
         */
        public static void hideSoftInput(Activity context) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        /**
         * 获取当前时间
         *
         * @return
         */
        public static String getSystemCurrentTime() {

            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        }


    }

    /**
     * 跟网络相关的工具类
     */
    public static class NetUtils {
        private NetUtils() {
		/* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * 判断网络是否连接
         *
         * @param context
         * @return
         */
        public static boolean isConnected(Context context) {

            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (null != connectivity) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (null != info && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * 判断是否是wifi连接
         */
        public static boolean isWifi(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null)
                return false;
            return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

        }

        /**
         * 打开网络设置界面
         */
        public static void openSetting(Activity activity) {
            Intent intent = new Intent("/");
            ComponentName cm = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(cm);
            intent.setAction("android.intent.action.VIEW");
            activity.startActivityForResult(intent, 0);
        }

    }

    /**
     * 获得屏幕相关的辅助类
     */
    public static class ScreenUtils {
        private ScreenUtils() {
		/* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * 获得屏幕高度
         *
         * @param context
         * @return
         */
        public static int getScreenWidth(Context context) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        }

        /**
         * 获得屏幕宽度
         *
         * @param context
         * @return
         */
        public static int getScreenHeight(Context context) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.heightPixels;
        }

        /**
         * 获得状态栏的高度
         *
         * @param context
         * @return
         */
        public static int getStatusHeight(Context context) {

            int statusHeight = -1;
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusHeight = context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return statusHeight;
        }

        /**
         * 获取当前屏幕截图，包含状态栏
         *
         * @param activity
         * @return
         */
        public static Bitmap snapShotWithStatusBar(Activity activity) {
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bmp = view.getDrawingCache();
            int width = getScreenWidth(activity);
            int height = getScreenHeight(activity);
            Bitmap bp = null;
            bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
            view.destroyDrawingCache();
            return bp;

        }

        /**
         * 获取当前屏幕截图，不包含状态栏
         *
         * @param activity
         * @return
         */
        public static Bitmap snapShotWithoutStatusBar(Activity activity) {
            View view = activity.getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bmp = view.getDrawingCache();
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            int width = getScreenWidth(activity);
            int height = getScreenHeight(activity);
            Bitmap bp = null;
            bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                    - statusBarHeight);
            view.destroyDrawingCache();
            return bp;

        }

    }

    /**
     * SD卡相关的辅助类
     */
    public static class SDCardUtils {
        private SDCardUtils() {
		/* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * 判断SDCard是否可用
         *
         * @return
         */
        public static boolean isSDCardEnable() {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);

        }

        /**
         * 获取SD卡路径
         *
         * @return
         */
        public static String getSDCardPath() {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator;
        }

        /**
         * 获取SD卡的剩余容量 单位byte
         *
         * @return
         */
        public static long getSDCardAllSize() {
            if (isSDCardEnable()) {
                StatFs stat = new StatFs(getSDCardPath());
                // 获取空闲的数据块的数量
                long availableBlocks = (long) stat.getAvailableBlocks() - 4;
                // 获取单个数据块的大小（byte）
                long freeBlocks = stat.getAvailableBlocks();
                return freeBlocks * availableBlocks;
            }
            return 0;
        }

        /**
         * 获取指定路径所在空间的剩余可用容量字节数，单位byte
         *
         * @param filePath
         * @return 容量字节 SDCard可用空间，内部存储可用空间
         */
        public static long getFreeBytes(String filePath) {
            // 如果是sd卡的下的路径，则获取sd卡可用容量
            if (filePath.startsWith(getSDCardPath())) {
                filePath = getSDCardPath();
            } else {// 如果是内部存储的路径，则获取内存存储的可用容量
                filePath = Environment.getDataDirectory().getAbsolutePath();
            }
            StatFs stat = new StatFs(filePath);
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            return stat.getBlockSize() * availableBlocks;
        }

        /**
         * 获取系统存储路径
         *
         * @return
         */
        public static String getRootDirectoryPath() {
            return Environment.getRootDirectory().getAbsolutePath();
        }

        /**
         * 从路径获取文件名
         * @param pathandname
         * @return
         */
        public static String getFileName(String pathandname) {
            int start = pathandname.lastIndexOf("/");
            int end = pathandname.lastIndexOf(".");
            if (start != -1 && end != -1) {
                return pathandname.substring(start + 1, end);
            } else {
                return null;
            }
        }

    }

    /**
     * SharedPreferences相关类
     */
    public static class SPUtils {
        public SPUtils() {
		/* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * 保存在手机里面的文件名
         */
        public static final String FILE_NAME = "share_data";

        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         *
         * @param context
         * @param key
         * @param object
         */
        public static void put(Context context, String key, Object object) {

            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (object instanceof String) {
                editor.putString(key, (String) object);
            } else if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, object.toString());
            }

            SharedPreferencesCompat.apply(editor);
        }

        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         *
         * @param context
         * @param key
         * @param defaultObject
         * @return
         */
        public static Object get(Context context, String key, Object defaultObject) {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);

            if (defaultObject instanceof String) {
                return sp.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return sp.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return sp.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return sp.getLong(key, (Long) defaultObject);
            }

            return null;
        }

        /**
         * 移除某个key值已经对应的值
         *
         * @param context
         * @param key
         */
        public static void remove(Context context, String key) {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            SharedPreferencesCompat.apply(editor);
        }

        /**
         * 清除所有数据
         *
         * @param context
         */
        public static void clear(Context context) {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            SharedPreferencesCompat.apply(editor);
        }

        /**
         * 查询某个key是否已经存在
         *
         * @param context
         * @param key
         * @return
         */
        public static boolean contains(Context context, String key) {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            return sp.contains(key);
        }

        /**
         * 返回所有的键值对
         *
         * @param context
         * @return
         */
        public static Map<String, ?> getAll(Context context) {
            SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
            return sp.getAll();
        }

        /**
         * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
         *
         * @author zhy
         */
        private static class SharedPreferencesCompat {
            private static final Method sApplyMethod = findApplyMethod();

            /**
             * 反射查找apply的方法
             *
             * @return
             */
            @SuppressWarnings({"unchecked", "rawtypes"})
            private static Method findApplyMethod() {
                try {
                    Class clz = SharedPreferences.Editor.class;
                    return clz.getMethod("apply");
                } catch (NoSuchMethodException e) {
                }

                return null;
            }

            /**
             * 如果找到则使用apply执行，否则使用commit
             *
             * @param editor
             */
            public static void apply(SharedPreferences.Editor editor) {
                try {
                    if (sApplyMethod != null) {
                        sApplyMethod.invoke(editor);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
                editor.commit();
            }
        }

    }

    /**
     * ToolFor9Ge 图片相关 界面字体修改等方法
     */
    public static class ToolFor9Ge {
        // 缩放/裁剪图片
        public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newbm;
        }




        // 通过路径生成Base64文件
        public static String getBase64FromPath(String path) {
            String base64 = "";
            try {
                File file = new File(path);
                byte[] buffer = new byte[(int) file.length() + 100];
                @SuppressWarnings("resource")
                int length = new FileInputStream(file).read(buffer);
                base64 = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return base64;
        }

        //通过文件路径获取到bitmap
        public static Bitmap getBitmapFromPath(String path, int w, int h) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置为ture只获取图片大小
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 返回为空
            BitmapFactory.decodeFile(path, opts);
            int width = opts.outWidth;
            int height = opts.outHeight;
            float scaleWidth = 0.f, scaleHeight = 0.f;
            if (width > w || height > h) {
                // 缩放
                scaleWidth = ((float) width) / w;
                scaleHeight = ((float) height) / h;
            }
            opts.inJustDecodeBounds = false;
            float scale = Math.max(scaleWidth, scaleHeight);
            opts.inSampleSize = (int) scale;
            WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
            return Bitmap.createScaledBitmap(weak.get(), w, h, true);
        }

        //把bitmap转换成base64
        public static String getBase64FromBitmap(Bitmap bitmap, int bitmapQuality) {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, bStream);
            byte[] bytes = bStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        //把base64转换成bitmap
        public static Bitmap getBitmapFromBase64(String string) {
            byte[] bitmapArray = null;
            try {
                bitmapArray = Base64.decode(string, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }

        //把Stream转换成String
        public static String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "/n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        // 修改整个界面所有控件的字体
        public static void changeFonts(ViewGroup root, String path, Activity act) {
            //path是字体路径
            Typeface tf = Typeface.createFromAsset(act.getAssets(), path);
            for (int i = 0; i < root.getChildCount(); i++) {
                View v = root.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTypeface(tf);
                } else if (v instanceof Button) {
                    ((Button) v).setTypeface(tf);
                } else if (v instanceof EditText) {
                    ((EditText) v).setTypeface(tf);
                } else if (v instanceof ViewGroup) {
                    changeFonts((ViewGroup) v, path, act);
                }
            }
        }

        // 修改整个界面所有控件的字体大小
        public static void changeTextSize(ViewGroup root, int size, Activity act) {
            for (int i = 0; i < root.getChildCount(); i++) {
                View v = root.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTextSize(size);
                } else if (v instanceof Button) {
                    ((Button) v).setTextSize(size);
                } else if (v instanceof EditText) {
                    ((EditText) v).setTextSize(size);
                } else if (v instanceof ViewGroup) {
                    changeTextSize((ViewGroup) v, size, act);
                }
            }
        }

        // 不改变控件位置，修改控件大小
        public static void changeWH(View v, int W, int H) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) v.getLayoutParams();
            params.width = W;
            params.height = H;
            v.setLayoutParams(params);
        }

        // 修改控件的高
        public static void changeH(View v, int H) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) v.getLayoutParams();
            params.height = H;
            v.setLayoutParams(params);
        }


    }

    /**
     * Toast统一管理类
     */
    public static class T {

        private T() {
		/* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        public static boolean isShow = true;

        /**
         * 短时间显示Toast
         *
         * @param context
         * @param message
         */
        public static void showShort(Context context, CharSequence message) {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        /**
         * 短时间显示Toast
         *
         * @param context
         * @param message
         */
        public static void showShort(Context context, int message) {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        /**
         * 长时间显示Toast
         *
         * @param context
         * @param message
         */
        public static void showLong(Context context, CharSequence message) {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        /**
         * 长时间显示Toast
         *
         * @param context
         * @param message
         */
        public static void showLong(Context context, int message) {
            if (isShow)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        /**
         * 自定义显示Toast时间
         *
         * @param context
         * @param message
         * @param duration
         */
        public static void show(Context context, CharSequence message, int duration) {
            if (isShow)
                Toast.makeText(context, message, duration).show();
        }

        /**
         * 自定义显示Toast时间
         *
         * @param context
         * @param message
         * @param duration
         */
        public static void show(Context context, int message, int duration) {
            if (isShow)
                Toast.makeText(context, message, duration).show();
        }

    }
}
