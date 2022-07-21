package com.yingda.rxtools.gsls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import dalvik.system.DexFile;

public class GT {

    //================================== 所有属于 GT 类的属性 =======================================
    private static GT gtAndroid = null;          //定义 GT 对象
    private static Toast toast;                  //吐司缓冲
    private Activity activity;                     //设置 当前动态的 上下文对象
    private static int logMaxLength = 3900;      //日志打印最大长度 默认是 3900 可修改
    //================================== 提供访问 GT 属性的接口======================================

    private GT() {
    }//设置不可实例化

    /**
     * 获取线程安全的 GT 单例对象
     *
     * @return GT  返回 GT 对象
     */
    public static GT getGT() {
        if (gtAndroid == null) {
            synchronized (GT.class) {
                if (gtAndroid == null) {
                    gtAndroid = new GT();
                }
            }
        }
        return gtAndroid;
    }

    /**
     * 获取 Context
     *
     * @return
     */
    public Activity getActivity() {
        return activity;
    }


    public int getLogMaxLength() {
        return logMaxLength;
    }

    public void setLogMaxLength(int logMaxLength) {
        this.logMaxLength = logMaxLength;
    }

    //============================================= 加载 GT 必要的工具 =============================

    /**
     * @param number 提示报错文件是第几级
     * @return String 报错的文件与行号
     * @报错提示 该提示可通过 GT 提供的接口 的实例获取
     */
    public static String getLineInfo(int number) {
        StackTraceElement ste = new Throwable().getStackTrace()[number];
        return "提示的文件  " + ste.getFileName() + "  行号 " + ste.getLineNumber();
    }

    /**
     * @return String 报错的文件与行号
     * @报错提示 该提示可通过 GT 提供的接口 的实例获取
     */
    public static String getLineInfo() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];//默认是当前报错层级
        return "提示的文件  " + ste.getFileName() + "  行号 " + ste.getLineNumber();
    }

    //============================================= 日志类 =========================================

    /**
     * @用于打详细日志的 LOG 框架
     */
    public static class LOG {

        public LOG() {
        }

        //保存log的路径
        private static String path = Environment.getExternalStorageDirectory().getPath() + "/GT_LOG/";
        private static String logFilePath = ""; //自定义 打印日志的文件路径
        //格式化不包含秒的时间
        private static SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);
        //保存log的文件名称
        public static String fileName = "-log-" + "GT".substring("GT".lastIndexOf(".") + 1) + ".txt";
        private static String printFileName = "";         //用于自定义 打印文件名

        public static boolean LOG_TF = true;        //控制外部所有的 Log 显示
        public static boolean LOG_FILE_TF = false;  //控制是否打日志到本地(注意：打开比较耗内存资源，该功能仅用于测试，在上线时请关闭它)
        public static boolean GT_LOG_TF = false;    //控制内部所有的 Log 显示
        public static String LOG_TAG = "GT_";       //控制日志 TAG 值
        public static int tier = 5;                 //控制日志截取第几层的信息

        public static String getLogFilePath() {
            return logFilePath;
        }

        public static void setLogFilePath(String logFilePath) {
            LOG.logFilePath = logFilePath;
        }

        public static String getPath() {
            return path;
        }

        public static void setPath(String path) {
            LOG.path = path;
        }

        public static SimpleDateFormat getDfd() {
            return dfd;
        }

        public static void setDfd(SimpleDateFormat dfd) {
            LOG.dfd = dfd;
        }

        public static String getFileName() {
            return fileName;
        }

        public static void setFileName(String fileName) {
            LOG.fileName = fileName;
        }

        public static boolean isLogTf() {
            return LOG_TF;
        }

        public static void setLogTf(boolean logTf) {
            LOG_TF = logTf;
        }

        public static boolean isLogFileTf() {
            return LOG_FILE_TF;
        }

        /**
         * @param logFileTf
         * @param activity
         * @打开本地打印
         */
        public static void setLogFileTf(boolean logFileTf, Activity activity) {
            LOG_FILE_TF = logFileTf;
            if (logFileTf) {
                printFileName = ApplicationUtils.getAppName(activity);
            }
        }

        public static boolean isGtLogTf() {
            return GT_LOG_TF;
        }

        public static void setGtLogTf(boolean gtLogTf) {
            GT_LOG_TF = gtLogTf;
        }

        public static String getLogTag() {
            return LOG_TAG;
        }

        public static void setLogTag(String logTag) {
            LOG_TAG = logTag;
        }

        public static int getTier() {
            return tier;
        }

        public static void setTier(int tier) {
            LOG.tier = tier;
        }

        public static String getPrintFileName() {
            return printFileName;
        }

        public static void setPrintFileName(String printFileName) {
            LOG.printFileName = printFileName;
        }

        public static SimpleDateFormat getDfs() {
            return dfs;
        }

        public static void setDfs(SimpleDateFormat dfs) {
            LOG.dfs = dfs;
        }

        // 获取log打印前缀(行数、类名、方法名)
        private static String getPrefix(int number) {
            String prefix = "<Line:%d>[%s] %s(): "; // 占位符
            StackTraceElement caller = java.lang.Thread.currentThread().getStackTrace()[number];// new Throwable().getStackTrace()[number];
            String className = caller.getClassName();
            prefix = String.format(prefix, caller.getLineNumber(), className, caller.getMethodName()); // 替换
            return prefix;
        }

        /**
         * log打印到sdCard
         *
         * @param path   文件路径(不含文件名)
         * @param prefix log前缀内容
         * @param msg    打印内容
         * @ 格式化包含秒的时间
         */
        private static SimpleDateFormat dfs = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINESE);

        public static void writeToSdCard(String path, String prefix, Object msg) {

            if (logFilePath != null && logFilePath.length() > 0) {
                path = Environment.getExternalStorageDirectory().getPath() + "/" + logFilePath;
            }

            String time = dfs.format(new Date());
            File file = createPathFile(path);
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                out.write("\r\n[" + time + "]" + getProcessTag() + prefix + msg);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //获取当前进程信息(PID,TID,ThreadId)
        private static String getProcessTag() {
            return "<PID:" + android.os.Process.myPid() + ",TID:" + android.os.Process.myTid() + ",ThreadId:" + java.lang.Thread.currentThread().getId() + ">";
        }

        /**
         * 根据文件路径 创建文件
         *
         * @param path 文件路径(不含文件名)
         */
        public static File createPathFile(String path) {
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            //如果自定义了 打印文件名就给它初始化上
            if (printFileName != null && !"".equals(printFileName)) {
                fileName = "-" + printFileName + ".txt";
            }
            String filePath = path + dfd.format(new Date()) + fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    err("LOG日志报错", getLineInfo(1) + "[logGT] createPathFile(): e=" + e);
                }
            }
            return file;
        }

        //注册App异常捕获
        public static void initAppErrLogTry(Activity activity) {
            AppErrLog.getInstance().init(activity);//初始化
        }


    }

    /**
     * @APP 错误异常信息捕获
     */
    private static class AppErrLog implements java.lang.Thread.UncaughtExceptionHandler {
        private static AppErrLog sInstance = new AppErrLog();
        private java.lang.Thread.UncaughtExceptionHandler mDefaultMyCrashHandler;
        private Context mContext;

        private AppErrLog() {
        }

        public static AppErrLog getInstance() {
            return sInstance;
        }

        public void init(@NonNull Context context) {
            mDefaultMyCrashHandler = java.lang.Thread.getDefaultUncaughtExceptionHandler();
            java.lang.Thread.setDefaultUncaughtExceptionHandler(this);
            mContext = context.getApplicationContext();
        }

        /**
         * 当程序中有未被捕获的异常，系统将会调用这个方法
         *
         * @param t 出现未捕获异常的线程
         * @param e 得到异常信息
         */
        @Override
        public void uncaughtException(java.lang.Thread t, Throwable e) {
            try {
                //保存到本地
                //            exportExceptionToSDCard(e);
                GT.logs("************************ ErrLogRun ***************************");
                GT.logs("PhoneData：" + appendPhoneInfo());
                GT.logs("---------------------------------------------------------------");
                GT.logs("ErrLog:" + e);
                GT.logs("************************ ErrLogClose ***************************");
                //下面也可以写上传的服务器的代码
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
            if (mDefaultMyCrashHandler != null) {
                mDefaultMyCrashHandler.uncaughtException(t, e);
            } else {
                GT.log("exit app");
            }
        }

        /**
         * 获取手机信息
         */
        private String appendPhoneInfo() throws PackageManager.NameNotFoundException {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            StringBuilder sb = new StringBuilder();
            //App版本
            sb.append("App Version: ");
            sb.append(pi.versionName);
            sb.append("_");
            sb.append(pi.versionCode + "\n");

            //Android版本号
            sb.append("OS Version: ");
            sb.append(Build.VERSION.RELEASE);
            sb.append("_");
            sb.append(Build.VERSION.SDK_INT + "\n");

            //手机制造商
            sb.append("Vendor: ");
            sb.append(Build.MANUFACTURER + "\n");

            //手机型号
            sb.append("Model: ");
            sb.append(Build.MODEL + "\n");

            //CPU架构
            sb.append("CPU: ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sb.append(Arrays.toString(Build.SUPPORTED_ABIS));
            } else {
                sb.append(Build.CPU_ABI);
            }
            return sb.toString();
        }
    }

    /**
     * 提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void log(Object msg) {
        if (LOG.LOG_TF) {
            Log.i(LOG.LOG_TAG + "i", "------- " + msg);
        }
    }

    /**
     * @param mg
     * @详细提示消息
     */
    public static void logs(Object mg) {
        if (LOG.LOG_TF) {
            String prefix = "";
            prefix = LOG.getPrefix(LOG.tier);
            log(prefix + mg);
            if (LOG.LOG_FILE_TF) {// 打印到sd卡
                if (TextUtils.isEmpty(prefix)) {
                    prefix = LOG.getPrefix(LOG.tier);
                }
                LOG.writeToSdCard(LOG.path, prefix, mg);
            }
        }
    }

    /**
     * 打印所有提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void logAll(Object msg) {
        if (LOG.LOG_TF) {

            String strMsg = msg.toString();

            if (strMsg.length() > logMaxLength) {
                while (true) {
                    String substring = strMsg.substring(0, logMaxLength);
                    Log.i(LOG.LOG_TAG + "i", "------- " + substring);
                    strMsg = strMsg.substring(logMaxLength);
                    if (strMsg.length() <= logMaxLength) {
                        Log.i(LOG.LOG_TAG + "i", strMsg);
                        break;
                    }
                }
            } else {
                Log.i(LOG.LOG_TAG + "i", "------- " + msg);
            }
        }
    }

    /**
     * 提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void err(Object msg) {
        if (LOG.LOG_TF) {
            Log.e(LOG.LOG_TAG + "e", "------- " + msg);
        }
    }

    /**
     * @param mg
     * @详细提示消息
     */
    public static void errs(Object mg) {
        if (LOG.LOG_TF) {
            String prefix = "";
            prefix = LOG.getPrefix(LOG.tier);
            err(prefix + mg);
            if (LOG.LOG_FILE_TF) {// 打印到sd卡
                if (TextUtils.isEmpty(prefix)) {
                    prefix = LOG.getPrefix(LOG.tier);
                }
                LOG.writeToSdCard(LOG.path, prefix, mg);
            }
        }
    }


    /**
     * 打印所有提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void errAll(Object msg) {
        if (LOG.LOG_TF) {
            String strMsg = msg.toString();
            if (strMsg.length() > logMaxLength) {
                while (true) {
                    String substring = strMsg.substring(0, logMaxLength);
                    Log.e(LOG.LOG_TAG + "i", "------- " + substring);
                    strMsg = strMsg.substring(logMaxLength);
                    if (strMsg.length() <= logMaxLength) {
                        Log.e(LOG.LOG_TAG + "i", strMsg);
                        break;
                    }
                }
            } else {
                Log.e(LOG.LOG_TAG + "i", "------- " + msg);
            }
        }
    }

    /**
     * 提示消息 Log
     *
     * @param title 日志标题
     * @param msg   日志消息
     */
    public static void log(Object title, Object msg) {
        if (LOG.LOG_TF) {
            Log.i(LOG.LOG_TAG + "i",
                    "------- Run" +
                            "\n\n---------------------" + title + "------------------------\n" +
                            "                   " + msg + "\n" +
                            "---------------------" + title + "-----------------------\n\n" +
                            "------- Close"
            );
        }

    }

    /**
     * 打印所有提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void logAll(Object title, Object msg) {
        if (LOG.LOG_TF) {

            Log.i(LOG.LOG_TAG + "i", "Run --------- " + title + " ---------");
            String strMsg = msg.toString();
            if (strMsg.length() > logMaxLength) {
                while (true) {
                    String substring = strMsg.substring(0, logMaxLength);
                    Log.i(LOG.LOG_TAG + "i", "------- " + substring);
                    strMsg = strMsg.substring(logMaxLength);
                    if (strMsg.length() <= logMaxLength) {
                        Log.i(LOG.LOG_TAG + "i", strMsg);
                        break;
                    }
                }
            } else {
                Log.i(LOG.LOG_TAG + "i", "------- " + msg);
            }

            Log.i(LOG.LOG_TAG + "i", "--------- " + title + " --------- Close");

        }
    }

    /**
     * 提示消息 Log
     *
     * @param title 日志标题
     * @param msg   日志消息
     */
    public static void err(Object title, Object msg) {
        if (LOG.LOG_TF) {
            Log.e(LOG.LOG_TAG + "e",
                    "------- Run" +
                            "\n\n---------------------" + title + "------------------------\n" +
                            "                   " + msg + "\n" +
                            "---------------------" + title + "-----------------------\n\n" +
                            "------- Close"
            );
        }

    }

    /**
     * 打印所有提示消息 Log
     *
     * @param msg object 类型的消息
     */
    public static void errAll(Object title, Object msg) {
        if (LOG.LOG_TF) {

            Log.e(LOG.LOG_TAG + "i", "Run --------- " + title + " ---------");
            String strMsg = msg.toString();
            if (strMsg.length() > logMaxLength) {
                while (true) {
                    String substring = strMsg.substring(0, logMaxLength);
                    Log.e(LOG.LOG_TAG + "i", "------- " + substring);
                    strMsg = strMsg.substring(logMaxLength);
                    if (strMsg.length() <= logMaxLength) {
                        Log.e(LOG.LOG_TAG + "i", strMsg);
                        break;
                    }
                }
            } else {
                Log.e(LOG.LOG_TAG + "i", "------- " + msg);
            }

            Log.e(LOG.LOG_TAG + "i", "--------- " + title + " --------- Close");

        }
    }

    /**
     * @param tag
     * @异常打印方法
     */
    public static void exception(Object tag) {
        try {
            throw new Exception(tag.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 抛异常，第几层
     *
     * @param tag
     * @param number
     */
    public static void exception(Object tag, int number) {
        try {
            throw new Exception(tag.toString());
        } catch (Exception e) {
            e.printStackTrace();
            GT.log(getLineInfo(number), "e:" + e);
        }
    }


    //============================================= 吐司类 =====================================


    /**
     * @吐司类
     */
    public static class TOAST {

        public static boolean TOAST_TF = true;      //控制外部所有的 toast 显示
        public static boolean GT_TOAST_TF = false;  //控制内部所有的 toast 显示

        public static boolean isToastTf() {
            return TOAST_TF;
        }

        public static void setToastTf(boolean toastTf) {
            TOAST_TF = toastTf;
        }

        public static boolean isGtToastTf() {
            return GT_TOAST_TF;
        }

        public static void setGtToastTf(boolean gtToastTf) {
            GT_TOAST_TF = gtToastTf;
        }

        /**
         * Toast 自定义 View
         */
        public static class ToastView {

            private static Toast toast;
            private View view;

            public Toast getToast() {
                return toast;
            }

            public void ShowToast() {
                toast.show();
            }


            public View getView() {
                return view;
            }


            /**
             * @param layout 布局
             * @return
             */
            public ToastView initLayout(int layout) {
                if (TOAST.TOAST_TF) {
                    if (getGT().activity != null) {
                        view = LayoutInflater.from(getGT().activity).inflate(layout, null);
                        toast = new Toast(getGT().activity);
                        toast.setView(view);
                    } else {
                        if (LOG.LOG_TF) {//设置为默认输出日志
                            err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
                        }
                    }
                }
                return this;
            }

            public ToastView initLayout(int layout, Context context) {
                if (TOAST.TOAST_TF) {
                    if (context != null) {
                        view = LayoutInflater.from(context).inflate(layout, null);
                        toast = new Toast(context);
                        toast.setView(view);
                    } else {
                        if (LOG.LOG_TF) {//设置为默认输出日志
                            err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
                        }
                    }
                }
                return this;
            }

            /**
             * @param layout  布局
             * @param Gravity Gravity.*****  用这个变量里面的值可以控制显示位置 如果为 0 就显示默认位置
             * @return
             */
            public ToastView initLayout(int layout, int Gravity) {

                if (TOAST.TOAST_TF) {
                    if (getGT().activity != null) {
                        view = LayoutInflater.from(getGT().activity).inflate(layout, null);
                        toast = new Toast(getGT().activity);
                        if (Gravity != 0)
                            toast.setGravity(Gravity, 0, 0);
                        toast.setView(view);
                    } else {
                        if (LOG.LOG_TF) {//设置为默认输出日志
                            err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
                        }
                    }
                }
                return this;
            }


            public ToastView initLayout(int layout, int Gravity, Context context) {

                if (TOAST.TOAST_TF) {
                    if (context != null) {
                        view = LayoutInflater.from(context).inflate(layout, null);
                        toast = new Toast(context);
                        if (Gravity != 0)
                            toast.setGravity(Gravity, 0, 0);
                        toast.setView(view);
                    } else {
                        if (LOG.LOG_TF) {//设置为默认输出日志
                            err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
                        }
                    }
                }
                return this;
            }


        }

    }

    /**
     * 单个消息框 Toast
     *
     * @param msg object 类型的消息
     */
    public static void toast_s(Object msg) {
        if (TOAST.TOAST_TF) {
            if (getGT().activity != null) {
                Toast.makeText(getGT().activity, String.valueOf(msg), Toast.LENGTH_SHORT).show();
            } else {
                if (LOG.LOG_TF)//设置为默认输出日志
                    err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
            }

        }
    }

    /**
     * 单个 Toast
     *
     * @param msg  消息
     * @param time 显示时间
     */
    public static void toast_time(Object msg, long time) {
        if (TOAST.TOAST_TF) {
            if (getGT().activity != null) {
                final Toast toast = Toast.makeText(getGT().activity, String.valueOf(msg), Toast.LENGTH_LONG);
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        toast.show();
                    }
                }, 0, 3000);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        toast.cancel();
                        timer.cancel();
                    }
                }, time);
            } else {
                if (LOG.LOG_TF)//设置为默认输出日志
                    err("GT_bug", "消息框错误日志：你没有为 Context 进行赋值 ，却引用了 Toast 导致该功能无法实现。解决措施 在调用 toast 代码之前添加：GT.getGT().setactivity(activity);");
            }

        }
    }/**/

    /**
     * 可多个消息框 Toast
     *
     * @param context 上下文
     * @param msg     object 类型的消息
     */
    public static void toast_s(Context context, Object msg) {
        if (TOAST.TOAST_TF)
            Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }

    /**
     * 可多个消息框 Toast
     *
     * @param context 上下文
     * @param msg     object 类型的消息
     */
    public static void toast_time(Context context, Object msg, int time) {
        if (TOAST.TOAST_TF) {
            final Toast toast = Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_LONG);
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.show();
                }
            }, 0, 3000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                    timer.cancel();
                }
            }, time);

        }
    }

    /**
     * @param content
     * @标准Toast
     */
    public static void toast(Object content) {
        if (getGT().getActivity() != null) {
            if (toast == null) {
                toast = Toast.makeText(getGT().getActivity(), content.toString(), Toast.LENGTH_SHORT);
            } else {
                toast.setText(content.toString());
            }
            toast.show();
        } else {
            log(getLineInfo(1), "当前没有赋值 Context 无法显示 Toast ");
        }

    }

    public static void toast(Context context, Object content) {
        if (toast == null) {
            toast = Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT);
        } else {
            toast.setText(content.toString());
        }
        toast.show();
    }

    //============================================= 数据存储类 =====================================

    /**
     * 对 Object 的增删查改操作
     */
    public static class SaveObject {

        public static interface SaveBean extends Serializable {
        }

        /**
         * 保存Object
         *
         * @param context
         * @param obj
         * @return 是否保存成功
         */
        public static synchronized boolean saveObject(Context context, Object obj) {
            if (context == null || obj == null) {
                errs("保存的参数为 null");
                return false;
            }
            try {
                File file = new File(context.getFilesDir().getPath() + obj.getClass().getName());
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(obj);//存储Object
                out.close();//关闭存储流
            } catch (IOException e) {
                GT.errs("异常:" + e);
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * 删除Object
         *
         * @param context
         * @param cla
         * @return
         */
        public static synchronized boolean deleteObject(Context context, Class<?> cla) {
            if (context == null || cla == null) {
                errs("删除的参数为 null");
                return false;
            }
            try {
                File file = new File(context.getFilesDir().getPath() + cla.getName());
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                Object obj = new Object();//保证不会空指针
                try {
                    obj = cla.newInstance();//实体化
                } catch (IllegalAccessException e) {
                    GT.errs("异常:" + e);
                    e.printStackTrace();
                    return false;
                } catch (InstantiationException e) {
                    GT.errs("异常:" + e);
                    e.printStackTrace();
                    return false;
                }
                out.writeObject(obj);//存储Object
                out.close();//关闭存储流
            } catch (IOException e) {
                GT.errs("异常:" + e);
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * 获取Object
         *
         * @param context
         * @param cla
         * @param <T>
         * @return
         */
        public static synchronized <T> T queryObject(Context context, Class<T> cla) {

            if (context == null || cla == null) {
                errs("查询的参数为 null");
                return null;
            }
            T t = null;
            try {
                File file = new File(context.getFilesDir().getPath() + cla.getName());
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                t = (T) in.readObject();
                in.close();//关闭反序列化数据流
            } catch (IOException | ClassNotFoundException e) {
                GT.errs("异常:" + e);
                e.printStackTrace();
            }
            return t;

        }

        /**
         * 更改Object
         *
         * @param context
         * @param obj
         * @return
         */
        public static synchronized boolean updateObject(Context context, Object obj) {
            if (context == null || obj == null) {
                errs("修改的参数为 null");
                return false;
            }
            try {
                File file = new File(context.getFilesDir().getPath() + obj.getClass().getName());
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(obj);//存储Object
                out.close();//关闭存储流
            } catch (IOException e) {
                GT.errs("异常:" + e);
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }


    /**
     * GT_IO  Android 的 内部 IO 流
     */
    public static class GT_IO {

        /**
         * 使用实例：
         * 第一步：
         * GT.GT_IO io = new GT.GT_IO(this);//创建 IO 对象
         * 第二步：
         * io.save(editText.getText().toString(),"King");//保存数据
         * 第三步：
         * String king = io.query("King"); //获取数据
         */

        private Context context;

        /**
         * 实例化 上下文
         *
         * @param context
         */
        public GT_IO(Context context) {
            this.context = context;
        }

        /**
         * 保存数据
         *
         * @param saveData 保存的数据
         * @param dataName 保存文件名称
         * @return 返回 IO 操作对象
         */
        public GT_IO save(String saveData, String dataName) {

            FileOutputStream fos = null;//文件输出流
            try {
                fos = context.openFileOutput(dataName, context.MODE_PRIVATE);//获取文件输出流对象
                fos.write(saveData.getBytes());//保存备忘信息
                fos.flush();//清除缓存
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();//关闭输出流
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return this;
        }

        /**
         * 查询文件
         *
         * @param dataName 文件绝对路径包括文件名
         * @return
         */
        public String query(String dataName) {
            FileInputStream fis = null;//文件输入流对象
            String data = null;
            byte[] buffer = null;
            try {
                fis = context.openFileInput(dataName);//获得文件输入流对象
                buffer = new byte[fis.available()];//实例化字节数组
                fis.read(buffer);//从输入流中读取数据
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();//关闭输入流对象
                        data = new String(buffer);//把字节数组中的数据转换为字符串
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data;
        }

    }

    /**
     * 增强版外部存储
     */
    public static class FileUtils {

        /**
         * 保存单个文件
         *
         * @param saveData 文件保存的数据
         * @param savePaht 文件保存的路径
         * @param fileName 文件名与扩展名
         */
        public static void saveData(String saveData, String savePaht, String fileName) {
            save(saveData, savePaht, fileName);
        }

        /**
         * 保存多个文件
         *
         * @param savePaht 多个文件保存的地址
         * @param saveMap  保存多个文件的数据 key = 文件名与扩展名  value = 文件里的数据
         */
        public static void saveDataAll(String savePaht, Map<String, String> saveMap) {
            for (String key : saveMap.keySet()) {
                save(saveMap.get(key), savePaht, key);
            }
        }

        /**
         * 文件是否存在
         *
         * @param filePathAddFileName
         * @return
         */
        public static boolean fileExist(String filePathAddFileName) {
            return new File(filePathAddFileName).exists();
        }

        /**
         * 保存文件数据
         *
         * @param saveData
         * @param savePaht
         * @param fileName
         */
        public static void save(String saveData, String savePaht, String fileName) {
            File fileNull = new File(savePaht);
            if (!fileNull.exists()) {
                fileNull.mkdirs();
            }

            File file = new File(savePaht, fileName);
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                fos.write(saveData.getBytes());
                fos.flush();
            } catch (FileNotFoundException var18) {
                var18.printStackTrace();
            } catch (IOException var19) {
                var19.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException var17) {
                        var17.printStackTrace();
                    }
                }

            }

        }

        /**
         * 查询文件数据
         *
         * @param queryPaht
         * @param fileName
         * @return
         */
        public static String query(String queryPaht, String fileName) {
            File fileNull = new File(queryPaht);
            if (!fileNull.exists()) {
                fileNull.mkdirs();
            }

            File file = new File(queryPaht, fileName);
            FileInputStream fis = null;
            byte[] buffer = null;
            String data = null;

            try {
                fis = new FileInputStream(file);
                buffer = new byte[fis.available()];
                fis.read(buffer);
            } catch (FileNotFoundException var19) {
                var19.printStackTrace();
            } catch (IOException var20) {
                var20.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                        data = new String(buffer);
                    } catch (IOException var18) {
                        var18.printStackTrace();
                    }
                }

            }

            return data;
        }

        /**
         * 查询当前文件路径汇总有多少个文件
         *
         * @param filePath
         * @return
         */
        public static List<String> queryFilePathFileNumber(String filePath) {

            List<String> fileNameList = new ArrayList<>();

            //判断当前路径是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                return fileNameList;
            }

            try {
                File[] files = file.listFiles();
                if (files == null || files.length == 0)
                    return fileNameList;
                for (File fileOne : files) {
                    if (fileOne.isDirectory()) {
                        fileNameList.add("目录:" + fileOne.getName());
                    } else {
                        fileNameList.add("文件:" + fileOne.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return fileNameList;
            }
            return fileNameList;
        }

        /**
         * 获取文件描述
         *
         * @param filePath
         * @param fileName
         * @return
         */
        public static String getFileSizeDescriptor(String filePath, String fileName) {

            try {
                long fileSize = getFileSize(new File(filePath, fileName));
                String fileDescriptor = formetFileSize(fileSize);
                return fileDescriptor;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 获取指定文件大小
         *
         * @param file
         * @return
         * @throws Exception
         */
        private static long getFileSize(File file) throws Exception {
            long size = 0;
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
            }
            return size;
        }

        /**
         * 转换文件大小
         *
         * @param fileS
         * @return
         */
        private static String formetFileSize(long fileS) {
            DecimalFormat df = new DecimalFormat("#.00");
            String fileSizeString = "";
            String wrongSize = "0B";
            if (fileS == 0) {
                return wrongSize;
            }
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "MB";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "GB";
            }
            return fileSizeString;
        }

        /**
         * 删除当前目录下所有的文件已经子文件
         *
         * @param deleteFile
         */
        public static void deleteAllFiles(File deleteFile, boolean deleteThisFile) {

            if (!deleteFile.exists()) {
                return;
            }

            File files[] = deleteFile.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isDirectory()) { // 判断是否为文件夹
                        deleteAllFiles(file, deleteThisFile);
                        try {
                            file.delete();
                        } catch (Exception e) {
                        }
                    } else {
                        if (file.exists()) { // 判断是否存在
                            deleteAllFiles(file, deleteThisFile);
                            try {
                                file.delete();
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                //是否删除当前文件
                if (deleteThisFile) {
                    try {
                        deleteFile.delete();
                    } catch (Exception e) {
                    }
                }

            } else {
                try {
                    deleteFile.delete();
                } catch (Exception e) {
                }
            }
        }

        /**
         * 重命名
         *
         * @param path
         * @param oldName
         * @param newName
         */
        public static void changeFileDirName(String path, String oldName, String newName) {
            File oldFile = new File(path + "/" + oldName);
            File newFile = new File(path + "/" + newName);

            oldFile.renameTo(newFile);
        }

        /**
         * 获取U盘名称
         *
         * @return
         */
        public static String getUsbName() {
            String filePath = "/proc/mounts";
            File file = new File(filePath);
            List<String> lineList = new ArrayList<>();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GBK");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("vfat")) {
                            lineList.add(line);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (lineList.size() == 0)
                return null;
            String editPath = lineList.get(lineList.size() - 1);
            int start = editPath.indexOf("/mnt");
            int end = editPath.indexOf(" vfat");
            String path = editPath.substring(start, end);

            return path;
        }


        /**
         * 复制单个文件
         *
         * @param fromFileStr
         * @param toFileStr
         * @return
         */
        public static void copyFile(String fromFileStr, String toFileStr) {

            File fromFile = new File(fromFileStr);
            File toFile = new File(toFileStr);

            if (!fromFile.exists()) {
                return;
            }

            //开始拷贝文件
            copy(fromFileStr, toFileStr);
        }

        /**
         * 拷贝文件
         *
         * @param fromFile
         * @param toFile
         * @return
         */
        private static int copy(String fromFile, String toFile) {
            try {
                InputStream inputStream = new FileInputStream(fromFile);
                OutputStream outputStream = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int d;
                while ((d = inputStream.read(bt)) > 0) {
                    outputStream.write(bt, 0, d);
                }
                inputStream.close();
                outputStream.close();
                return 0;
            } catch (Exception e) {
                return -1;
            }
        }

        /**
         * 复制所有文件
         *
         * @param fromFile
         * @param toFile
         * @return
         */
        public static int copyAllFile(String fromFile, String toFile) {
            //要复制的文件目录
            File[] currentFiles;
            File root = new File(fromFile);
            //如同判断SD卡是否存在或者文件是否存在
            //如果不存在则 return出去
            if (!root.exists()) {
                return -1;
            }
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles();
            if (currentFiles == null) {//如果只有一个文件，那就使用拷贝一个文件的方法
                FileUtils.copyFile(fromFile, toFile);
                return 0;
            }

            //目标目录
            File targetDir = new File(toFile);
            //创建目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < currentFiles.length; i++) {
                if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
                {
                    //                GT.log("1 " + currentFiles[i].getPath() + "/" + "--------" + toFile + "/" + currentFiles[i].getName() + "/");
                    copyAllFile(currentFiles[i].getPath() + "/", toFile + "/" + currentFiles[i].getName() + "/");

                } else//如果当前项为文件则进行文件拷贝
                {
                    //                GT.log("2 " + currentFiles[i].getPath() + "--------" + toFile + "/" + currentFiles[i].getName());
                    CopySdcardFile(currentFiles[i].getPath(), toFile + "/" + currentFiles[i].getName());
                }
            }
            return 0;
        }

        /**
         * 要复制的目录下的所有非子目录(文件夹)文件拷贝
         *
         * @param fromFile
         * @param toFile
         * @return
         */
        private static int CopySdcardFile(String fromFile, String toFile) {
            try {
                InputStream fosfrom = new FileInputStream(fromFile);
                OutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();
                return 0;

            } catch (Exception ex) {
                return -1;
            }
        }


    }

    /**
     * @Hibernate SQL
     */
    public static class Hibernate implements SaveObject.SaveBean {


        //=============================== 实例化 Hibernate 对象 ====================================
        private Context context;

        public Hibernate() {
            Context context = getGT().getActivity();
            if (context != null) {
                this.context = context;
            } else {
                errs("当前并没有绑定 Activity 无法使用无参构造方法，请先使用 GT.getGT().build(this); 进行 Activity 绑定。");
            }
        }

        public Hibernate(Context context) {
            this.context = context;
        }

        //=============================== 数据库注解 ====================================

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Build {
            String sqlName() default "GT";//数据库默认名为 GT.db

            int sqlVersion() default 1;//数据库默认版本从 1
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_Entity {
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_Bean {
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_Entitys {
            Class<?>[] valueArray() default {};
        }

        @Target(ElementType.FIELD)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_Beans {
            Class<?>[] valueArray() default {};
        }

        //设置主键注解
        @Target({ElementType.FIELD})
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_Key {
            //该 Key 值的类型
            boolean autoincrement() default true;//默认为 手动增长
        }

        //修改表字段注解
        @Target({ElementType.FIELD})
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_DatabaseField {
            String oldTableValue() default "";

            String defaultValue() default "";
        }

        //不被持久化
        @Target({ElementType.FIELD})
        @Retention(RetentionPolicy.RUNTIME)
        public @interface GT_OnNullValue {

        }

        //=============================== 数据库属性 ====================================
        private String DATABASE_NAME = "GT.db";   //数据库名称
        private int DATABASE_VERSION = 1;         //数据库版本

        public String getDATABASE_NAME() {
            return DATABASE_NAME;
        }

        public void setDATABASE_NAME(String DATABASE_NAME) {
            this.DATABASE_NAME = DATABASE_NAME;
        }

        public int getDATABASE_VERSION() {
            return DATABASE_VERSION;
        }

        public void setDATABASE_VERSION(int DATABASE_VERSION) {
            this.DATABASE_VERSION = DATABASE_VERSION;
        }

        //=============================== 数据库对象 ====================================
        public SQLiteDatabase sqLiteDatabase2;
        //=============================== 数据库语句 ====================================
        public static String SQL_CODE = "";

        //=============================== 无实体类完成数据库 ====================================
        //无实体类创建表
        public static class NoEntityTable {

            /**
             * @创建数据库代码示例： CREATE TABLE Dome( domeID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, domeName VARCHAR(200), domeNumber INT)
             */

            //构造方法
            public NoEntityTable() {
                //初始化
                isCreateTable = true;
                tableNameList.clear();
                creationTableNameList.clear();
                updateTableValueList.clear();
            }

            //=================================== 第二步：添加创建表代码 ===================================

            /**
             * @param tableCode 创建表的SQL代码
             * @存储添加表SQL代码
             */
            public NoEntityTable addTableCode(String tableCode) {
                if (tableCode != null) {
                    String tableName = null;
                    try {
                        tableName = analysisTableName(tableCode);
                        if (!tableNameList.contains(tableName)) {
                            tableNameList.add(tableName);
                            creationTableNameList.add(tableCode);
                        } else {
                            err(getLineInfo(2), tableName + "表 重复创建");
                            isCreateTable = false;
                        }
                    } catch (Exception e) {
                        err(getLineInfo(2), "请确认创建表的SQL代码是否正确！e:" + e);
                        isCreateTable = false;
                    }
                }
                return this;
            }

            //获取表代码
            public List<String> getTableCode() {
                return creationTableNameList;
            }


            //=================================== 第三步：添加修改的代码 ===================================

            /**
             * @param updateTableCode
             * @return
             * @存储修改表代码
             */
            public NoEntityTable addUpdateTableCode(String updateTableCode) {
                if (updateTableCode != null) {
                    updateTableValueList.add(updateTableCode);
                }
                return this;
            }

            /**
             * @param Code
             * @return
             * @通过创建表代码解析表名称
             */
            private String analysisTableName(String Code) {
                String substring = Code.substring(0, Code.indexOf("("));
                Pattern pattern = Pattern.compile("\\s+");
                Matcher matcher = pattern.matcher(substring);
                substring = matcher.replaceAll(" ").trim();
                substring = substring.substring(substring.lastIndexOf(" ") + 1, substring.length());
                return substring;
            }

        }

        public SQLiteDatabase getSqLiteDatabase() {
            return sqLiteDatabase2;
        }

        /**
         * 关闭SQL对象
         *
         * @return
         */
        public Hibernate close() {
            if (sqLiteDatabase2 != null) {
                sqLiteDatabase2.close();
            }
            return this;
        }

        /**
         * @param sqLiteDatabase2
         * @设置Hibernate数据库的SQL管理对象
         */
        public void setSqLiteDatabase(SQLiteDatabase sqLiteDatabase2) {
            this.sqLiteDatabase2 = sqLiteDatabase2;
        }

        //=============================== 数据表路径 ====================================

        private List<Class<?>> tableList;//创建表class集合

        public List<Class<?>> getTableList() {
            return tableList;
        }

        public void setTableList(List<Class<?>> tableList) {
            this.tableList = tableList;
        }

        //表字段与字段的类型
        private List<String> tableStr;

        public List<String> getTableStr() {
            return tableStr;
        }

        public void setTableStr(List<String> tableStr) {
            this.tableStr = tableStr;
        }

        //=============================== 数据库管理 ====================================

        /**
         * @数据库管理类
         */
        private class DatabaseHelper extends SQLiteOpenHelper {

            private DatabaseHelper databaseHelper = null;
            private Context context = null;

            public DatabaseHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
                this.context = context;
            }

            public synchronized DatabaseHelper getDatabaseHelper(Context context) {
                if (databaseHelper == null) {
                    databaseHelper = new DatabaseHelper(context);
                }
                return databaseHelper;
            }

            //构造方法
            public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
                this.context = context;
            }

            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase2 = sqLiteDatabase;
                //如果设置了 独自的 SQL 语句代码就用单独设置的
                if (null != SQL_CODE && !"".equals(SQL_CODE)) {
                    sqLiteDatabase.execSQL(SQL_CODE); //创建数据库 sql 语句 并 执行
                    SQL_CODE = "";//初始化
                } else {//否则用智能SQL代码
                    for (String sqlStr : creationTableNameList) {
                        sqLiteDatabase.execSQL(sqlStr); //创建数据库 sql 语句 并 执行
                    }
                }

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                //更新开始
                sqLiteDatabase2 = sqLiteDatabase;

                //如果当前版本需要升级
                if (oldVersion < newVersion) {

                    if (SQL_CODE != null && !"".equals(SQL_CODE)) {//用户指定的代码
                        sqLiteDatabase.execSQL(SQL_CODE);
                        SQL_CODE = "";//初始化
                    } else {//用智能代码

                        for (int i = 0; i < creationTableNameList.size(); i++) { //遍历需要升级的表
                            //第一步：获取想要更新升级的所有表名，将表名全部改成“待删除表”
                            if (isTable(tableNameList.get(i))) {//如果当前数据库存在该表
                                updateTableName(tableNameList.get(i), "temp_" + tableNameList.get(i));//修改表名
                            }

                            //第二步：创建最新的表
                            if (!isTable(tableNameList.get(i))) {//当前表如果不存在
                                sqLiteDatabase2.execSQL(creationTableNameList.get(i)); //创建数据库 sql 语句 并 执行
                            }

                            //第三步：将旧表数据导入新表中
                            if (isTable("temp_" + tableNameList.get(i)) && isTable(tableNameList.get(i))) {//如果当前数据库存在该表

                                List<String> oldList = new ArrayList<>();//需要导入的旧字段
                                List<String> newList = new ArrayList<>();//需要导入的新字段

                                List<String> tempSQLTableValue = getTableAllValue("temp_" + tableNameList.get(i));//获取上个版本表所有字段
                                List<String> SQLTableValue = getTableAllValue(tableNameList.get(i));//获取当前最新版本数据库表所有字段
                                for (String tableValue : tempSQLTableValue) {//遍历旧版本，如果新版本也有就存入 待导入数据字段行列
                                    if (SQLTableValue.contains(tableValue)) {
                                        oldList.add(tableValue);//存入容器中
                                        newList.add(tableValue);//存入容器中
                                    }
                                }

                                //当前表是否需要修改字段
                                //Book{bookName bookNumber ,name number }
                                if (updateTableValueList != null && updateTableValueList.size() > 0) {
                                    for (String updateData : updateTableValueList) {
                                        String updateTableName = updateData.substring(0, updateData.indexOf("{"));//获取需要修改的表名
                                        if (!tableNameList.get(i).equals(updateTableName))
                                            continue;//过滤掉不需要修改的表

                                        //开始解析 修改字段
                                        String updateValue = updateData;
                                        updateValue = updateValue.substring(updateValue.indexOf("{") + 1, updateValue.indexOf("}"));
                                        String[] updateArrayValue = updateValue.split(",");
                                        String[] oldArray = updateArrayValue[0].split(" ");
                                        String[] newArray = updateArrayValue[1].split(" ");

                                        for (String old : oldArray) {
                                            oldList.add(old);//存入容器中
                                        }

                                        for (String news : newArray) {
                                            newList.add(news);//存入容器中
                                        }


                                    }
                                }
                                //将旧表中 需要导入的数据 导入到新表中
                                inputTableData("temp_" + tableNameList.get(i), oldList, tableNameList.get(i), newList);
                            }


                            //第四步：删除旧表
                            if (isTable("temp_" + tableNameList.get(i))) {//如果当前数据库存在该表
                                deleteTable("temp_" + tableNameList.get(i));
                            }

                        }

                        //第五步：删除数据库中多余无用的表
                        for (String tableName : getSQLAllTableName()) {//获取数据库中所有的表
                            if (!"sqlite_sequence".equals(tableName)) {//过来掉系统自带的表
                                if (!tableNameList.contains(tableName)) {//如果数据库表没有存在最新数据库更新的表名当中
                                    deleteTable(tableName);//删除当前无效的表
                                }
                            }
                        }

                        //更新结束
                    }


                }

            }
        }

        //=============================== 数据库操作 ====================================

        /**
         * @param oldTableName 旧表名称
         * @param NewTableName 新表名称
         * @return
         * @修改表名称
         */
        public Hibernate updateTableName(String oldTableName, String NewTableName) {
            String sql = "ALTER TABLE " + oldTableName + " RENAME TO " + NewTableName;
            sqLiteDatabase2.execSQL(sql);
            return this;
        }

        /**
         * @param oldTableNameClass 旧表名称
         * @param NewTableNameClass 新表名称
         * @return
         * @修改表名称
         */
        public Hibernate updateTableName(Class<?> oldTableNameClass, Class<?> NewTableNameClass) {
            String sql = "ALTER TABLE " + oldTableNameClass.getSimpleName() + " RENAME TO " + NewTableNameClass.getSimpleName();
            sqLiteDatabase2.execSQL(sql);
            return this;
        }

        /**
         * @param tableName 表名
         * @return
         * @获取表所有字段名
         */
        public List<String> getTableAllValue(String tableName) {
            String sql = "SELECT * FROM " + tableName + " WHERE 0";
            Cursor tempCursor = sqLiteDatabase2.rawQuery(sql, null);
            try {
                String[] columnNames = tempCursor.getColumnNames();

                List<String> tableNameList = new ArrayList<>();
                for (String str : columnNames) {
                    tableNameList.add(str);
                }
                return tableNameList;
            } finally {
                tempCursor.close();
            }

        }

        /**
         * @param tableClass 表名
         * @return
         * @获取表所有字段名
         */
        public List<String> getTableAllValue(Class<?> tableClass) {
            String sql = "SELECT * FROM " + tableClass.getSimpleName() + " WHERE 0";
            Cursor tempCursor = sqLiteDatabase2.rawQuery(sql, null);
            try {
                String[] columnNames = tempCursor.getColumnNames();
                List<String> tableNameList = new ArrayList<>();
                for (String str : columnNames) {
                    tableNameList.add(str);
                }
                return tableNameList;
            } finally {
                tempCursor.close();
            }

        }

        /**
         * @param tableName
         * @return
         * @删除表
         */
        public Hibernate deleteTable(String tableName) {
            String sql = "DROP TABLE " + tableName;
            sqLiteDatabase2.execSQL(sql);
            return this;
        }

        /**
         * @param tableClass
         * @return
         * @删除表
         */
        public Hibernate deleteTable(Class<?> tableClass) {
            String sql = "DROP TABLE " + tableClass.getSimpleName();
            sqLiteDatabase2.execSQL(sql);
            return this;
        }

        /**
         * @return
         * @获取当前数据库所有表名称
         */
        public List<String> getSQLAllTableName() {
            List<String> strList = new ArrayList<>();
            Cursor cursor = sqLiteDatabase2.rawQuery("select name from sqlite_master where type='table' order by name", null);
            while (cursor.moveToNext()) {
                String tableName = cursor.getString(0);
                if (!"sqlite_sequence".equals(tableName)) {//过滤掉系统自带的表
                    strList.add(tableName);
                }
            }
            return strList;
        }

        /**
         * @param tableName 表名
         * @return 存在返回 true 不存在返回 false
         * @监测当前数据库是否存在此表
         */
        public boolean isTable(String tableName) {
            List<String> sqlAllTableName = getSQLAllTableName();
            if (sqlAllTableName.contains(tableName)) {
                return true;
            }
            return false;
        }

        /**
         * @param tableClass 表名
         * @return 存在返回 true 不存在返回 false
         * @监测当前数据库是否存在此表
         */
        public boolean isTable(Class<?> tableClass) {
            List<String> sqlAllTableName = getSQLAllTableName();
            if (sqlAllTableName.contains(tableClass.getSimpleName())) {
                return true;
            }
            return false;
        }

        /**
         * @param oldTable 旧表
         * @param newTable 新表
         * @return
         * @导入表的数据 (自动匹配相同表字段自动导入数据)
         */
        public Hibernate inputTableData(String oldTable, String newTable) {

            List<String> tempSQLTableValue = getTableAllValue(oldTable);//获取上个版本表所有字段
            List<String> SQLTableValue = getTableAllValue(newTable);//获取当前最新版本数据库表所有字段
            //效验出 旧版本 与 新版本 数据库均有的字段
            List<String> SQLValue = new ArrayList<>();
            for (String tableValue : tempSQLTableValue) {//遍历旧版本，如果新版本也有就存入 待导入数据字段行列
                if (SQLTableValue.contains(tableValue)) {
                    SQLValue.add(tableValue);//存入容器中
                }
            }

            //生成可用的表字段
            String tableChars = "";//表字段
            for (String str : SQLValue) {
                tableChars += (str + ",");
            }
            tableChars = tableChars.substring(0, tableChars.length() - 1);//去掉SQLCode 最后一个无用逗号

            String inputSQL = "INSERT INTO " + newTable + "(" + tableChars + ") SELECT " + tableChars + " FROM " + oldTable;
            sqLiteDatabase2.execSQL(inputSQL);

            return this;
        }

        /**
         * @param oldTableClass 旧表
         * @param newTableClass 新表
         * @return
         * @导入表的数据 (自动匹配相同表字段自动导入数据)
         */
        public Hibernate inputTableData(Class<?> oldTableClass, Class<?> newTableClass) {

            String oldTable = oldTableClass.getSimpleName();
            String newTable = newTableClass.getSimpleName();

            List<String> tempSQLTableValue = getTableAllValue(oldTable);//获取上个版本表所有字段
            List<String> SQLTableValue = getTableAllValue(newTable);//获取当前最新版本数据库表所有字段
            //效验出 旧版本 与 新版本 数据库均有的字段
            List<String> SQLValue = new ArrayList<>();
            for (String tableValue : tempSQLTableValue) {//遍历旧版本，如果新版本也有就存入 待导入数据字段行列
                if (SQLTableValue.contains(tableValue)) {
                    SQLValue.add(tableValue);//存入容器中
                }
            }

            //生成可用的表字段
            String tableChars = "";//表字段
            for (String str : SQLValue) {
                tableChars += (str + ",");
            }
            tableChars = tableChars.substring(0, tableChars.length() - 1);//去掉SQLCode 最后一个无用逗号

            String inputSQL = "INSERT INTO " + newTable + "(" + tableChars + ") SELECT " + tableChars + " FROM " + oldTable;
            sqLiteDatabase2.execSQL(inputSQL);

            return this;
        }

        /**
         * @param oldTable     旧表
         * @param oldTableList 旧表集合
         * @param newTable     新表
         * @param newTableList 新表集合
         * @return
         * @导入表的数据 (指定匹配相同表字段自动导入数据)
         */
        public Hibernate inputTableData(String oldTable, List<String> oldTableList, String newTable, List<String> newTableList) {

            if (isTable(oldTable) && isTable(newTable)) {//如果当前数据库存在该表

                //生成旧的可用表字段
                String oldTableChar = "";
                for (String str : oldTableList) {
                    oldTableChar += (str + ",");
                }
                oldTableChar = oldTableChar.substring(0, oldTableChar.length() - 1);//去掉SQLCode 最后一个无用逗号

                //生成新的可用表字段
                String newTableChar = "";
                for (String str : newTableList) {
                    newTableChar += (str + ",");
                }
                newTableChar = newTableChar.substring(0, newTableChar.length() - 1);//去掉SQLCode 最后一个无用逗号


                String inputSQL = "INSERT INTO " + newTable + "(" + newTableChar + ") SELECT " + oldTableChar + " FROM " + oldTable;
                sqLiteDatabase2.execSQL(inputSQL);
            }

            return this;
        }

        /**
         * @param oldTableClass     旧表
         * @param oldTableList 旧表集合
         * @param newTableClass     新表
         * @param newTableList 新表集合
         * @return
         * @导入表的数据 (指定匹配相同表字段自动导入数据)
         */
        public Hibernate inputTableData(Class<?> oldTableClass, List<String> oldTableList, Class<?> newTableClass, List<String> newTableList) {

            String oldTable = oldTableClass.getSimpleName();
            String newTable = newTableClass.getSimpleName();


            if (isTable(oldTable) && isTable(newTable)) {//如果当前数据库存在该表

                //生成旧的可用表字段
                String oldTableChar = "";
                for (String str : oldTableList) {
                    oldTableChar += (str + ",");
                }
                oldTableChar = oldTableChar.substring(0, oldTableChar.length() - 1);//去掉SQLCode 最后一个无用逗号

                //生成新的可用表字段
                String newTableChar = "";
                for (String str : newTableList) {
                    newTableChar += (str + ",");
                }
                newTableChar = newTableChar.substring(0, newTableChar.length() - 1);//去掉SQLCode 最后一个无用逗号


                String inputSQL = "INSERT INTO " + newTable + "(" + newTableChar + ") SELECT " + oldTableChar + " FROM " + oldTable;
                sqLiteDatabase2.execSQL(inputSQL);
            }

            return this;
        }

        /**
         * 返回List或Map
         *
         * @param type
         * @param value
         * @return
         */
        public Object returnListOrMap(Class<?> type, String value) {

            if (type == List.class) {
                if (value == null || "null".equals(value))
                    return null;
                //如果是 List java.lang.String[1111, 2222, 3333]
                String valueType = value.substring(0, value.indexOf("["));
                String valueData = value.substring(value.indexOf("[") + 1, value.length() - 1);
                String[] arrayValue = valueData.split(",");

                switch (valueType) {
                    case "java.lang.String":
                        List<String> strList = new ArrayList<String>();
                        for (String str : arrayValue) {
                            strList.add(str);
                        }
                        return strList;
                    case "java.lang.Integer":
                        List<Integer> intList = new ArrayList<Integer>();
                        for (String str : arrayValue) {
                            intList.add(Integer.parseInt(str));
                        }
                        return intList;
                    case "java.lang.Boolean":
                        List<Boolean> booleanList = new ArrayList<Boolean>();
                        for (String str : arrayValue) {
                            booleanList.add(Boolean.parseBoolean(str));
                        }
                        return booleanList;
                    case "java.lang.Double":
                        List<Double> doubleList = new ArrayList<Double>();
                        for (String str : arrayValue) {
                            doubleList.add(Double.parseDouble(str));
                        }
                        return doubleList;
                    case "java.lang.Float":
                        List<Float> floatList = new ArrayList<Float>();
                        for (String str : arrayValue) {
                            floatList.add(Float.parseFloat(str));
                        }
                        return floatList;
                    case "java.sql.Time":
                        List<Time> timeList = new ArrayList<Time>();
                        for (String str : arrayValue) {
                            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                            Date d2 = null;
                            try {
                                d2 = format2.parse(str);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Time startTimeFmt = new Time(d2.getTime());
                            timeList.add(startTimeFmt);
                        }
                        return timeList;
                    case "java.util.Date":
                    case "java.sql.Date":
                        List<Date> dateList = new ArrayList<Date>();
                        for (String str : arrayValue) {
                            try {
                                Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(str);
                                dateList.add(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        return dateList;
                    case "java.lang.Long":
                        List<Long> longList = new ArrayList<Long>();
                        for (String str : arrayValue) {
                            longList.add(Long.parseLong(str));
                        }
                        return longList;
                    case "[B"://byte数组
                        List<Object> byteArrayList = new ArrayList<Object>();
                        for (String str : arrayValue) {
                            byteArrayList.add(str);
                        }
                        return byteArrayList;
                    case "java.util.ArrayList": {
                        //解析二级
                        valueData = valueData.substring(1, valueData.length() - 1);
                        String[] split = valueData.split("\\], \\[");
                        List<Object> listList = new ArrayList<Object>();
                        for (String str : split) {
                            List<Object> list = new ArrayList<Object>();
                            String[] split1 = str.split(", ");
                            for (String str1 : split1) {
                                if (str1 != null) {
                                    list.add(str1);
                                } else {
                                    list.add(str1);
                                }
                            }
                            listList.add(list);
                        }
                        return listList;
                    }
                    case "java.util.HashMap": {
                        //解析二级 Map {key1=value1, key2=value2}, {2=2, 22=22}
                        List<Object> mapList = new ArrayList<Object>();
                        valueData = valueData.substring(1, valueData.length() - 1);
                        String[] split = valueData.split("\\}, \\{");//key1=value1, key2=value2}, {2=2, 22=22
                        for (String str : split) {
                            Map<Object, Object> map = new HashMap<>();
                            String[] split1 = str.split(", ");//key1=value1, key2=value2
                            for (String str1 : split1) {
                                String[] split2 = str1.split("=");//key1=value1
                                map.put(split2[0], split2[1]);
                            }
                            mapList.add(map);
                        }
                        return mapList;
                    }
                    default: {
                        //非基础类型的数据处理

                        //获取集合中实体类的数据类型
                        Class<?> clazz = getClass();
                        try {
                            clazz = Class.forName(valueType);
                        } catch (ClassNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        //解析集合数据
                        arrayValue = valueData.split("\\},\\{");

                        //生成集合数据并返回
                        List<Object> mapList = new ArrayList<Object>();
                        for (int i = 0; i < arrayValue.length; i++) {
                            String str = arrayValue[i];
                            if (i == 0) {
                                str = str + "}";
                            } else if (i == arrayValue.length - 1) {
                                str = "{" + str;
                            } else {
                                str = "{" + str + "}";
                            }
                            Object o = null;
                            try {
                                o = JSON.fromJson(str, clazz);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mapList.add(o);
                        }
                        return mapList;
                    }
                }


            } else if (type == Map.class) {
                if (value == null || "null".equals(value))
                    return null;
                //如果是 Map java.lang.String[key1, key2, key3]
                //          java.lang.String[1号, 2号, 3号]

                String[] split = value.split("\n");
                if (split.length != 2) {
                    return null;
                }

                String keys = split[0];//map-key
                String values = split[1];//map-value

                // Key 解析
                int keysIndex = keys.indexOf("[");//解析普通类型
                if (keysIndex == -1) {
                    keysIndex = keys.indexOf("{");//解析特殊类型
                }
                String keysType = keys.substring(0, keysIndex);
                String keysData = keys.substring(keysIndex + 1, keys.length() - 1);
                String[] arraykeysData = keysData.split(",");//解析待带入的key值

                // value 解析
                int valuesIndex = values.indexOf("[");//解析普通类型
                if (valuesIndex == -1) {
                    valuesIndex = values.indexOf("{");//解析特殊类型
                }
                String valuesType = values.substring(0, valuesIndex);
                String valuesData = values.substring(valuesIndex + 1, values.length() - 1);

                Map<Object, Object> map = new HashMap<>();
                Class<?> keysTypeClass = null;
                Class<?> valuesTypeClass = null;
                try {
                    keysTypeClass = Class.forName(keysType);
                    valuesTypeClass = Class.forName(valuesType);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //解析key数据
                String[] keysDataArray = null;
                if (returnType(keysTypeClass) == null) {
                    //特殊类型
                    keysDataArray = keysData.split("\\},\\{");
                } else {
                    //普通类型
                    keysDataArray = keysData.split(", ");
                }

                //解析value数据
                String[] valuesDataArray = null;
                if (returnType(valuesTypeClass) == null) {
                    //特殊类型
                    valuesDataArray = valuesData.split("\\},\\{");

                } else {
                    //普通类型
                    valuesDataArray = valuesData.split(", ");
                }


                Object keyObject = null;
                Object valueObject = null;

                //生成Map
                for (int i = 0; i < keysDataArray.length; i++) {

                    String keyStr = keysDataArray[i];
                    String valueStr = valuesDataArray[i];

                    //如果key是特殊类型就做特殊处理
                    if (returnType(keysTypeClass) == null) {
                        if (i == 0) {
                            keyStr += "}";
                        } else if (i == keysDataArray.length - 1) {
                            keyStr = "{" + keyStr;
                        } else {
                            keyStr = "{" + keyStr + "}";
                        }
                        try {
                            keyObject = JSON.fromJson(keyStr, keysTypeClass);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            keyObject = keyStr.toString();
                        }
                    } else {
                        keyObject = keyStr.toString();
                    }

                    //如果value是特殊类型就做特殊处理
                    if (returnType(valuesTypeClass) == null) {
                        if (i == 0) {
                            valueStr += "}";
                        } else if (i == keysDataArray.length - 1) {
                            valueStr = "{" + valueStr;
                        } else {
                            valueStr = "{" + valueStr + "}";
                        }

                        try {
                            valueObject = JSON.fromJson(valueStr, valuesTypeClass);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            valueObject = valueStr.toString();
                        }

                    } else {
                        valueObject = valueStr.toString();
                    }

                    map.put(keyObject, valueObject);


                }

                return map;

            }


            return null;
        }

        /**
         * 返回类型
         *
         * @param type
         * @return
         */
        public Class returnType(Class<?> type) {

            switch (type.getName()) {
                case "java.lang.String":
                    return String.class;
                case "java.lang.Integer":

                    return Integer.class;
                case "java.lang.Boolean":
                    return Boolean.class;
                case "java.lang.Double":
                    return Double.class;
                case "java.lang.Float":
                    return Float.class;
                case "java.sql.Time":
                    return Time.class;
                case "java.util.Date":
                case "java.sql.Date":
                    return Date.class;
                case "java.lang.Long":
                    return Long.class;
                case "[B"://byte数组
                    return Byte.class;
                case "java.util.ArrayList": {
                    return ArrayList.class;
                }
                case "java.util.HashMap": {
                    return HashMap.class;
                }
                default:
                    //特殊类型
                    return null;
            }
        }

        /**
         * 返回类型
         *
         * @param type
         * @return
         */
        public Object returnTypeDataValue(Object objValue, Class<?> type) {
            if (objValue == null)
                return null;

            String objValueStr = objValue.toString();

            try {
                switch (type.getName()) {
                    case "java.lang.String":
                        return String.valueOf(objValue);
                    case "java.lang.Integer":
                        return Integer.parseInt(objValueStr);
                    case "java.lang.Boolean":
                        return Boolean.parseBoolean(objValueStr);
                    case "java.lang.Double":
                        return Double.parseDouble(objValueStr);
                    case "java.lang.Float":
                        return Float.parseFloat(objValueStr);
                    case "java.sql.Time":
                        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                        Date d2 = null;
                        try {
                            d2 = format2.parse(objValueStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Time startTimeFmt = new Time(d2.getTime());
                        return startTimeFmt;
                    case "java.util.Date":
                    case "java.sql.Date":
                        try {
                            Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(objValueStr);
                            return date;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return null;
                        }
                    case "java.lang.Long":
                        return Long.parseLong(objValueStr);
                    case "[B"://byte数组
                        return Byte.parseByte(objValueStr);
                    default:
                        //特殊类型
                        return null;
                }
            } catch (Exception e) {
                return null;
            }

        }

        /**
         * 查询表数据总数量
         *
         * @param table
         * @return 返回当前表数据总条数，如果当前表不存在则返回-1
         */
        public long queryTableDataCount(String table) {
            if (sqLiteDatabase2 == null || table == null)
                return -1;
            long count = 0;
            if (isTable(table)) {
                Cursor cursor = sqLiteDatabase2.rawQuery("select count(2) from " + table, null);
                cursor.moveToFirst();
                count = cursor.getLong(0);
                cursor.close();
            } else {
                count = -1;
            }
            return count;
        }

        /**
         * 查询表数据总数量
         *
         * @param tableClass
         * @return 返回当前表数据总条数，如果当前表不存在则返回-1
         */
        public long queryTableDataCount(Class<?> tableClass) {
            if (sqLiteDatabase2 == null || tableClass == null)
                return -1;
            long count = 0;
            String tableName = tableClass.getSimpleName();
            if (isTable(tableName)) {
                Cursor cursor = sqLiteDatabase2.rawQuery("select count(2) from " + tableName, null);
                cursor.moveToFirst();
                count = cursor.getLong(0);
                cursor.close();
            } else {
                count = -1;
            }

            return count;
        }

        /**
         * ======================================================== 表的 增删查改 代码 ========================================
         */

        private boolean status = true;//当前增删查改的状态
        private int statusNumber = 0;//数据库操作返回值

        /**
         * @return
         * @当前数据库操作后的状态
         */
        public boolean isStatus() {
            return status;
        }

        /**
         * @return
         * @数据库操作返回值
         */
        public int getStatusNumber() {
            return statusNumber;
        }


        //============================================= 无实体类查询 =================================

        /**
         * @param tableName     表名称
         * @param contentValues 内容值
         * @return
         * @保存
         */
        public Hibernate save(String tableName, ContentValues contentValues) {
            if (!isTable(tableName)) {
                err(getLineInfo(2), "保存的表不存在，操作失败");
                status = false;
                return this;
            }
            long insert = sqLiteDatabase2.insert(tableName, null, contentValues);
            statusNumber = (int) insert;
            //设置状态码
            if (insert == -1) {
                status = false;
            } else {
                status = true;
            }
            return this;
        }

        /**
         * @param tableName     表名
         * @param contentValues 内容值
         * @param condition     条件
         * @param valuesArray   条件值
         * @return
         * @修改
         */
        public Hibernate update(String tableName, ContentValues contentValues, String condition, String[] valuesArray) {
            if (!isTable(tableName)) {
                err(getLineInfo(2), "修改的表不存在，操作失败");
                status = false;
                return this;
            }
            statusNumber = sqLiteDatabase2.update(tableName, contentValues, condition, valuesArray);
            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param tableName   表名
         * @param condition   条件
         * @param valuesArray 条件值
         * @return
         * @删除
         */
        public Hibernate delete(String tableName, String condition, String[] valuesArray) {
            //进行删除
            if (!isTable(tableName)) {
                err(getLineInfo(2), "删除的表不存在，操作失败");
                status = false;
                return this;
            }
            int update = sqLiteDatabase2.delete(tableName, condition, valuesArray);
            if (update == 0) {
                status = false;
            } else {
                status = true;
            }
            return this;
        }

        /**
         * @return
         * @删除所有表
         */
        public Hibernate deleteAll(String tableName) {

            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "删除的表不存在，操作失败");
                status = false;
                return this;
            }
            statusNumber = sqLiteDatabase2.delete(tableName, null, null);
            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param tableName   表名
         * @param condition   条件
         * @param valuesArray 条件值
         * @return
         * @查询
         */
        public Cursor query(String tableName, String condition, String[] valuesArray) {

            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }
            Cursor cursor = sqLiteDatabase2.query(tableName, null, condition, valuesArray, null, null, orderByStr, limitStr);
            statusNumber = cursor.getCount();
            orderByStr = "";
            limitStr = "";
            cursor.moveToFirst();
            return cursor;
        }

        //============================================= 实体类查询 =================================

        /**
         * @param bean //要保存的对象
         * @return
         * @保存数据
         */
        public <T> Hibernate save(T bean) {

            if (bean == null) {
                err(getLineInfo(2), "保存的对象为null，操作失败！");
                status = false;
                return this;
            }

            Class<? extends Object> class1 = bean.getClass();
            String simpleName = class1.getSimpleName();//获取表名
            //检查数据库中是否存在该表
            if (!isTable(simpleName)) {
                err(getLineInfo(2), "当前数据库中 " + simpleName + " 表不存在，操作失败！.");
                status = false;
                return this;
            }
            //获取所有成员变量
            Field[] fields = class1.getDeclaredFields();
            //解析并设置保存的数据
            ContentValues contentValues = new ContentValues();
            for (Field field : fields) {
                field.setAccessible(true); // 设置属性是可以访问的
                try {
                    Class<?> type = field.getType();//获取字段类型
                    String name = field.getName();//获取属性名
                    Object val = field.get(bean);// 得到此属性的值

                    GT_Key annotation = field.getAnnotation(GT_Key.class);
                    if (annotation != null && annotation.autoincrement()) {//如果这个字段是Key并且是 "自增" 的就跳过
                        continue;
                    }

                    if (String.class == type) {
                        if (val == null) {
                            contentValues.put(name, "");
                            continue;
                        }
                        contentValues.put(name, (String) val);
                    } else if (int.class == type || Integer.class == type) {
                        if (val == null) {
                            contentValues.put(name, 0);
                            continue;
                        }
                        contentValues.put(name, (int) val);
                    } else if (boolean.class == type || Boolean.class == type) {
                        if (val == null) {
                            contentValues.put(name, false);
                            continue;
                        }
                        contentValues.put(name, (boolean) val);
                    } else if (double.class == type || Double.class == type) {
                        if (val == null) {
                            contentValues.put(name, 0);
                            continue;
                        }
                        contentValues.put(name, (double) val);
                    } else if (float.class == type || Float.class == type) {
                        if (val == null) {
                            contentValues.put(name, 0);
                            continue;
                        }
                        contentValues.put(name, (float) val);
                    } else if (Time.class == type) {
                        if (val == null) {
                            contentValues.put(name, "");
                            continue;
                        }
                        contentValues.put(name, String.valueOf((Time) val));
                    } else if (Date.class == type || java.sql.Date.class == type) {
                        if (val == null) {
                            contentValues.put(name, "");
                            continue;
                        }
                        contentValues.put(name, String.valueOf((Date) val));
                    } else if (long.class == type || Long.class == type) {
                        if (val == null) {
                            contentValues.put(name, 0);
                            continue;
                        }
                        contentValues.put(name, (long) val);
                    } else if (short.class == type || Short.class == type) {
                        if (val == null) {
                            contentValues.put(name, 0);
                            continue;
                        }
                        contentValues.put(name, (short) val);
                    } else if (byte[].class.equals(type)) {
                        contentValues.put(name, (byte[]) val);
                    } else if (List.class == type || Map.class == type) { //存储 List 与 Map 类型的
                        if (val == null) {
                            contentValues.put(name, "null");
                            continue;
                        }

                        String data = "";
                        //如果是 List 类型的存储
                        if (val.getClass() == ArrayList.class) {
                            ArrayList<?> list = (ArrayList<?>) val;
                            if (list.size() != 0) {
                                Object o = list.get(0);
                                if (o != null) {
                                    Class aClass = returnType(o.getClass());//返回当前类型
                                    if (aClass == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                        String jsonStr = JSON.toJson(val);//将 bean 转为 json
                                        data = o.getClass().getName() + jsonStr;
                                    } else {
                                        data = aClass.getName() + val.toString();
                                    }
                                } else {
                                    data = val.toString();
                                }
                            } else {
                                data = val.toString();
                            }
                        } else if (val.getClass() == HashMap.class) {
                            Set<?> keys = ((HashMap<?, ?>) val).keySet();
                            Collection<?> values = ((HashMap<?, ?>) val).values();
                            if (values.size() != 0) {
                                Object keyObj = keys.iterator().next();
                                Object valObj = values.iterator().next();
                                if (keyObj != null && valObj != null) {

                                    String keysData = "";
                                    String valuesData = "";

                                    if (returnType(keyObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                        keysData = JSON.toJson(keys);//将 bean 转为 json;
                                    } else {
                                        keysData = keys.toString();
                                    }

                                    if (returnType(valObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                        valuesData = JSON.toJson(values);//将 bean 转为 json;
                                    } else {
                                        valuesData = values.toString();
                                    }

                                    data =
                                            keyObj.getClass().getName() + keysData
                                                    + "\n" +
                                                    valObj.getClass().getName() + valuesData;
                                } else {
                                    data = val.toString();
                                }
                            } else {
                                data = val.toString();
                            }
                        }

                        contentValues.put(name, data);
                    } else {
                        String jsonStr = JSON.toJson(val);//将 bean 转为 json
                        contentValues.put(name, jsonStr);
                    }

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    exception(getLineInfo(2) + "数据出现问题");
                    status = false;
                    return this;
                }
            }
            //数据库操作
            long insert = sqLiteDatabase2.insert(simpleName, null, contentValues);
            statusNumber = (int) insert;
            //设置状态码
            if (insert == -1) {
                status = false;
            } else {
                status = true;
            }
            return this;
        }

        /**
         * @param bean       更新的表数据(实体类)
         * @param conditions 修改条件
         * @param values2     修改条件对应值
         * @return
         * @更新表
         */
        public Hibernate update(Class<?> tableClass, Object bean, Object conditions, Object values2) {

            if (tableClass == null || bean == null || conditions == null || values2 == null) {
                err(getLineInfo(2), "修改的数据为null，操作失败");
                status = false;
                return this;
            }

            //监测是否为 String 或 String[] 类型
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values2.getClass().getSimpleName();
            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "修改的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return this;
            }
            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "修改的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return this;
            }

            if (conditionsType.equals("String[]") && valuesType.equals("String[]")) {
                String[] a = (String[]) conditions;
                String[] b = (String[]) values2;

                if (a.length == 0 || b.length == 0) {
                    err(getLineInfo(2), "修改的条件或条件值为null，操作失败");
                    status = false;
                    return this;
                }

                if (a.length != b.length) {
                    err(getLineInfo(2), "修改的数据的条件与条件值不匹配，操作失败");
                    status = false;
                    return this;
                }
            }

            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }
                condition = condition.substring(0, condition.lastIndexOf("and"));//去掉最后一个多余 and
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            String[] valuesArray = null;//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values2;
            } else if (valuesType.equals("String")) {
                valuesArray = new String[1];
                valuesArray[0] = (String) values2;
            }


            statusNumber = 0;
            Class<? extends Object> class1 = bean.getClass();
            String simpleName = tableClass.getSimpleName();//获取表名
            //检查数据库中是否存在该表
            if (!isTable(simpleName)) {
                err(getLineInfo(2), "当前数据库中 " + simpleName + " 表不存在，操作失败！..");
                status = false;
                return this;
            }

            //判断更新对象是 实体类 还是 ContentValues 对象
            if (ContentValues.class != bean.getClass()) {
                ContentValues contentValues = new ContentValues();

                Field[] fields = class1.getDeclaredFields();//获取所有成员变量
                for (Field field : fields) {
                    field.setAccessible(true); // 设置些属性是可以访问的
                    try {
                        Class<?> type = field.getType();//获取字段类型
                        String name = field.getName();//获取属性名
                        Object val = field.get(bean);// 得到此属性的值

                        GT_Key annotation = field.getAnnotation(GT_Key.class);
                        if (annotation != null && annotation.autoincrement()) {//如果这个字段是Key并且是自增的就跳过
                            continue;
                        }

                        if (String.class == type) {
                            contentValues.put(name, (String) val);
                        } else if (int.class == type) {
                            contentValues.put(name, (int) val);
                        } else if (Integer.class == type) {
                            contentValues.put(name, (Integer) val);
                        } else if (boolean.class == type || Boolean.class == type) {
                            contentValues.put(name, (boolean) val);
                        } else if (double.class == type || Double.class == type) {
                            contentValues.put(name, (double) val);
                        } else if (float.class == type || Float.class == type) {
                            contentValues.put(name, (float) val);
                        } else if (Time.class == type) {
                            contentValues.put(name, String.valueOf((Time) val));
                        } else if (Date.class == type || java.sql.Date.class == type) {
                            contentValues.put(name, String.valueOf((Date) val));
                        } else if (long.class == type || Long.class == type) {
                            contentValues.put(name, (long) val);
                        } else if (short.class == type || Short.class == type) {
                            contentValues.put(name, (short) val);
                        } else if (byte[].class == type) {
                            contentValues.put(name, (byte[]) val);
                        } else if (List.class == type || Map.class == type) { //存储 List 与 Map 类型的
                            if (val == null) {
                                contentValues.put(name, "null");
                                continue;
                            }
                            String data = "";
                            //如果是 List 类型的存储
                            if (val.getClass() == ArrayList.class) {
                                ArrayList<?> list = (ArrayList<?>) val;
                                if (list.size() != 0) {
                                    Object o = list.get(0);
                                    if (o != null) {
                                        Class aClass = returnType(o.getClass());//返回当前类型
                                        if (aClass == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            String jsonStr = JSON.toJson(val);//将 bean 转为 json
                                            data = o.getClass().getName() + jsonStr;
                                        } else {
                                            data = aClass.getName() + val.toString();
                                        }
                                    } else {
                                        data = val.toString();
                                    }
                                } else {
                                    data = val.toString();
                                }
                            } else if (val.getClass() == HashMap.class) {
                                Set<?> keys = ((HashMap<?, ?>) val).keySet();
                                Collection<?> values = ((HashMap<?, ?>) val).values();
                                if (values.size() != 0) {
                                    Object keyObj = keys.iterator().next();
                                    Object valObj = values.iterator().next();
                                    if (keyObj != null && valObj != null) {

                                        String keysData = "";
                                        String valuesData = "";

                                        if (returnType(keyObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            keysData = JSON.toJson(keys);//将 bean 转为 json;
                                        } else {
                                            keysData = keys.toString();
                                        }

                                        if (returnType(valObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            valuesData = JSON.toJson(values);//将 bean 转为 json;
                                        } else {
                                            valuesData = values.toString();
                                        }

                                        data =
                                                keyObj.getClass().getName() + keysData
                                                        + "\n" +
                                                        valObj.getClass().getName() + valuesData;
                                    } else {
                                        data = val.toString();
                                    }
                                } else {
                                    data = val.toString();
                                }
                            }

                            contentValues.put(name, data);
                        } else {
                            String jsonStr = JSON.toJson(val);//将 bean 转为 json
                            contentValues.put(name, jsonStr);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        exception(getLineInfo(2) + "保存数据出现问题");
                        status = false;
                        return this;
                    }
                }
                statusNumber = sqLiteDatabase2.update(simpleName, contentValues, condition, valuesArray);
            } else {
                statusNumber = sqLiteDatabase2.update(simpleName, (ContentValues) bean, condition, valuesArray);
            }

            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param tableClass 修改哪张表
         * @param bean       修改表的 实体类 或 ContentValues 值
         * @param keyValue   修改表里的那个ID行数据
         * @return
         * @修改表
         */
        public Hibernate update(Class<?> tableClass, Object bean, Object keyValue) {

            //判空
            if (bean == null || keyValue == null || tableClass == null) {
                err(getLineInfo(2), "修改的参数为null，操作失败");
                status = false;
                return this;
            }

            //初始化与获取必要属性
            ContentValues contentValues = new ContentValues();
            Class<? extends Object> class1 = bean.getClass();
            String simpleName = tableClass.getSimpleName();//获取表名
            statusNumber = 0;

            //判断这个表是否存在
            List<String> tableAllValue = null;
            if (isTable(simpleName)) {
                tableAllValue = getTableAllValue(simpleName);
                if (tableAllValue == null || tableAllValue.size() == 0) {
                    err(getLineInfo(2), "修改的表数据为空，操作失败");
                    status = false;
                    return this;
                }
            } else {
                err(getLineInfo(2), "修改的表不存在，操作失败");
                status = false;
                return this;
            }

            //判断更新对象是 实体类 还是 ContentValues 对象
            if (ContentValues.class != bean.getClass()) {
                Field[] fields = class1.getDeclaredFields();//获取所有成员变量
                for (Field field : fields) {
                    field.setAccessible(true); // 设置些属性是可以访问的
                    try {
                        Class<?> type = field.getType();//获取字段类型
                        String name = field.getName();//获取属性名
                        Object val = field.get(bean);// 得到此属性的值

                        GT_Key annotation = field.getAnnotation(GT_Key.class);
                        if (annotation != null && annotation.autoincrement()) {//如果这个字段是Key并且是自增的就跳过
                            continue;
                        }

                        if (String.class == type) {
                            contentValues.put(name, (String) val);
                        } else if (int.class == type) {
                            contentValues.put(name, (int) val);
                        } else if (Integer.class == type) {
                            contentValues.put(name, (Integer) val);
                        } else if (boolean.class == type || Boolean.class == type) {
                            contentValues.put(name, (boolean) val);
                        } else if (double.class == type || Double.class == type) {
                            contentValues.put(name, (double) val);
                        } else if (float.class == type || Float.class == type) {
                            contentValues.put(name, (float) val);
                        } else if (Time.class == type) {
                            contentValues.put(name, String.valueOf((Time) val));
                        } else if (Date.class == type || java.sql.Date.class == type) {
                            contentValues.put(name, String.valueOf((Date) val));
                        } else if (long.class == type || Long.class == type) {
                            contentValues.put(name, (long) val);
                        } else if (short.class == type || Short.class == type) {
                            contentValues.put(name, (short) val);
                        } else if (byte[].class == type) {
                            contentValues.put(name, (byte[]) val);
                        } else if (List.class == type || Map.class == type) { //存储 List 与 Map 类型的
                            if (val == null) {
                                contentValues.put(name, "null");
                                continue;
                            }
                            String data = "";
                            //如果是 List 类型的存储
                            if (val.getClass() == ArrayList.class) {
                                ArrayList<?> list = (ArrayList<?>) val;
                                if (list.size() != 0) {
                                    Object o = list.get(0);
                                    if (o != null) {
                                        Class aClass = returnType(o.getClass());//返回当前类型
                                        if (aClass == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            String jsonStr = JSON.toJson(val);//将 bean 转为 json
                                            data = o.getClass().getName() + jsonStr;
                                        } else {
                                            data = aClass.getName() + val.toString();
                                        }
                                    } else {
                                        data = val.toString();
                                    }
                                } else {
                                    data = val.toString();
                                }
                            } else if (val.getClass() == HashMap.class) {
                                Set<?> keys = ((HashMap<?, ?>) val).keySet();
                                Collection<?> values = ((HashMap<?, ?>) val).values();
                                if (values.size() != 0) {
                                    Object keyObj = keys.iterator().next();
                                    Object valObj = values.iterator().next();
                                    if (keyObj != null && valObj != null) {

                                        String keysData = "";
                                        String valuesData = "";

                                        if (returnType(keyObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            keysData = JSON.toJson(keys);//将 bean 转为 json;
                                        } else {
                                            keysData = keys.toString();
                                        }

                                        if (returnType(valObj.getClass()) == null) {//如果是特殊类型 ，那就进过json 处理 再存储
                                            valuesData = JSON.toJson(values);//将 bean 转为 json;
                                        } else {
                                            valuesData = values.toString();
                                        }

                                        data =
                                                keyObj.getClass().getName() + keysData
                                                        + "\n" +
                                                        valObj.getClass().getName() + valuesData;
                                    } else {
                                        data = val.toString();
                                    }
                                } else {
                                    data = val.toString();
                                }
                            }

                            contentValues.put(name, data);
                        } else {
                            String jsonStr = JSON.toJson(val);//将 bean 转为 json
                            contentValues.put(name, jsonStr);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        exception(getLineInfo(2) + "数据出现问题");
                        status = false;
                        return this;
                    }
                }
                statusNumber = sqLiteDatabase2.update(simpleName, contentValues, tableAllValue.get(0) + "= ?", new String[]{keyValue.toString()});
            } else {
                statusNumber = sqLiteDatabase2.update(simpleName, (ContentValues) bean, tableAllValue.get(0) + "= ?", new String[]{keyValue.toString()});
            }

            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param tableClass
         * @param contentValues
         * @return
         * @更新这张表全部数据
         */
        public Hibernate updateAll(Class<?> tableClass, ContentValues contentValues) {

            //判空
            if (contentValues == null || tableClass == null) {
                err(getLineInfo(2), "修改的参数为null，操作失败");
                status = false;
                return this;
            }

            //初始化与获取必要属性
            String simpleName = tableClass.getSimpleName();//获取表名
            statusNumber = 0;

            //判断这个表是否存在
            if (!isTable(simpleName)) {
                err(getLineInfo(2), "修改的表不存在，操作失败");
                status = false;
                return this;
            }
            statusNumber = sqLiteDatabase2.update(simpleName, contentValues, null, null);
            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param beanClass  删除那张表数据
         * @param conditions 删除条件
         * @param values     删除条件对应值
         * @return
         * @删除表
         */
        public Hibernate delete(Class<?> beanClass, Object conditions, Object values) {

            if (beanClass == null) {
                err(getLineInfo(2), "删除的 beanClass 数据为null，操作失败");
                return this;
            }

            if (conditions == null) {
                err(getLineInfo(2), "删除的 conditions 数据为null，操作失败");
                return this;
            }

            if (values == null) {
                err(getLineInfo(2), "删除的 values 数据为null，操作失败");
                return this;
            }

            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                return this;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                return this;
            }

            if (conditionsType.equals("String[]") && valuesType.equals("String[]")) {
                String[] a = (String[]) conditions;
                String[] b = (String[]) values;

                if (a.length == 0 || b.length == 0) {
                    err(getLineInfo(2), "删除的条件或条件值为null，操作失败");
                    status = false;
                    return this;
                }

                if (a.length != b.length) {
                    err(getLineInfo(2), "删除的数据的条件与条件值不匹配，操作失败");
                    status = false;
                    return this;
                }
            }


            Class<? extends Object> class1 = beanClass;
            String simpleName = class1.getSimpleName();//获取表名

            //检查数据库中是否存在该表
            if (!isTable(simpleName)) {
                err(getLineInfo(2), "当前数据库中 " + simpleName + " 表不存在，操作失败！...");
                status = false;
                return this;
            }

            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }
                condition = condition.substring(0, condition.lastIndexOf("and"));//去掉最后一个多余 and
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //进行更新
            int update = sqLiteDatabase2.delete(simpleName, condition, valuesArray);

            if (update == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param beanClass 删除的表
         * @param keyValue  删除的ID
         * @return
         * @更具ID删除
         */
        public Hibernate delete(Class<?> beanClass, Object keyValue) {

            if (beanClass == null) {
                err(getLineInfo(2), "删除的 beanClass 数据为null，操作失败");
                return this;
            }

            if (keyValue == null) {
                err(getLineInfo(2), "删除的 values 数据为null，操作失败");
                return this;
            }

            Class<? extends Object> class1 = beanClass;
            String simpleName = class1.getSimpleName();//获取表名

            //检查数据库中是否存在该表
            //判断这个表是否存在
            List<String> tableAllValue = null;
            if (isTable(simpleName)) {
                tableAllValue = getTableAllValue(simpleName);
                if (tableAllValue == null || tableAllValue.size() == 0) {
                    err(getLineInfo(2), "修改的表数据为空，操作失败");
                    status = false;
                    return this;
                }
            } else {
                err(getLineInfo(2), "修改的表不存在，操作失败");
                status = false;
                return this;
            }

            //进行更新
            int update = sqLiteDatabase2.delete(simpleName, tableAllValue.get(0) + "= ?", new String[]{keyValue.toString()});

            if (update == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        /**
         * @param tableClass
         * @return
         * @删除所有表
         */
        public Hibernate deleteAll(Class<?> tableClass) {

            //初始化与获取必要属性
            String simpleName = tableClass.getSimpleName();//获取表名
            statusNumber = 0;

            //判断这个表是否存在
            if (!isTable(simpleName)) {
                err(getLineInfo(2), "修改的表不存在，操作失败");
                status = false;
                return this;
            }
            statusNumber = sqLiteDatabase2.delete(simpleName, null, null);
            //进行更新
            if (statusNumber == 0) {
                status = false;
            } else {
                status = true;
            }

            return this;
        }

        // SQL 查询

        private String orderByStr = "";//排序
        private String limitStr = "";//限量

        public Hibernate flashback(String orderByStr) {
            this.orderByStr = orderByStr + " desc";
            return this;
        }

        public Hibernate limit(int limitStr) {
            this.limitStr = limitStr + "";
            return this;
        }

        public Hibernate limit(int limitStart, int limitEnd) {
            this.limitStr = limitStart + "," + limitEnd;
            return this;
        }

        /**
         * @param tableNameClass 查询的表
         * @param values         查询ID
         * @param <T>
         * @return
         * @根据表ID查询数据
         */
        public <T> T query(Class<T> tableNameClass, Object values) {

            if (tableNameClass == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            T bean = null;//定义泛型
            try {
                bean = tableNameClass.newInstance();//实体化
            } catch (IllegalAccessException e) {
                //                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            }

            String[] returnValue = null;//获取当前类的所有字段
            List<String> tableAllValue = getTableAllValue(tableName);
            if (tableAllValue != null && tableAllValue.size() > 0) {
                returnValue = new String[tableAllValue.size()];
                for (int i = 0; i < tableAllValue.size(); i++) {
                    returnValue[i] = tableAllValue.get(i);
                }
            }

            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                String valueName = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);

                //反射生成对象并注入
                Cursor cursor = sqLiteDatabase2.query(tableName, returnValue, tableAllValue.get(0) + " = ?", new String[]{values.toString()}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();//移动到首位
                    for (String str : returnValue) {
                        Object obj = null;
                        if (str.equals(valueName)) {
                            if (String.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (int.class == type || Integer.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (boolean.class == type || Boolean.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                if (value == 1) {
                                    obj = true;
                                } else if (value == 0) {
                                    obj = false;
                                }
                            } else if (double.class == type || Double.class == type) {
                                double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (float.class == type || Float.class == type) {
                                float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (Time.class == type) {
                                String time = cursor.getString(cursor.getColumnIndex(valueName));
                                if (time == null || time.length() == 0) {
                                    time = new Time(System.currentTimeMillis()).toString();
                                }
                                SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                Date d2 = null;
                                try {
                                    d2 = format2.parse(time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Time startTimeFmt = new Time(d2.getTime());
                                obj = startTimeFmt;
                            } else if (Date.class == type || java.sql.Date.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                try {
                                    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                    obj = date;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (long.class == type || Long.class == type) {
                                long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (short.class == type || Short.class == type) {
                                short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (byte[].class.equals(type)) {
                                byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (List.class == type || Map.class == type) {

                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                if (value == null || "null".equals(value)) {
                                    obj = null;
                                } else {
                                    //第二次转换 List OR Map
                                    obj = returnListOrMap(type, value);
                                }
                            } else {
                                //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                String name = field.getName();//获取属性名
                                Object val = null;
                                try {
                                    val = field.get(bean);// 得到此属性的值
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    obj = JSON.fromJson(value, type);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        if (obj != null) {
                            try {
                                field.set(bean, obj);
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                cursor.close();//释放资源
            }

            return bean;
        }

        /**
         * @param tableNameClass 查询的表
         * @param conditions     查询条件
         * @param values         查询条件值
         * @param <T>
         * @return
         * @查询一条数据
         */
        public <T> T query(Class<T> tableNameClass, Object conditions, Object values) {

            if (tableNameClass == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            T bean = null;//定义泛型
            try {
                bean = tableNameClass.newInstance();//实体化
            } catch (IllegalAccessException e) {
                //                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            }

            String[] returnValue = null;//获取当前类的所有字段
            List<String> tableAllValue = getTableAllValue(tableName);
            if (tableAllValue != null && tableAllValue.size() > 0) {
                returnValue = new String[tableAllValue.size()];
                for (int i = 0; i < tableAllValue.size(); i++) {
                    returnValue[i] = tableAllValue.get(i);
                }
            }

            for (Field field : bean.getClass().getDeclaredFields()) {
                String valueName = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);

                //反射生成对象并注入
                Cursor cursor = sqLiteDatabase2.query(tableName, returnValue, condition, valuesArray, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();//移动到首位
                    for (String str : returnValue) {
                        Object obj = null;
                        if (str.equals(valueName)) {
                            if (String.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (int.class == type || Integer.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (boolean.class == type || Boolean.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                if (value == 1) {
                                    obj = true;
                                } else if (value == 0) {
                                    obj = false;
                                }
                            } else if (double.class == type || Double.class == type) {
                                double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (float.class == type || Float.class == type) {
                                float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (Time.class == type) {
                                String time = cursor.getString(cursor.getColumnIndex(valueName));
                                if (time == null || time.length() == 0) {
                                    time = new Time(System.currentTimeMillis()).toString();
                                }
                                SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                Date d2 = null;
                                try {
                                    d2 = format2.parse(time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Time startTimeFmt = new Time(d2.getTime());
                                obj = startTimeFmt;
                            } else if (Date.class == type || java.sql.Date.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                try {
                                    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                    obj = date;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (long.class == type || Long.class == type) {
                                long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (short.class == type || Short.class == type) {
                                short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (byte[].class.equals(type)) {
                                byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (List.class == type || Map.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                if (value == null || "null".equals(value)) {
                                    obj = null;
                                } else {
                                    //第二次转换 List OR Map
                                    obj = returnListOrMap(type, value);
                                }
                            } else {
                                //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                String name = field.getName();//获取属性名
                                Object val = null;
                                try {
                                    val = field.get(bean);// 得到此属性的值
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    obj = JSON.fromJson(value, type);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        if (obj != null) {
                            try {
                                field.set(bean, obj);
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                cursor.close();//释放资源
            }

            return bean;
        }

        /**
         * @param tableNameClass 查询的表
         * @param returnValue    返回的字段
         * @param conditions     查询条件
         * @param values         查询条件值
         * @param <T>
         * @return
         * @查询一条数据
         */
        public <T> T query(Class<T> tableNameClass, Object returnValue, Object conditions, Object values) {

            if (tableNameClass == null || returnValue == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String returnValueType = returnValue.getClass().getSimpleName();
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!returnValueType.equals("String") && !returnValueType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 returnValue 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] returnValues = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                returnValues = (String[]) returnValue;
            } else if (valuesType.equals("String")) {
                returnValues[0] = (String) returnValue;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            T bean = null;//定义泛型
            try {
                bean = tableNameClass.newInstance();//实体化
            } catch (IllegalAccessException e) {
                //                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            }

            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                String valueName = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);

                //反射生成对象并注入
                Cursor cursor = sqLiteDatabase2.query(tableName, returnValues, condition, valuesArray, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();//移动到首位
                    for (String str : (String[]) returnValue) {
                        Object obj = null;
                        if (str.equals(valueName)) {
                            if (String.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (int.class == type || Integer.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (boolean.class == type || Boolean.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                if (value == 1) {
                                    obj = true;
                                } else if (value == 0) {
                                    obj = false;
                                }
                            } else if (double.class == type || Double.class == type) {
                                double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (float.class == type || Float.class == type) {
                                float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (Time.class == type) {
                                String time = cursor.getString(cursor.getColumnIndex(valueName));
                                if (time == null || time.length() == 0) {
                                    time = new Time(System.currentTimeMillis()).toString();
                                }
                                SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                Date d2 = null;
                                try {
                                    d2 = format2.parse(time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Time startTimeFmt = new Time(d2.getTime());
                                obj = startTimeFmt;
                            } else if (Date.class == type || java.sql.Date.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                try {
                                    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                    obj = date;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (long.class == type || Long.class == type) {
                                long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (short.class == type || Short.class == type) {
                                short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (byte[].class.equals(type)) {
                                byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (List.class == type || Map.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                if (value == null || "null".equals(value)) {
                                    obj = null;
                                } else {
                                    //第二次转换 List OR Map
                                    obj = returnListOrMap(type, value);
                                }
                            } else {
                                //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                String name = field.getName();//获取属性名
                                Object val = null;
                                try {
                                    val = field.get(bean);// 得到此属性的值
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    obj = JSON.fromJson(value, type);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        if (obj != null) {
                            try {
                                field.set(bean, obj);
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                cursor.close();//释放资源
            }

            return bean;
        }

        /**
         * @param tableNameClass
         * @param returnValue
         * @param conditions
         * @param values
         * @param <T>
         * @return
         * @查询最后一条数据
         */
        public <T> T queryLast(Class<T> tableNameClass, Object returnValue, Object conditions, Object values) {

            if (tableNameClass == null || returnValue == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String returnValueType = returnValue.getClass().getSimpleName();
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!returnValueType.equals("String") && !returnValueType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 returnValue 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] returnValues = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                returnValues = (String[]) returnValue;
            } else if (valuesType.equals("String")) {
                returnValues[0] = (String) returnValue;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            T bean = null;//定义泛型
            try {
                bean = tableNameClass.newInstance();//实体化
            } catch (IllegalAccessException e) {
                //                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            }

            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                String valueName = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);

                //反射生成对象并注入
                Cursor cursor = sqLiteDatabase2.query(tableName, returnValues, condition, valuesArray, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToLast();//移动到最后一位
                    for (String str : (String[]) returnValue) {
                        Object obj = null;
                        if (str.equals(valueName)) {
                            if (String.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (int.class == type || Integer.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (boolean.class == type || Boolean.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                if (value == 1) {
                                    obj = true;
                                } else if (value == 0) {
                                    obj = false;
                                }
                            } else if (double.class == type || Double.class == type) {
                                double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (float.class == type || Float.class == type) {
                                float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (Time.class == type) {
                                String time = cursor.getString(cursor.getColumnIndex(valueName));
                                if (time == null || time.length() == 0) {
                                    time = new Time(System.currentTimeMillis()).toString();
                                }
                                SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                Date d2 = null;
                                try {
                                    d2 = format2.parse(time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Time startTimeFmt = new Time(d2.getTime());
                                obj = startTimeFmt;
                            } else if (Date.class == type || java.sql.Date.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                try {
                                    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                    obj = date;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (long.class == type || Long.class == type) {
                                long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (short.class == type || Short.class == type) {
                                short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (byte[].class.equals(type)) {
                                byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (List.class == type || Map.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else {
                                err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                            }
                        }

                        if (obj != null) {
                            try {
                                field.set(bean, obj);
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                cursor.close();//释放资源
            }

            return bean;
        }

        /**
         * @param tableNameClass
         * @param returnValue
         * @param conditions
         * @param values
         * @param <T>
         * @return
         * @查询第一条数据
         */
        public <T> T queryFirst(Class<T> tableNameClass, Object returnValue, Object conditions, Object values) {

            if (tableNameClass == null || returnValue == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String returnValueType = returnValue.getClass().getSimpleName();
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!returnValueType.equals("String") && !returnValueType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 returnValue 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] returnValues = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                returnValues = (String[]) returnValue;
            } else if (valuesType.equals("String")) {
                returnValues[0] = (String) returnValue;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            T bean = null;//定义泛型
            try {
                bean = tableNameClass.newInstance();//实体化
            } catch (IllegalAccessException e) {
                //                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            } catch (InstantiationException e) {
                e.printStackTrace();
                err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                status = false;
                return null;
            }

            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                String valueName = field.getName();
                Class<?> type = field.getType();
                field.setAccessible(true);

                //反射生成对象并注入
                Cursor cursor = sqLiteDatabase2.query(tableName, returnValues, condition, valuesArray, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();//移动到首位
                    for (String str : (String[]) returnValue) {
                        Object obj = null;
                        if (str.equals(valueName)) {
                            if (String.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (int.class == type || Integer.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (boolean.class == type || Boolean.class == type) {
                                int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                if (value == 1) {
                                    obj = true;
                                } else if (value == 0) {
                                    obj = false;
                                }
                            } else if (double.class == type || Double.class == type) {
                                double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (float.class == type || Float.class == type) {
                                float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (Time.class == type) {
                                String time = cursor.getString(cursor.getColumnIndex(valueName));
                                if (time == null || time.length() == 0) {
                                    time = new Time(System.currentTimeMillis()).toString();
                                }
                                SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                Date d2 = null;
                                try {
                                    d2 = format2.parse(time);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Time startTimeFmt = new Time(d2.getTime());
                                obj = startTimeFmt;
                            } else if (Date.class == type || java.sql.Date.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                try {
                                    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                    obj = date;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else if (long.class == type || Long.class == type) {
                                long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (short.class == type || Short.class == type) {
                                short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (byte[].class.equals(type)) {
                                byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else if (List.class == type || Map.class == type) {
                                String value = cursor.getString(cursor.getColumnIndex(valueName));
                                obj = value;
                            } else {
                                err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                            }
                        }

                        if (obj != null) {
                            try {
                                field.set(bean, obj);
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                cursor.close();//释放资源
            }

            return bean;
        }

        //多个查询

        /**
         * @param tableNameClass
         * @param <T>
         * @return
         * @根据表查询出表中所有数据
         */
        public <T> List<T> queryAll(Class<T> tableNameClass) {

            if (tableNameClass == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            String[] returnValue = null;//获取当前类的所有字段
            List<String> tableAllValue = getTableAllValue(tableName);
            if (tableAllValue != null && tableAllValue.size() > 0) {
                returnValue = new String[tableAllValue.size()];
                for (int i = 0; i < tableAllValue.size(); i++) {
                    returnValue[i] = tableAllValue.get(i);
                }
            }

            //反射生成对象并注入
            Cursor cursor = sqLiteDatabase2.query(tableName, null, null, null, null, null, orderByStr, limitStr);
            List<T> beanList = new ArrayList<T>();//创建容器
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//移动到首位
                for (int i = 0; i < cursor.getCount(); i++) {
                    T bean = null;//定义泛型
                    try {
                        bean = tableNameClass.newInstance();//实体化
                    } catch (IllegalAccessException e) {
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    }

                    Field[] fields = bean.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        String valueName = field.getName();
                        Class<?> type = field.getType();
                        field.setAccessible(true);
                        for (String str : (String[]) returnValue) {
                            Object obj = null;
                            if (str.equals(valueName)) {
                                if (String.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (int.class == type || Integer.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (boolean.class == type || Boolean.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                    if (value == 1) {
                                        obj = true;
                                    } else if (value == 0) {
                                        obj = false;
                                    }
                                } else if (double.class == type || Double.class == type) {
                                    double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (float.class == type || Float.class == type) {
                                    float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (Time.class == type) {
                                    String time = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (time == null || time.length() == 0) {
                                        time = new Time(System.currentTimeMillis()).toString();
                                    }
                                    SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                    Date d2 = null;
                                    try {
                                        d2 = format2.parse(time);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Time startTimeFmt = new Time(d2.getTime());
                                    obj = startTimeFmt;
                                } else if (Date.class == type || java.sql.Date.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    try {
                                        Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                        obj = date;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else if (long.class == type || Long.class == type) {
                                    long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (short.class == type || Short.class == type) {
                                    short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (byte[].class.equals(type)) {
                                    byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (List.class == type || Map.class == type) {

                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (value == null || "null".equals(value)) {
                                        obj = null;
                                    } else {
                                        //第二次转换 List OR Map
                                        obj = returnListOrMap(type, value);
                                    }
                                } else {
                                    //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    String name = field.getName();//获取属性名
                                    Object val = null;
                                    try {
                                        val = field.get(bean);// 得到此属性的值
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        obj = JSON.fromJson(value, type);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            try {
                                if (obj != null) {
                                    field.set(bean, obj);
                                }
                                continue;
                            } catch (Exception e) {
                                //                                    e.printStackTrace();
                                errs("查询数据中出现了无法转换的类型:" + e);
                            }
                        }

                    }
                    beanList.add(bean);//存入查询到的数据
                    cursor.moveToNext();//移动到下一位
                }
            }
            cursor.close();//释放资源

            orderByStr = "";
            limitStr = "";

            return beanList;
        }

        /**
         * @param <T>
         * @param tableNameClass 查询的表
         * @param conditions     查询条件
         * @param values         查询条件值
         * @return
         * @查询多条数据
         */
        public <T> List<T> queryAll(Class<T> tableNameClass, Object conditions, Object values) {

            if (tableNameClass == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            String[] returnValue = null;//获取当前类的所有字段
            List<String> tableAllValue = getTableAllValue(tableName);
            if (tableAllValue != null && tableAllValue.size() > 0) {
                returnValue = new String[tableAllValue.size()];
                for (int i = 0; i < tableAllValue.size(); i++) {
                    returnValue[i] = tableAllValue.get(i);
                }
            }

            //反射生成对象并注入
            Cursor cursor = sqLiteDatabase2.query(tableName, returnValue, condition, valuesArray, null, null, orderByStr, limitStr);
            List<T> beanList = new ArrayList<T>();//创建容器
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//移动到首位
                for (int i = 0; i < cursor.getCount(); i++) {

                    T bean = null;//定义泛型
                    try {
                        bean = tableNameClass.newInstance();//实体化
                    } catch (IllegalAccessException e) {
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    }


                    Field[] fields = bean.getClass().getDeclaredFields();

                    for (Field field : fields) {
                        String valueName = field.getName();
                        Class<?> type = field.getType();
                        field.setAccessible(true);

                        /**
                         * 思路：
                         * 将反射循环注入赋值放在这里进行操作
                         */
                        for (String str : (String[]) returnValue) {
                            Object obj = null;
                            if (str.equals(valueName)) {
                                if (String.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (int.class == type || Integer.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (boolean.class == type || Boolean.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                    if (value == 1) {
                                        obj = true;
                                    } else if (value == 0) {
                                        obj = false;
                                    }
                                } else if (double.class == type || Double.class == type) {
                                    double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (float.class == type || Float.class == type) {
                                    float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (Time.class == type) {
                                    String time = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (time == null || time.length() == 0) {
                                        time = new Time(System.currentTimeMillis()).toString();
                                    }
                                    SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                    Date d2 = null;
                                    try {
                                        d2 = format2.parse(time);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Time startTimeFmt = new Time(d2.getTime());
                                    obj = startTimeFmt;
                                } else if (Date.class == type || java.sql.Date.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    try {
                                        Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                        obj = date;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else if (long.class == type || Long.class == type) {
                                    long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (short.class == type || Short.class == type) {
                                    short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (byte[].class.equals(type)) {
                                    byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (List.class == type || Map.class == type) {

                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (value == null || "null".equals(value)) {
                                        obj = null;
                                    } else {
                                        //第二次转换 List OR Map
                                        obj = returnListOrMap(type, value);
                                    }
                                } else {
                                    //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    String name = field.getName();//获取属性名
                                    Object val = null;
                                    try {
                                        val = field.get(bean);// 得到此属性的值
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        obj = JSON.fromJson(value, type);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            if (obj != null) {
                                try {
                                    field.set(bean, obj);
                                    continue;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    beanList.add(bean);//存入查询到的数据
                    cursor.moveToNext();//移动到下一位
                }
            }
            cursor.close();//释放资源

            orderByStr = "";
            limitStr = "";

            return beanList;
        }

        /**
         * @param tableNameClass 查询的表
         * @param returnValue    返回的字段
         * @param conditions     查询条件
         * @param values         查询条件值
         * @param <T>
         * @return
         * @查询多条数据
         */
        public <T> List<T> queryAll(Class<T> tableNameClass, Object returnValue, Object conditions, Object values) {

            if (tableNameClass == null || returnValue == null || conditions == null || values == null) {
                err(getLineInfo(2), "查询的数据为 null，操作失败");
                status = false;
                return null;
            }

            //监测条件
            String returnValueType = returnValue.getClass().getSimpleName();
            String conditionsType = conditions.getClass().getSimpleName();
            String valuesType = values.getClass().getSimpleName();

            if (!conditionsType.equals("String") && !conditionsType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 conditionsType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!valuesType.equals("String") && !valuesType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 valuesType 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            if (!returnValueType.equals("String") && !returnValueType.equals("String[]")) {
                err(getLineInfo(2), "删除的数据 returnValue 类型仅支持String 与 String[]，操作失败");
                status = false;
                return null;
            }

            //解析查询条件 conditions （String）
            String condition = "";//解析筛选条件
            if (conditionsType.equals("String[]")) {
                for (String str : (String[]) conditions) {
                    condition += (str + " = ? and ");
                }

                //去掉最后一个多余 and
                condition = condition.substring(0, condition.lastIndexOf("and"));
            } else if (conditionsType.equals("String")) {
                condition = (String) conditions;
            }

            //解析查询条件值
            String[] returnValues = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                returnValues = (String[]) returnValue;
            } else if (valuesType.equals("String")) {
                returnValues[0] = (String) returnValue;
            }

            //解析查询条件值
            String[] valuesArray = new String[1];//解析筛选值
            if (valuesType.equals("String[]")) {
                valuesArray = (String[]) values;
            } else if (valuesType.equals("String")) {
                valuesArray[0] = (String) values;
            }

            //获取表名
            String tableName = tableNameClass.getSimpleName();
            //判断这个表是否存在
            if (!isTable(tableName)) {
                err(getLineInfo(2), "查询的表不存在，操作失败");
                status = false;
                return null;
            }

            //反射生成对象并注入
            Cursor cursor = sqLiteDatabase2.query(tableName, returnValues, condition, valuesArray, null, null, orderByStr, limitStr);
            List<T> beanList = new ArrayList<T>();//创建容器
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();//移动到首位
                for (int i = 0; i < cursor.getCount(); i++) {

                    T bean = null;//定义泛型
                    try {
                        bean = tableNameClass.newInstance();//实体化
                    } catch (IllegalAccessException e) {
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        err(getLineInfo(2), "查询的实体类缺少无参构造，操作失败e：" + e);
                        status = false;
                        return null;
                    }


                    Field[] fields = bean.getClass().getDeclaredFields();

                    for (Field field : fields) {
                        String valueName = field.getName();
                        Class<?> type = field.getType();
                        field.setAccessible(true);

                        /**
                         * 思路：
                         * 将反射循环注入赋值放在这里进行操作
                         */
                        for (String str : (String[]) returnValues) {
                            Object obj = null;
                            if (str.equals(valueName)) {
                                if (String.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (int.class == type || Integer.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (boolean.class == type || Boolean.class == type) {
                                    int value = cursor.getInt(cursor.getColumnIndex(valueName));// false:0   true:1
                                    if (value == 1) {
                                        obj = true;
                                    } else if (value == 0) {
                                        obj = false;
                                    }
                                } else if (double.class == type || Double.class == type) {
                                    double value = cursor.getDouble(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (float.class == type || Float.class == type) {
                                    float value = cursor.getFloat(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (Time.class == type) {
                                    String time = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (time == null || time.length() == 0) {
                                        time = new Time(System.currentTimeMillis()).toString();
                                    }
                                    SimpleDateFormat format2 = new SimpleDateFormat("hh:mm:ss");// 格式化类型
                                    Date d2 = null;
                                    try {
                                        d2 = format2.parse(time);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Time startTimeFmt = new Time(d2.getTime());
                                    obj = startTimeFmt;
                                } else if (Date.class == type || java.sql.Date.class == type) {
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    try {
                                        Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(value);
                                        obj = date;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else if (long.class == type || Long.class == type) {
                                    long value = cursor.getLong(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (short.class == type || Short.class == type) {
                                    short value = cursor.getShort(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (byte[].class.equals(type)) {
                                    byte[] value = cursor.getBlob(cursor.getColumnIndex(valueName));
                                    obj = value;
                                } else if (List.class == type || Map.class == type) {

                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    if (value == null || "null".equals(value)) {
                                        obj = null;
                                    } else {
                                        //第二次转换 List OR Map
                                        obj = returnListOrMap(type, value);
                                    }
                                } else {
                                    //                                    err(getLineInfo(2), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                                    String value = cursor.getString(cursor.getColumnIndex(valueName));
                                    String name = field.getName();//获取属性名
                                    Object val = null;
                                    try {
                                        val = field.get(bean);// 得到此属性的值
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        obj = JSON.fromJson(value, type);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            if (obj != null) {
                                try {
                                    field.set(bean, obj);
                                    continue;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    beanList.add(bean);//存入查询到的数据
                    cursor.moveToNext();//移动到下一位
                }
            }
            cursor.close();//释放资源

            orderByStr = "";
            limitStr = "";

            return beanList;
        }

        //原生查询

        /**
         * @param sqlCode       执行SQL代码
         * @param selectionArgs 对应占位符值
         * @return
         * @原生查询
         */
        public Cursor query(String sqlCode, String[] selectionArgs) {
            return sqLiteDatabase2.rawQuery(sqlCode, selectionArgs);
        }

        //====================================== 第一步：设置数据库名称 ======================================

        /**
         * @param sqlName
         * @return
         * @初始化数据库名称
         */
        public Hibernate init_1_SqlName(String sqlName) {
            if (sqlName != null) {
                DATABASE_NAME = sqlName + ".db";
            }
            return this;
        }

        //====================================== 第二步：设置数据库版本 ======================================

        /**
         * @param sqlVersion
         * @return
         * @初始化数据库版本号
         */
        public Hibernate init_2_SqlVersion(int sqlVersion) {
            if (sqlVersion > 0) {
                DATABASE_VERSION = sqlVersion;
            }
            return this;
        }

        //====================================== 第三步：设置扫描表路径 ======================================

        /**
         * @return
         * @初始化扫描数据库实体类路径
         * @可输入的值如下：
         * @1.实体类的包路径 com.gsls.gtlibrary.enity
         * @2.实体类的class User.class
         * @3.实体类的 List/Set/Array ：List<Class<?>> 、 Set<Class<?>、 Class<?>[]
         */
        public Hibernate init_3_SqlTable(Object scanTable) {
            isCreateTable = true;
            tableNameList.clear();
            creationTableNameList.clear();
            updateTableValueList.clear();
            //判空
            if (scanTable == null)
                return this;

            //实例化 数据库表集合
            if (tableList == null) {
                tableList = new ArrayList<>();
            } else {
                tableList.clear();//清空
            }

            boolean isReflect = false;//是否需要反射得到 class
            //解析路径 或 引用赋值
            String EnityPackagePath = "";
            if (scanTable instanceof Class<?>) {//添加单个的表
                Class<?> classs = (Class<?>) scanTable;
                tableList.add(classs);//添加到数据库表的集合中
            } else if (scanTable instanceof String) {//添加该路径下所有表
                EnityPackagePath = scanTable.toString();//如果是路径直接赋值
                isReflect = true;
            } else if (scanTable instanceof List) {//添加该集合中所有表
                tableList = (List<Class<?>>) scanTable;//如果是class 集合直接引用
            } else if (scanTable instanceof Set) {//添加该集合中所有表
                Set<Class<?>> tableSet = (Set<Class<?>>) scanTable;//如果是 Set 转换到 List 去
                for (Class<?> classs : tableSet) {
                    tableList.add(classs);
                }
            } else if (scanTable instanceof Class<?>[]) {//添加该集合中所有表
                Class<?>[] classArray = (Class<?>[]) scanTable;//如果是 Array 转换到 List 去
                for (Class<?> classs : classArray) {
                    tableList.add(classs);
                }
            } else {
                errs("当前不支持 " + scanTable.getClass() + "  类型。");
                isCreateTable = false;
                return this;
            }

            //检测扫描路径
            if (isReflect) {//如果需要反射就进行反射得到 class
                if (!"".equals(EnityPackagePath)) {
                    loadHibernateAnnotation(EnityPackagePath, context);//加载数据库注解
                } else {
                    errs("当前扫描数据库实体类的路径有错误！请检查该路径。EnityPackagePath = " + EnityPackagePath);
                    isCreateTable = false;
                    return this;
                }
            }

            //进行 class 反射解析
            if (tableList != null && tableList.size() > 0) {
                analysisClassData();//解析 class
            }


            return this;
        }
        //====================================== 第三步：反射解析 class 信息自动生成 SQL代码 =================================

        /**
         * @反射解析 Class 数据
         * @return
         * @待完成
         */
        private static final List<String> creationTableNameList = new ArrayList<>();//实例化表名集合
        private static final List<String> tableNameList = new ArrayList<>();//实例化表名集合
        private static final List<String> updateTableValueList = new ArrayList<>();//实例化存储更新字段的集合

        private String tableSqlCode;//创建表的SQL语句
        private String sqlChar = "";//添加要创建表字段

        //解析 主键 与 字段 自动生成 SQL 语句
        private Hibernate analysisClassData() {

            boolean isKey = false;//是否存在多个主键

            //遍历所有集成的 SQL 语句
            for (int i = 0; i < tableList.size(); i++) {

                Class<?> aClass = tableList.get(i);//获取需要处理的 Class

                //存储需要更新的数据库字段
                saveUpdateData(aClass);

                //解析表名
                String tableName = aClass.getSimpleName();//获取表名
                tableNameList.add(tableName);//存储表名
                tableSqlCode = "CREATE TABLE " + tableName + "(";//创建表的SQL语句
                sqlChar = "";//初始化 SQL 字段

                //遍历所有成员变量
                for (Field field : aClass.getDeclaredFields()) {
                    Class<?> type = field.getType();//获取当前字段类型

                    //跳过标注不被持久化的字段
                    if (field.getAnnotation(GT_OnNullValue.class) != null) {
                        continue;
                    }

                    //解析主键
                    GT_Key initView = field.getAnnotation(GT_Key.class);
                    if (initView != null) {

                        //监测当前类中是主键是否合法，
                        if (!isKey) {
                            isKey = true;
                        } else {
                            err(getLineInfo(), "数据库报错:当前 " + tableName + "类 中主键不唯一");
                            isCreateTable = false;
                            return this;
                        }

                        //若需要兼容其他字段可直接在这添加，不会对其他代码有影响
                        String KeyType = "";
                        if (String.class == type) {
                            KeyType = "varchar(200)";
                        } else if (int.class == type || Integer.class == type) {
                            KeyType = "integer";
                        } else if (boolean.class == type || Boolean.class == type) {
                            KeyType = "BOOLEAN";
                        } else if (double.class == type || Double.class == type) {
                            KeyType = "DOUBLE";
                        } else if (float.class == type || Float.class == type) {
                            KeyType = "FLOAT";
                        } else if (Time.class == type) {
                            KeyType = "TIME";
                        } else if (Date.class == type || java.sql.Date.class == type) {
                            KeyType = "DATE";
                        } else if (long.class == type || Long.class == type) {
                            //                            KeyType = "varchar(20)";
                            KeyType = "LONG";
                        } else if (short.class == type || Short.class == type) {
                            KeyType = "varchar(6)";
                        } else if (byte[].class.equals(type)) {
                            KeyType = "BLOB";
                        } else if (List.class == type || Map.class == type) {
                            KeyType = "TEXT";
                        } else {
                            err(getLineInfo(), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                            isCreateTable = false;
                            return this;
                        }

                        boolean autoincrement = initView.autoincrement();//获取 主键类型 注解值
                        if (autoincrement) {//自动增长
                            if ("integer".equals(KeyType) || "LONG".equals(KeyType)) {
                                tableSqlCode += field.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
                            } else {
                                err(getLineInfo(), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 不是int类型，暂不支持自增。");
                                isCreateTable = false;
                                return this;
                            }
                        } else {//手动增长
                            if ("integer".equals(KeyType)) {//如果是整数型就需要转换一下
                                KeyType = "INT";
                            }
                            tableSqlCode += field.getName() + " " + KeyType + " PRIMARY KEY NOT NULL";
                        }
                        continue;//跳过字段的解析
                    }


                    //表字段解析
                    String tableStrName = field.getName();
                    //若需要兼容其他字段可直接在这添加，不会对其他代码有影响
                    if (String.class == type) {
                        sqlChar += (", " + tableStrName + " VARCHAR(200)");
                    } else if (int.class == type) {
                        sqlChar += (", " + tableStrName + " INT");
                    } else if (Integer.class == type) {
                        sqlChar += (", " + tableStrName + " INTEGER");
                    } else if (boolean.class == type || Boolean.class == type) {
                        sqlChar += (", " + tableStrName + " BOOLEAN");
                    } else if (double.class == type || Double.class == type) {
                        sqlChar += (", " + tableStrName + " DOUBLE");
                    } else if (float.class == type || Float.class == type) {
                        sqlChar += (", " + tableStrName + " FLOAT");
                    } else if (Time.class == type) {
                        sqlChar += (", " + tableStrName + " TIME");
                    } else if (Date.class == type || java.sql.Date.class == type) {
                        sqlChar += (", " + tableStrName + " DATE");
                    } else if (long.class == type || Long.class == type) {
                        sqlChar += (", " + tableStrName + "  VARCHAR(20)");
                    } else if (short.class == type || Short.class == type) {
                        sqlChar += (", " + tableStrName + "  VARCHAR(6)");
                    } else if (byte[].class.equals(type)) {
                        sqlChar += (", " + tableStrName + " BLOB");
                    } else if (List.class == type || Map.class == type) {
                        sqlChar += (", " + tableStrName + " TEXT");
                    } else {
                        //                        err(getLineInfo(), "解析的表文件 [" + tableName + "] 类中的字段 [" + field + "] 出现不支持类型。");
                        //                        isCreateTable = false;
                        sqlChar += (", " + tableStrName + " TEXT");
                        //                        return this;
                    }

                }

                if (!isKey) {
                    //自行为该表添加一个主键
                    tableSqlCode += "GT_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
                }

                creationTableNameList.add(tableSqlCode + sqlChar + ")");//添加在创建表时需要执行的
                isKey = false;//初始化注解标识
            }
            return this;
        }

        /**
         * @param mClass
         * @存储需要更新的数据
         */
        private void saveUpdateData(Class<?> mClass) {
            String simpleName = mClass.getSimpleName();//获取表名
            Field[] fields = mClass.getDeclaredFields();//获取所有成员变量
            String oldStr = "";//存储旧字段
            String newStr = "";//存储新字段
            for (Field field : fields) {
                String newTableName = field.getName();//获取新字段名
                GT_DatabaseField initView = field.getAnnotation(GT_DatabaseField.class);
                if (initView != null) {
                    String oldTableName = initView.oldTableValue();//获取注解值
                    if (oldTableName != null && !"".equals(oldTableName)) {
                        //过滤掉 字段值 与 注解值 一样的问题
                        if (newTableName.equals(oldTableName))
                            continue;
                        oldStr += oldTableName + " ";
                        newStr += newTableName + " ";
                    }
                }
            }
            if (oldStr != null && newStr != null && !"".equals(oldStr) && !"".equals(newStr)) {
                updateTableValueList.add(simpleName + "{" + oldStr + "," + newStr + "}");
            }
        }

        //====================================== 第四步：创建数据库对象 ======================================
        public static boolean isCreateTable = true;//是否创建数据库

        public Hibernate init_4_Sql() {
            if (isCreateTable) {//是否执行创建数据库代码
                DatabaseHelper databaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
                try {
                    sqLiteDatabase2 = databaseHelper.getWritableDatabase();
                    deleteUnnecessaryTable();//删除掉多余的表
                } catch (RuntimeException e) {
                    errs(getLineInfo() + "数据库报错:" + e);
                }
            }


            return this;
        }

        /**
         * 删除掉多余的表,如系统自己创建的表
         */
        private void deleteUnnecessaryTable() {
            //删除系统创建的多余表
            List<String> okTableNameList = new ArrayList<>();
            for (Class classz : tableList) {
                okTableNameList.add(classz.getSimpleName());
            }

            for (String tableName : getSQLAllTableName()) {
                if (!okTableNameList.contains(tableName)) {//判断当前创建的表是否为系统创建的表
                    if (isTable(tableName)) {
                        deleteTable(tableName).isStatus();//删除表
                    }
                }
            }
            okTableNameList.clear();
            okTableNameList = null;
        }

        //====================================== 加载 包名扫描 SQL 注解 ======================================
        private void loadHibernateAnnotation(String EnityPackagePath, Context context) {
            DexFile dexFile = null;
            try {
                dexFile = new DexFile(context.getPackageCodePath());
            } catch (IOException e) {
                e.printStackTrace();
                isCreateTable = false;
            }
            Enumeration<String> enumeration = dexFile.entries();
            while (enumeration.hasMoreElements()) {
                String className = enumeration.nextElement();
                if (className.contains(EnityPackagePath)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    Class<?> clazz1 = null;
                    try {
                        clazz1 = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    //过滤掉未被注解过的类
                    Annotation BeanSubAnnotation = clazz1.getAnnotation(GT_Bean.class);    //判断是否被 Bean 注解过
                    Annotation EntitySubAnnotation = clazz1.getAnnotation(GT_Entity.class);    //判断是否被 Entity 注解过

                    if (BeanSubAnnotation != null || EntitySubAnnotation != null) {
                        tableList.add(clazz1);
                    }/*else{
                        log("过滤掉 " + clazz1 + " 没有被注解的类");
                    }*/

                }
            }
        }

        //数据库类结束
    }

    /**
     * 管理手机数据
     */
    public static class CleanDataUtils {

        /**
         * 需要查下缓存大小
         *
         * @param context
         * @return
         */
        public static String getTotalCacheSize(Context context) throws Exception {
            long cacheSize = getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += getFolderSize(context.getExternalCacheDir());
            }
            return getFormatSize(cacheSize);
        }

        /**
         * 清空缓存
         *
         * @param context
         */
        public static void clearAllCache(Context context) {
            deleteDir(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                deleteDir(context.getExternalCacheDir());
            }

        }

        /**
         * 删除 dir 文件
         *
         * @param dir
         * @return
         */
        private static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }

        /**
         * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
         *
         * @param context
         */
        public static void cleanInternalCache(Context context) {
            deleteFilesByDirectory(context.getCacheDir());
        }

        /**
         * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
         *
         * @param context
         */
        public static void cleanDatabases(Context context) {
            deleteFilesByDirectory(new File("/data/data/"
                    + context.getPackageName() + "/databases"));
        }

        /**
         * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
         *
         * @param context
         */
        public static void cleanSharedPreference(Context context) {
            deleteFilesByDirectory(new File("/data/data/"
                    + context.getPackageName() + "/shared_prefs"));
        }

        /**
         * 按名字清除本应用数据库 * *
         *
         * @param context
         * @param dbName
         */
        public static void cleanDatabaseByName(Context context, String dbName) {
            context.deleteDatabase(dbName);
        }

        /**
         * 清除/data/data/com.xxx.xxx/files下的内容 * *
         *
         * @param context
         */
        public static void cleanFiles(Context context) {
            deleteFilesByDirectory(context.getFilesDir());
        }

        /**
         * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
         *
         * @param context
         */
        public static void cleanExternalCache(Context context) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                deleteFilesByDirectory(context.getExternalCacheDir());
            }
        }

        /**
         * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
         *
         * @param filePath
         */
        public static void cleanCustomCache(String filePath) {
            deleteFilesByDirectory(new File(filePath));
        }

        /**
         * 清除本应用所有的数据 * *
         *
         * @param context
         * @param filepath
         */
        public static void cleanApplicationData(Context context, String... filepath) {
            cleanInternalCache(context);
            cleanExternalCache(context);
            cleanDatabases(context);
            cleanSharedPreference(context);
            cleanFiles(context);
            if (filepath == null) {
                return;
            }
            for (String filePath : filepath) {
                cleanCustomCache(filePath);
            }
        }

        /**
         * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
         *
         * @param directory
         */
        private static void deleteFilesByDirectory(File directory) {
            if (directory != null && directory.exists() && directory.isDirectory()) {
                for (File item : directory.listFiles()) {
                    item.delete();
                }
            }
        }

        // 获取文件
        //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
        //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
        public static long getFolderSize(File file) throws Exception {
            long size = 0;
            try {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        /**
         * 删除指定目录下文件及目录
         *
         * @param deleteThisPath
         * @param filePath
         * @return
         */
        public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
            if (!TextUtils.isEmpty(filePath)) {
                try {
                    File file = new File(filePath);
                    if (file.isDirectory()) {// 如果下面还有文件
                        File files[] = file.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            deleteFolderFile(files[i].getAbsolutePath(), true);
                        }
                    }
                    if (deleteThisPath) {
                        if (!file.isDirectory()) {// 如果是文件，删除
                            file.delete();
                        } else {// 目录
                            if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                                file.delete();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 格式化单位
         *
         * @param size
         * @return
         */
        public static String getFormatSize(double size) {
            double kiloByte = size / 1024;
            //        if (kiloByte < 1) {
            //            return size + "Byte";
            //        }

            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "K";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "M";
            }

            double teraBytes = gigaByte / 1024;
            if (teraBytes < 1) {
                BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "G";
            }
            BigDecimal result4 = new BigDecimal(teraBytes);
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                    + "T";
        }

        public static String getCacheSize(File file) throws Exception {
            return getFormatSize(getFolderSize(file));
        }

    }

    //=========================================== 网络类 =========================================

    /**
     * network 网络类
     */
    public static class Network {

        /**
         * 监听网络状态 true 网络正常  false 网络异常
         *
         * @param context 上下文
         * @return boolean  true 为当前网络正常    false 则反之
         */
        @SuppressLint("MissingPermission")
        public static boolean netWorkStatus(Context context) {
            ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cwjManager.getActiveNetworkInfo() != null) {
                return cwjManager.getActiveNetworkInfo().isAvailable();
            }
            return false;
        }

        /**
         * 获取手机 IP 地址
         *
         * @param context 上下文
         * @return String   返回当前 ip 地址
         */
        public static String getIPAddress(Context context) {
            @SuppressLint("MissingPermission") NetworkInfo info = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                    try {
                        //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }


                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    int ipAddress1 = wifiInfo.getIpAddress();
                    String ipAddress = (ipAddress1 & 0xFF) + "." +
                            ((ipAddress1 >> 8) & 0xFF) + "." +
                            ((ipAddress1 >> 16) & 0xFF) + "." +
                            (ipAddress1 >> 24 & 0xFF);
                    return ipAddress;
                }
            } else {
                //当前无网络连接,请在设置中打开网络
                GT.toast_s("当前无网络");
            }
            return null;
        }

        //检测当前手机是否可上网
        public static boolean isInternet(Context context) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = manager.getActiveNetworkInfo();// 检查网络连接，如果无网络可用，就不需要进行连网操作等
            if (info == null || !manager.getBackgroundDataSetting()) {
                return false;
            }
            return true;
        }

        /**
         * 获取当前网络属于 无网络(返回0)、WF(返回1)、2G(返回2)、3G(返回3)、4G(返回4) 网络
         *
         * @param context
         * @return
         */
        public static int getNetworkState(Context context) {
            return IntenetUtil.getNetworkState(context);
        }

        /**
         * 获取当前网络属于 无网络、WF、2G、3G、4G网络
         * //没有网络连接
         * public static final int NETWORN_NONE = 0;
         * //wifi连接
         * public static final int NETWORN_WIFI = 1;
         * //手机网络数据连接类型
         * public static final int NETWORN_2G = 2;
         * public static final int NETWORN_3G = 3;
         * public static final int NETWORN_4G = 4;
         * public static final int NETWORN_MOBILE = 5;
         */
        private static class IntenetUtil {

            //没有网络连接
            public static final int NETWORN_NONE = 0;
            //wifi连接
            public static final int NETWORN_WIFI = 1;
            //手机网络数据连接类型
            public static final int NETWORN_2G = 2;
            public static final int NETWORN_3G = 3;
            public static final int NETWORN_4G = 4;
            public static final int NETWORN_MOBILE = 5;

            /**
             * 获取当前网络连接类型
             *
             * @param context
             * @return
             */
            public static int getNetworkState(Context context) {
                //获取系统的网络服务
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //如果当前没有网络
                if (null == connManager)
                    return NETWORN_NONE;
                //获取当前网络类型，如果为空，返回无网络
                @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
                if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
                    return NETWORN_NONE;
                }
                // 判断是不是连接的是不是wifi
                @SuppressLint("MissingPermission") NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (null != wifiInfo) {
                    NetworkInfo.State state = wifiInfo.getState();
                    if (null != state)
                        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                            return NETWORN_WIFI;
                        }
                }
                // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
                @SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (null != networkInfo) {
                    NetworkInfo.State state = networkInfo.getState();
                    String strSubTypeName = networkInfo.getSubtypeName();
                    if (null != state)
                        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                            switch (activeNetInfo.getSubtype()) {
                                //如果是2g类型
                                case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                                case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                                case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    return NETWORN_2G;
                                //如果是3g类型
                                case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                case TelephonyManager.NETWORK_TYPE_EHRPD:
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    return NETWORN_3G;
                                //如果是4g类型
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    return NETWORN_4G;
                                default:
                                    //中国移动 联通 电信 三种3G制式
                                    if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                        return NETWORN_3G;
                                    } else {
                                        return NETWORN_MOBILE;
                                    }
                            }
                        }
                }
                return NETWORN_NONE;
            }
        }

    }

    /**
     * JSON 接口解析 json 与 Bean 互转
     */
    public static class JSON {

        /**
         * 将对象转换成Json字符串
         *
         * @param obj
         * @return json类型字符串
         */
        public static String toJson(Object obj) {
            JSONStringer js = new JSONStringer();
            serialize(js, obj);
            return js.toString();
        }

        /**
         * 序列化为JSON
         *
         * @param js json对象
         * @param o  待需序列化的对象
         */
        private static void serialize(JSONStringer js, Object o) {
            if (isNull(o)) {
                try {
                    js.value(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

            Class<?> clazz = o.getClass();
            if (isObject(clazz)) { // 对象
                serializeObject(js, o);
            } else if (isArray(clazz)) { // 数组
                serializeArray(js, o);
            } else if (isCollection(clazz)) { // 集合
                Collection<?> collection = (Collection<?>) o;
                serializeCollect(js, collection);
            } else if (isMap(clazz)) { // 集合
                HashMap<?, ?> collection = (HashMap<?, ?>) o;
                serializeMap(js, collection);
            } else { // 单个值
                try {
                    js.value(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 序列化数组
         *
         * @param js    json对象
         * @param array 数组
         */
        private static void serializeArray(JSONStringer js, Object array) {
            try {
                js.array();
                for (int i = 0; i < Array.getLength(array); ++i) {
                    Object o = Array.get(array, i);
                    serialize(js, o);
                }
                js.endArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 序列化集合
         *
         * @param js         json对象
         * @param collection 集合
         */
        private static void serializeCollect(JSONStringer js, Collection<?> collection) {
            try {
                js.array();
                for (Object o : collection) {
                    serialize(js, o);
                }
                js.endArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 序列化Map
         *
         * @param js  json对象
         * @param map map对象
         */
        private static void serializeMap(JSONStringer js, Map<?, ?> map) {
            try {
                js.object();
                Map<?, Object> valueMap = (Map<?, Object>) map;
                Iterator<? extends Map.Entry<?, Object>> it = valueMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<?, Object> entry = (Map.Entry<?, Object>) it.next();
                    js.key(entry.getKey().toString());
                    serialize(js, entry.getValue());
                }
                js.endObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 序列化对象
         *
         * @param js  json对象
         * @param obj 待序列化对象
         */
        private static void serializeObject(JSONStringer js, Object obj) {
            try {
                js.object();
                Class<? extends Object> objClazz = obj.getClass();
                //获取所有的数组
                Method[] methods = objClazz.getDeclaredMethods();
                //获取所有的字段
                Field[] fields = objClazz.getDeclaredFields();
                //遍历这个对象
                for (Field field : fields) {
                    try {
                        String fieldType = field.getType().getSimpleName();
                        String fieldGetName = parseMethodName(field.getName(), "get");
                        if (!haveMethod(methods, fieldGetName)) {
                            continue;
                        }
                        Method fieldGetMet = objClazz.getMethod(fieldGetName, new Class[]{});
                        Object fieldVal = fieldGetMet.invoke(obj, new Object[]{});
                        Object result = null;
                        if ("Date".equals(fieldType)) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                            result = sdf.format((Date) fieldVal);

                        } else {
                            if (null != fieldVal) {
                                result = fieldVal;
                            }
                        }
                        js.key(field.getName());
                        serialize(js, result);
                    } catch (Exception e) {
                        continue;
                    }
                }
                js.endObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断是否存在某属性的 get方法
         *
         * @param methods     引用方法的数组
         * @param fieldMethod 方法名称
         * @return true或者false
         */
        public static boolean haveMethod(Method[] methods, String fieldMethod) {
            for (Method met : methods) {
                if (fieldMethod.equals(met.getName())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 拼接某属性的 get或者set方法
         *
         * @param fieldName  字段名称
         * @param methodType 方法类型
         * @return 方法名称
         */
        public static String parseMethodName(String fieldName, String methodType) {
            if (null == fieldName || "".equals(fieldName)) {
                return null;
            }
            return methodType + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }

        /**
         * 给字段赋值
         *
         * @param obj    实例对象
         * @param valMap 值集合
         */
        public static void setFieldValue(Object obj, Map<String, String> valMap) {
            Class<?> cls = obj.getClass();
            // 取出bean里的所有方法
            Method[] methods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                try {
                    String setMetodName = parseMethodName(field.getName(), "set");
                    if (!haveMethod(methods, setMetodName)) {
                        continue;
                    }
                    Method fieldMethod = cls.getMethod(setMetodName, field
                            .getType());
                    String value = valMap.get(field.getName());
                    if (null != value && !"".equals(value)) {
                        String fieldType = field.getType().getSimpleName();
                        if ("String".equals(fieldType)) {
                            fieldMethod.invoke(obj, value);
                        } else if ("Date".equals(fieldType)) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                            Date temp = sdf.parse(value);
                            fieldMethod.invoke(obj, temp);
                        } else if ("Integer".equals(fieldType)
                                || "int".equals(fieldType)) {
                            Integer intval = Integer.parseInt(value);
                            fieldMethod.invoke(obj, intval);
                        } else if ("Long".equalsIgnoreCase(fieldType)) {
                            Long temp = Long.parseLong(value);
                            fieldMethod.invoke(obj, temp);
                        } else if ("Double".equalsIgnoreCase(fieldType)) {
                            Double temp = Double.parseDouble(value);
                            fieldMethod.invoke(obj, temp);
                        } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                            Boolean temp = Boolean.parseBoolean(value);
                            fieldMethod.invoke(obj, temp);
                        } else {
                            System.out.println("setFieldValue not supper type:" + fieldType);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

        }

        /**
         * bean对象转Map
         *
         * @param obj 实例对象
         * @return map集合
         */
        public static Map<String, String> beanToMap(Object obj) {
            Class<?> cls = obj.getClass();
            Map<String, String> valueMap = new HashMap<String, String>();
            // 取出bean里的所有方法
            Method[] methods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                try {
                    String fieldType = field.getType().getSimpleName();
                    String fieldGetName = parseMethodName(field.getName(), "get");
                    if (!haveMethod(methods, fieldGetName)) {
                        continue;
                    }
                    Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                    Object fieldVal = fieldGetMet.invoke(obj, new Object[]{});
                    String result = null;
                    if ("Date".equals(fieldType)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        result = sdf.format((Date) fieldVal);

                    } else {
                        if (null != fieldVal) {
                            result = String.valueOf(fieldVal);
                        }
                    }
                    valueMap.put(field.getName(), result);
                } catch (Exception e) {
                    continue;
                }
            }
            return valueMap;

        }

        /**
         * 给对象的字段赋值
         *
         * @param obj            类实例
         * @param fieldSetMethod 字段方法
         * @param fieldType      字段类型
         * @param value
         */
        public static void setFiedlValue(Object obj, Method fieldSetMethod, String fieldType, Object value) {

            try {
                if (null != value && !"".equals(value)) {
                    if ("String".equals(fieldType)) {
                        fieldSetMethod.invoke(obj, value.toString());
                    } else if ("Date".equals(fieldType)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                        Date temp = sdf.parse(value.toString());
                        fieldSetMethod.invoke(obj, temp);
                    } else if ("Integer".equals(fieldType)
                            || "int".equals(fieldType)) {
                        Integer intval = Integer.parseInt(value.toString());
                        fieldSetMethod.invoke(obj, intval);
                    } else if ("Long".equalsIgnoreCase(fieldType)) {
                        Long temp = Long.parseLong(value.toString());
                        fieldSetMethod.invoke(obj, temp);
                    } else if ("Double".equalsIgnoreCase(fieldType)) {
                        Double temp = Double.parseDouble(value.toString());
                        fieldSetMethod.invoke(obj, temp);
                    } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                        Boolean temp = Boolean.parseBoolean(value.toString());
                        fieldSetMethod.invoke(obj, temp);
                    } else if ("Map".equals(fieldType)) {

                        Map map = fromJson(value.toString(), Map.class);


                        fieldSetMethod.invoke(obj, map);
                    } else if ("List".equals(fieldType)) {
                        fieldSetMethod.invoke(obj, fromJson(value.toString(), List.class));
                    } else if ("Set".equals(fieldType)) {
                        fieldSetMethod.invoke(obj, fromJson(value.toString(), Set.class));
                    } else {
                        fieldSetMethod.invoke(obj, value);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * 将Map中存在JSON的数据转为 实体类
         *
         * @param mapJSON
         * @param tClass
         * @param <T>
         * @return
         */
        public static <T> Map<?, T> mapJSONToMapObj(Map<?, ?> mapJSON, Class<T> tClass) {
            Map<Object, T> map = null;
            try {
                map = fromJson(mapJSON.toString(), Map.class);
                for (Object key : map.keySet()) {
                    T o = map.get(key);
                    T t = fromJson(o.toString(), tClass);
                    map.put(key, t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return map;
        }

        /**
         * 反序列化简单对象
         *
         * @param jo    json对象
         * @param clazz 实体类类型
         * @return 反序列化后的实例
         * @throws JSONException
         */
        public static <T> T fromJson(JSONObject jo, Class<T> clazz) throws JSONException {
            if (clazz == null || isNull(jo)) {
                return null;
            }

            T t = newInstance(clazz);
            if (t == null) {
                return null;
            }
            if (isMap(clazz)) {
                setField(t, jo);
            } else {
                // 取出bean里的所有方法
                Method[] methods = clazz.getDeclaredMethods();
                Field[] fields = clazz.getDeclaredFields();
                for (Field f : fields) {
                    String setMetodName = parseMethodName(f.getName(), "set");
                    if (!haveMethod(methods, setMetodName)) {
                        continue;
                    }
                    try {
                        Method fieldMethod = clazz.getMethod(setMetodName, f.getType());
                        setField(t, fieldMethod, f, jo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return t;
        }

        /**
         * 反序列化简单对象
         *
         * @param jsonStr json字符串
         * @param clazz   实体类类型
         * @return 反序列化后的实例
         * @throws JSONException
         */
        public static <T> T fromJson(String jsonStr, Class<T> clazz) throws JSONException {
            if (clazz == null || jsonStr == null || jsonStr.length() == 0) {
                return null;
            }

            JSONObject jo = new JSONObject(jsonStr);
            if (isNull(jo)) {
                return null;
            }

            return fromJson(jo, clazz);
        }

        /**
         * 反序列化数组对象
         *
         * @param ja    json数组
         * @param clazz 实体类类型
         * @return 反序列化后的数组
         */
        public static <T> T[] fromJsonArray(JSONArray ja, Class<T> clazz) {
            if (clazz == null || isNull(ja)) {
                return null;
            }

            int len = ja.length();

            @SuppressWarnings("unchecked")
            T[] array = (T[]) Array.newInstance(clazz, len);

            for (int i = 0; i < len; ++i) {
                try {
                    JSONObject jo = ja.getJSONObject(i);
                    T o = fromJson(jo, clazz);
                    array[i] = o;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return array;
        }


        /**
         * 反序列化数组对象
         *
         * @param jsonStr json字符串
         * @param clazz   实体类类型
         * @return 序列化后的数组
         */
        public static <T> T[] fromJsonArray(String jsonStr, Class<T> clazz) {
            if (clazz == null || jsonStr == null || jsonStr.length() == 0) {
                return null;
            }
            JSONArray jo = null;
            try {
                jo = new JSONArray(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isNull(jo)) {
                return null;
            }

            return fromJsonArray(jo, clazz);
        }

        /**
         * 反序列化泛型集合
         *
         * @param ja              json数组
         * @param collectionClazz 集合类型
         * @param genericType     实体类类型
         * @return
         * @throws JSONException
         */
        @SuppressWarnings("unchecked")
        public static <T> Collection<T> parseCollection(JSONArray ja, Class<?> collectionClazz,
                                                        Class<T> genericType) throws JSONException {

            if (collectionClazz == null || genericType == null || isNull(ja)) {
                return null;
            }

            Collection<T> collection = (Collection<T>) newInstance(collectionClazz);

            for (int i = 0; i < ja.length(); ++i) {
                try {
                    JSONObject jo = ja.getJSONObject(i);
                    T o = fromJson(jo, genericType);
                    collection.add(o);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return collection;
        }

        /**
         * 反序列化泛型集合
         *
         * @param jsonStr         json字符串
         * @param collectionClazz 集合类型
         * @param genericType     实体类类型
         * @return 反序列化后的数组
         * @throws JSONException
         */
        public static <T> Collection<T> parseCollection(String jsonStr, Class<?> collectionClazz,
                                                        Class<T> genericType) throws JSONException {
            if (collectionClazz == null || genericType == null || jsonStr == null
                    || jsonStr.length() == 0) {
                return null;
            }
            JSONArray jo = null;
            try {
                //如果为数组，则此处转化时，需要去掉前面的键，直接后面的[]中的值
                int index = jsonStr.indexOf("[");
                String arrayString = null;

                //获取数组的字符串
                if (-1 != index) {
                    arrayString = jsonStr.substring(index);
                }

                //如果为数组，使用数组转化
                if (null != arrayString) {
                    jo = new JSONArray(arrayString);
                } else {
                    jo = new JSONArray(jsonStr);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isNull(jo)) {
                return null;
            }

            return parseCollection(jo, collectionClazz, genericType);
        }

        /**
         * 根据类型创建对象
         *
         * @param clazz 待创建实例的类型
         * @return 实例对象
         * @throws JSONException
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static <T> T newInstance(Class<T> clazz) throws JSONException {
            if (clazz == null)
                return null;
            T obj = null;
            if (clazz.isInterface()) {
                if (clazz.equals(Map.class)) {
                    obj = (T) new HashMap();
                } else if (clazz.equals(List.class)) {
                    obj = (T) new ArrayList();
                } else if (clazz.equals(Set.class)) {
                    obj = (T) new HashSet();
                } else {
                    throw new JSONException("unknown interface: " + clazz);
                }
            } else {
                try {
                    obj = clazz.newInstance();//实体化
                } catch (IllegalAccessException e) {
                    return null;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return obj;
        }

        /**
         * 设定Map的值
         *
         * @param obj 待赋值字段的对象
         * @param jo  json实例
         */
        private static void setField(Object obj, JSONObject jo) {
            try {
                Iterator<String> keyIter = jo.keys();
                String key;
                Object value;
                @SuppressWarnings("unchecked")
                Map<String, Object> valueMap = (Map<String, Object>) obj;
                while (keyIter.hasNext()) {
                    key = (String) keyIter.next();
                    value = jo.get(key);
                    valueMap.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 设定字段的值
         *
         * @param obj            待赋值字段的对象
         * @param fieldSetMethod 字段方法名
         * @param field          字段
         * @param jo             json实例
         */
        private static void setField(Object obj, Method fieldSetMethod, Field field, JSONObject jo) {

            String name = field.getName();
            Class<?> clazz = field.getType();

            try {
                if (isArray(clazz)) { // 数组
                    Class<?> c = clazz.getComponentType();
                    JSONArray ja = jo.optJSONArray(name);
                    if (!isNull(ja)) {
                        Object array = fromJsonArray(ja, c);
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), array);
                    }
                } else if (isCollection(clazz)) { // 泛型集合
                    // 获取定义的泛型类型
                    Class<?> c = null;
                    Type gType = field.getGenericType();
                    if (gType instanceof ParameterizedType) {
                        ParameterizedType ptype = (ParameterizedType) gType;
                        Type[] targs = ptype.getActualTypeArguments();
                        if (targs.length > 0) {
                            Type t = targs[0];
                            c = (Class<?>) t;
                        }
                    }

                    JSONArray ja = jo.optJSONArray(name);
                    if (!isNull(ja)) {
                        Object o = parseCollection(ja, clazz, c);
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
                    }
                } else if (isSingle(clazz)) { // 值类型
                    Object o = jo.opt(name);
                    if (o != null) {
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
                    }
                } else if (isObject(clazz)) { // 对象
                    JSONObject j = jo.optJSONObject(name);
                    if (!isNull(j)) {
                        Object o = fromJson(j, clazz);
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
                    }
                } else if (isList(clazz) || isMap(clazz) || isSet(clazz)) { // list 或 Map 或 Set
                    JSONObject j = jo.optJSONObject(name);
                    if (!isNull(j)) {
                        Object o = fromJson(j, clazz);
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
                    }
                } else {
                    //或者位置类型
                    JSONObject j = jo.optJSONObject(name);
                    if (!isNull(j)) {
                        Object o = fromJson(j, clazz);
                        setFiedlValue(obj, fieldSetMethod, clazz.getSimpleName(), o);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 设定字段的值
         *
         * @param obj   待赋值字段的对象
         * @param field 字段
         * @param jo    json实例
         */
        @SuppressWarnings("unused")
        private static void setField(Object obj, Field field, JSONObject jo) {
            String name = field.getName();
            Class<?> clazz = field.getType();
            try {
                if (isArray(clazz)) { // 数组
                    Class<?> c = clazz.getComponentType();
                    JSONArray ja = jo.optJSONArray(name);
                    if (!isNull(ja)) {
                        Object array = fromJsonArray(ja, c);
                        field.set(obj, array);
                    }
                } else if (isCollection(clazz)) { // 泛型集合
                    // 获取定义的泛型类型
                    Class<?> c = null;
                    Type gType = field.getGenericType();
                    if (gType instanceof ParameterizedType) {
                        ParameterizedType ptype = (ParameterizedType) gType;
                        Type[] targs = ptype.getActualTypeArguments();
                        if (targs != null && targs.length > 0) {
                            Type t = targs[0];
                            c = (Class<?>) t;
                        }
                    }
                    JSONArray ja = jo.optJSONArray(name);
                    if (!isNull(ja)) {
                        Object o = parseCollection(ja, clazz, c);
                        field.set(obj, o);
                    }
                } else if (isSingle(clazz)) { // 值类型
                    Object o = jo.opt(name);
                    if (o != null) {
                        field.set(obj, o);
                    }
                } else if (isObject(clazz)) { // 对象
                    JSONObject j = jo.optJSONObject(name);
                    if (!isNull(j)) {
                        Object o = fromJson(j, clazz);
                        field.set(obj, o);
                    }
                } else if (isList(clazz)) { // 列表
                    JSONObject j = jo.optJSONObject(name);
                    if (!isNull(j)) {
                        Object o = fromJson(j, clazz);
                        field.set(obj, o);
                    }
                } else {
                    throw new Exception("unknow type!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断对象是否为空
         *
         * @param obj 实例
         * @return
         */
        private static boolean isNull(Object obj) {
            if (obj instanceof JSONObject) {
                return JSONObject.NULL.equals(obj);
            }
            return obj == null;
        }

        /**
         * 判断是否是值类型
         *
         * @param clazz
         * @return
         */
        private static boolean isSingle(Class<?> clazz) {
            return isBoolean(clazz) || isNumber(clazz) || isString(clazz);
        }

        /**
         * 是否布尔值
         *
         * @param clazz
         * @return
         */
        public static boolean isBoolean(Class<?> clazz) {
            return (clazz != null)
                    && ((Boolean.TYPE.isAssignableFrom(clazz)) || (Boolean.class
                    .isAssignableFrom(clazz)));
        }

        /**
         * 是否数值
         *
         * @param clazz
         * @return
         */
        public static boolean isNumber(Class<?> clazz) {
            return (clazz != null)
                    && ((Byte.TYPE.isAssignableFrom(clazz)) || (Short.TYPE.isAssignableFrom(clazz))
                    || (Integer.TYPE.isAssignableFrom(clazz))
                    || (Long.TYPE.isAssignableFrom(clazz))
                    || (Float.TYPE.isAssignableFrom(clazz))
                    || (Double.TYPE.isAssignableFrom(clazz)) || (Number.class
                    .isAssignableFrom(clazz)));
        }

        /**
         * 判断是否是字符串
         *
         * @param clazz
         * @return
         */
        public static boolean isString(Class<?> clazz) {
            return (clazz != null)
                    && ((String.class.isAssignableFrom(clazz))
                    || (Character.TYPE.isAssignableFrom(clazz)) || (Character.class
                    .isAssignableFrom(clazz)));
        }

        /**
         * 判断是否是对象
         *
         * @param clazz
         * @return
         */
        private static boolean isObject(Class<?> clazz) {
            return clazz != null && !isSingle(clazz) && !isArray(clazz) && !isCollection(clazz) && !isMap(clazz);
        }

        /**
         * 判断是否是数组
         *
         * @param clazz
         * @return
         */
        public static boolean isArray(Class<?> clazz) {
            return clazz != null && clazz.isArray();
        }

        /**
         * 判断是否是集合
         *
         * @param clazz
         * @return
         */
        public static boolean isCollection(Class<?> clazz) {
            return clazz != null && Collection.class.isAssignableFrom(clazz);
        }

        /**
         * 判断是否是Map
         *
         * @param clazz
         * @return
         */
        public static boolean isMap(Class<?> clazz) {
            return clazz != null && Map.class.isAssignableFrom(clazz);
        }

        /**
         * 判断是否是列表
         *
         * @param clazz
         * @return
         */
        public static boolean isList(Class<?> clazz) {
            return clazz != null && List.class.isAssignableFrom(clazz);
        }

        /**
         * 判断是否是列表
         *
         * @param clazz
         * @return
         */
        public static boolean isSet(Class<?> clazz) {
            return clazz != null && Set.class.isAssignableFrom(clazz);
        }

    }


    //============================================= 小工具类 =======================================

    /**
     * data 日期类
     */
    public static class GT_Date {

        /**
         * 使用案例
         * long currentTimeMillis = System.currentTimeMillis();
         * <p>
         * 输出：输入的时间：1567233280386
         * System.out.println("输入的时间：" + currentTimeMillis);
         * <p>
         * currentTimeMillis = 1538364324000L;
         * <p>
         * Lunar lunar = new Lunar(currentTimeMillis);//初始化高级功能
         * 节气: 如果指定的日期有节气则返回当天节气，如果没有则返回 "" 空字符串 不是 null
         * System.out.println("节气:" + lunar.getTermString());
         * <p>
         * 生肖:狗
         * System.out.println("生肖:" + lunar.getAnimalString());
         * <p>
         * 星期：2
         * System.out.println("星期：" + lunar.getDayOfWeek());//星期几(星期日为:1, 星期六为:7)
         * <p>
         * 干支历:戊戌年辛酉月丙寅日
         * System.out.println("干支历:" + lunar.getCyclicalDateString());
         * <p>
         * 农历:戊戌年八月廿二日
         * System.out.println("农历:" + lunar.getLunarDateString());
         * <p>
         * 当前是否为 农历节日:true
         * boolean lFestival = lunar.isLFestival();
         * System.out.println("当前是否为 农历节日:" + lFestival);
         * <p>
         * 农历节日:燃灯佛诞
         * if(lFestival){
         * System.out.println("农历节日:" + lunar.getLFestivalName());
         * }
         * <p>
         * 当前是否为公历节日:true
         * boolean sFestival = lunar.isSFestival();
         * System.out.println("当前是否为公历节日:" + lFestival);
         * if(sFestival){
         * 公历节日:国庆节
         * System.out.println("公历节日:" + lunar.getSFestivalName());
         * }
         * <p>
         * 当前是否为节日:true
         * boolean festival = lunar.isFestival();
         * System.out.println("当前是否为节日:" + festival);
         * <p>
         * 当前是否放假:true
         * boolean holiday = lunar.isHoliday();
         * System.out.println("当前是否放假:" + holiday);
         * <p>
         * <p>
         * Date[] jieqi = Lunar.jieqilist(2019);
         * for (int i = 0; i < Lunar.solarTerm.length; i++) {
         * System.out.print(Lunar.solarTerm[i]);
         *
         * @SuppressWarnings("deprecation") int month = jieqi[i].getMonth();
         * month += 1;
         * System.out.print(month + "月");
         * System.out.println(jieqi[i].getDate());
         * }
         * //对应结果
         * 小寒1月6
         * 大寒1月20
         * 立春2月4
         * 雨水2月19
         * 惊蛰3月6
         * 春分3月21
         * 清明4月5
         * 谷雨4月21
         * 立夏5月6
         * 小满5月22
         * 芒种6月6
         * 夏至6月22
         * 小暑7月8
         * 大暑7月23
         * 立秋8月8
         * 处暑8月24
         * 白露9月8
         * 秋分9月24
         * 寒露10月9
         * 霜降10月24
         * 立冬11月8
         * 小雪11月23
         * 大雪12月8
         * 冬至12月22
         */

        private Lunar lunar = null;

        /**
         * @return the lunar
         */
        public Lunar getLunar() {
            return lunar;
        }

        /**
         * @param lunar the lunar to set
         */
        public void setLunar(Lunar lunar) {
            this.lunar = lunar;
        }

        /**
         * 获取中国日期
         *
         * @return
         */
        public static String getDateTime_CH() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            return df.format(new Date());   //获取当时间
        }

        /**
         * 获取美国日期
         *
         * @return
         */
        public static String getDateTime_US() {
            SimpleDateFormat df = new SimpleDateFormat("ss:mm:HH dd-MM-yyyy");//设置日期格式
            return df.format(new Date());   //获取当时间
        }

        /**
         * 日期格式
         *
         * @param dateTimeFormat
         * @return
         */
        public static String getDateTime(String dateTimeFormat) {
            SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);//设置日期格式
            return df.format(new Date());   //获取当时间
        }

        /**
         * 初始化时间 基础功能
         */
        public GT_Date() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            time = df.format(new Date());   //获取当时间
            times = time.split(" ");        //分割时间 年月日  时分秒 数组
            ymd = times[0].split("-");    //分割年月日 数组
            hms = times[1].split(":");    //分割时分秒 数组
        }

        /**
         * 初始化时间 高级功能
         */
        public GT_Date(long timestamp) {

            //初始化基本的时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            time = df.format(new Date());   //获取当时间
            times = time.split(" ");        //分割时间 年月日  时分秒 数组
            ymd = times[0].split("-");    //分割年月日 数组
            hms = times[1].split(":");    //分割时分秒 数组

            //初始化高级功能
            lunar = new Lunar(timestamp);

        }

        /**
         * 将多少秒转换成 时分秒 如：100秒 = 1分钟40秒
         *
         * @param seconds
         * @return
         */
        public static String secondsToTime(int seconds) {
            int h = seconds / 3600; // 小时
            int m = (seconds % 3600) / 60; // 分钟
            int s = (seconds % 3600) % 60; // 秒
            if (h > 0) {
                return h + "小时" + m + "分钟" + s + "秒";
            }
            if (m > 0) {
                return m + "分钟" + s + "秒";
            }
            return s + "秒";
        }

        /**
         * ***************日历工具的基础功能*******************
         */

        private static String time;        //定义返回的 时间整体字符串
        private static String[] times;     //定义分割后产生的 年月日 / 时分秒 数组
        private static String[] ymd;       //定义分割后产生的 年月日 数组
        private static String[] hms;       //定义分割后产生的 时分秒 数组

        /**
         * 获取当前星期
         *
         * @return
         */
        public static String getWeekOfDateString() {
            String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        }


        /**
         * 获取当前星期
         *
         * @return
         */
        public static int getWeekOfDateInt() {
            int[] weekDays = {0, 1, 2, 3, 4, 5, 6};
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            return weekDays[w];
        }


        /**
         * 获取当前 年月日
         *
         * @return
         */
        public String getYearMonthDay() {
            return times[0];
        }

        /**
         * 获取年
         *
         * @return
         */
        public String getYear() {
            return ymd[0];
        }

        /**
         * 获取月
         *
         * @return
         */
        public String getMonth() {
            return ymd[1];
        }

        /**
         * 获取日
         *
         * @return
         */
        public String getDay() {
            return ymd[2];
        }

        /**
         * /获取当前 时分秒
         *
         * @return
         */
        public String getHourMinuteSecond() {
            return times[1];
        }

        /**
         * 获取时
         *
         * @return
         */
        public String getHour() {
            return hms[0];
        }

        /**
         * 获取分
         *
         * @return
         */
        public String getMinute() {
            return hms[1];
        }

        /**
         * 获取秒
         *
         * @return
         */
        public String getSecond() {
            return hms[2];
        }

        /**
         * 时间戳转 时间
         *
         * @param dataTime
         * @return
         */
        public String toTime(String dataTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long myTime = Long.parseLong(dataTime);
            long lt = new Long(myTime * 1000);//返回来的时间戳1476929029是毫秒，这里要乘1000才是正确的北京时间
            Date date = new Date(lt);
            String time = formatter.format(date);
            return time;
        }

        /**
         * 时分秒
         *
         * @param dataTime
         * @return
         */
        public String toTime_hms(String dataTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long myTime = Long.parseLong(dataTime);
            long lt = new Long(myTime * 1000);//返回来的时间戳1476929029是毫秒，这里要乘1000才是正确的北京时间
            Date date = new Date(lt);
            String time = formatter.format(date);
            time = time.substring(time.length() - 8, time.length());
            return time;
        }

        /**
         * 离现在过去几小时
         *
         * @param dataTime
         * @return
         */
        public String toPastTime(String dataTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            long myTime = Long.parseLong(dataTime);
            long lt = new Long(myTime * 1000);//返回来的时间戳1476929029是毫秒，这里要乘1000才是正确的北京时间
            Date date = new Date(lt);
            String time = formatter.format(date);
            time = time.substring(time.length() - 8, time.length());
            time = time.substring(0, 2);
            String hour = getHour();
            int timeInt = Integer.parseInt(time);
            int hourInt = Integer.parseInt(hour);
            int showTime;

            if (hourInt < timeInt) {
                timeInt = 24 - timeInt; //一天减去 当时发送的时间
                showTime = hourInt + timeInt;
            } else {
                showTime = hourInt - timeInt;
            }

            //判断当前过去的时间是否大于0 最小为 1小时前
            if (showTime > 0) {
                time = showTime + "小时前";
            } else {//否则 进行分钟判断

                time = formatter.format(date);
                time = time.substring(time.length() - 8, time.length());
                time = time.substring(3, 5);

                timeInt = Integer.parseInt(time);
                hourInt = Integer.parseInt(getMinute());
                showTime = hourInt - timeInt;

                if (showTime > 0) {
                    time = showTime + "分钟前";
                } else {
                    time = "刚刚";
                }
            }

            return time;
        }

        /**
         * 时间戳转 年月日
         *
         * @param dataTime
         * @return
         */
        public String toYearMonthDay(String dataTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
            long myTime = Long.parseLong(dataTime);
            long lt = new Long(myTime * 1000);//返回来的时间戳1476929029是毫秒，这里要乘1000才是正确的北京时间
            Date date = new Date(lt);
            String time = formatter.format(date);
            return time;
        }

        /**
         * 时间戳转 北京时间
         *
         * @param dataTime
         * @return
         */
        public String toBeijingTime(String dataTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            long myTime = Long.parseLong(dataTime);
            long lt = new Long(myTime * 1000);//返回来的时间戳1476929029是毫秒，这里要乘1000才是正确的北京时间
            Date date = new Date(lt);
            String time = formatter.format(date);
            return time;
        }


        /**
         * ***********************日历的高级功能***********************
         */

        public static class Lunar {

            /**
             * 获得某天前个节气日期差
             *
             * @return 日期数
             */
            public static long getbeforesolarTerm(int year, Date date) {
                List<Date> jieqi = Alljieqi(year);
                int[] jieqibeforeafter = getnearsolarTerm(year, date);
                return MyDate.DateDays(date, jieqi.get(jieqibeforeafter[0]));
            }

            /**
             * 获得某天后个节气日期差
             *
             * @return 日期数
             */
            public static long getaftersolarTerm(int year, Date date) {
                List<Date> jieqi = Alljieqi(year);
                int[] jieqibeforeafter = getnearsolarTerm(year, date);
                return MyDate.DateDays(date, jieqi.get(jieqibeforeafter[1]));

            }

            public static List<Date> Alljieqi(int year) {

                List<Date> jieqi = new ArrayList<Date>();
                Date[] temp;
                temp = jieqilist(year - 1);
                jieqi.addAll(Arrays.asList(temp));
                temp = jieqilist(year);
                jieqi.addAll(Arrays.asList(temp));
                temp = jieqilist(year + 1);
                jieqi.addAll(Arrays.asList(temp));
                return jieqi;
            }


            /**
             * 获得某天前后两个节气序号
             *
             * @return
             */
            public static int[] getnearsolarTerm(int year, Date date) {
                List<Date> jieqi = Alljieqi(year);

                int[] returnValue = new int[2];
                for (int i = 0; i < jieqi.size(); i++) {
                    if (date.getTime() > jieqi.get(i).getTime()) {
                        continue;
                    }
                    if (i % 2 == 0) {//只管气
                        returnValue[0] = i - 2;
                        returnValue[1] = i;
                    } else {
                        returnValue[0] = i - 1;
                        returnValue[1] = i + 1;

                    }
                    break;
                }

                return returnValue;
            }


            /**
             * 获得某年中所有节气Date
             *
             * @return
             */
            public static Date[] jieqilist(int year) {
                Date[] returnvalue = new Date[solarTerm.length];

                for (int i = 0; i < solarTerm.length; i++) {

                    Date t = getSolarTermCalendar(year, i);
                    returnvalue[i] = t;

                }
                return returnvalue;
            }


            private final static int[] lunarInfo = {
                    0x4bd8, 0x4ae0, 0xa570, 0x54d5, 0xd260, 0xd950, 0x5554, 0x56af,
                    0x9ad0, 0x55d2, 0x4ae0, 0xa5b6, 0xa4d0, 0xd250, 0xd295, 0xb54f,
                    0xd6a0, 0xada2, 0x95b0, 0x4977, 0x497f, 0xa4b0, 0xb4b5, 0x6a50,
                    0x6d40, 0xab54, 0x2b6f, 0x9570, 0x52f2, 0x4970, 0x6566, 0xd4a0,
                    0xea50, 0x6a95, 0x5adf, 0x2b60, 0x86e3, 0x92ef, 0xc8d7, 0xc95f,
                    0xd4a0, 0xd8a6, 0xb55f, 0x56a0, 0xa5b4, 0x25df, 0x92d0, 0xd2b2,
                    0xa950, 0xb557, 0x6ca0, 0xb550, 0x5355, 0x4daf, 0xa5b0, 0x4573,
                    0x52bf, 0xa9a8, 0xe950, 0x6aa0, 0xaea6, 0xab50, 0x4b60, 0xaae4,
                    0xa570, 0x5260, 0xf263, 0xd950, 0x5b57, 0x56a0, 0x96d0, 0x4dd5,
                    0x4ad0, 0xa4d0, 0xd4d4, 0xd250, 0xd558, 0xb540, 0xb6a0, 0x95a6,
                    0x95bf, 0x49b0, 0xa974, 0xa4b0, 0xb27a, 0x6a50, 0x6d40, 0xaf46,
                    0xab60, 0x9570, 0x4af5, 0x4970, 0x64b0, 0x74a3, 0xea50, 0x6b58,
                    0x5ac0, 0xab60, 0x96d5, 0x92e0, 0xc960, 0xd954, 0xd4a0, 0xda50,
                    0x7552, 0x56a0, 0xabb7, 0x25d0, 0x92d0, 0xcab5, 0xa950, 0xb4a0,
                    0xbaa4, 0xad50, 0x55d9, 0x4ba0, 0xa5b0, 0x5176, 0x52bf, 0xa930,
                    0x7954, 0x6aa0, 0xad50, 0x5b52, 0x4b60, 0xa6e6, 0xa4e0, 0xd260,
                    0xea65, 0xd530, 0x5aa0, 0x76a3, 0x96d0, 0x4afb, 0x4ad0, 0xa4d0,
                    0xd0b6, 0xd25f, 0xd520, 0xdd45, 0xb5a0, 0x56d0, 0x55b2, 0x49b0,
                    0xa577, 0xa4b0, 0xaa50, 0xb255, 0x6d2f, 0xada0, 0x4b63, 0x937f,
                    0x49f8, 0x4970, 0x64b0, 0x68a6, 0xea5f, 0x6b20, 0xa6c4, 0xaaef,
                    0x92e0, 0xd2e3, 0xc960, 0xd557, 0xd4a0, 0xda50, 0x5d55, 0x56a0,
                    0xa6d0, 0x55d4, 0x52d0, 0xa9b8, 0xa950, 0xb4a0, 0xb6a6, 0xad50,
                    0x55a0, 0xaba4, 0xa5b0, 0x52b0, 0xb273, 0x6930, 0x7337, 0x6aa0,
                    0xad50, 0x4b55, 0x4b6f, 0xa570, 0x54e4, 0xd260, 0xe968, 0xd520,
                    0xdaa0, 0x6aa6, 0x56df, 0x4ae0, 0xa9d4, 0xa4d0, 0xd150, 0xf252, 0xd520
            };


            private final static int[] solarTermInfo = {
                    0, 21208, 42467, 63836, 85337, 107014, 128867, 150921,
                    173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033,
                    353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758
            };
            public final static String[] Tianan = {
                    "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
            };
            public final static String[] Deqi = {
                    "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
            };
            public final static String[] Animals = {
                    "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"
            };
            public final static String[] solarTerm = {
                    "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
                    "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
                    "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
                    "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
            };
            public final static String[] lunarString1 = {
                    "零", "一", "二", "三", "四", "五", "六", "七", "八", "九"
            };
            public final static String[] lunarString2 = {
                    "初", "十", "廿", "卅", "正", "腊", "冬", "闰"
            };
            /**
             * 国历节日 *表示放假日
             */
            private final static String[] sFtv = {
                    "0101*元旦", "0214 情人节", "0308 妇女节", "0312 植树节",
                    "0315 消费者权益日", "0401 愚人节", "0501*劳动节", "0504 青年节",
                    "0509 郝维节", "0512 护士节", "0601 儿童节", "0701 建党节 香港回归纪念",
                    "0801 建军节", "0808 父亲节", "0816 燕衔泥节", "0909 毛泽东逝世纪念",
                    "0910 教师节", "0928 孔子诞辰", "1001*国庆节", "1006 老人节",
                    "1024 联合国日", "1111 光棍节", "1112 孙中山诞辰纪念", "1220 澳门回归纪念",
                    "1225 圣诞节", "1226 毛泽东诞辰纪念"
            };
            /**
             * 农历节日 *表示放假日
             */
            private final static String[] lFtv = {
                    "0101*春节、弥勒佛诞", "0106 定光佛诞", "0115 元宵节",
                    "0208 释迦牟尼佛出家", "0215 释迦牟尼佛涅槃", "0209 海空上师诞",
                    "0219 观世音菩萨诞", "0221 普贤菩萨诞", "0316 准提菩萨诞",
                    "0404 文殊菩萨诞", "0408 释迦牟尼佛诞", "0415 佛吉祥日——释迦牟尼佛诞生、成道、涅槃三期同一庆(即南传佛教国家的卫塞节)",
                    "0505 端午节", "0513 伽蓝菩萨诞", "0603 护法韦驮尊天菩萨诞",
                    "0619 观世音菩萨成道——此日放生、念佛，功德殊胜",
                    "0707 七夕情人节", "0713 大势至菩萨诞", "0715 中元节",
                    "0724 龙树菩萨诞", "0730 地藏菩萨诞", "0815 中秋节",
                    "0822 燃灯佛诞", "0909 重阳节", "0919 观世音菩萨出家纪念日",
                    "0930 药师琉璃光如来诞", "1005 达摩祖师诞", "1107 阿弥陀佛诞",
                    "1208 释迦如来成道日，腊八节", "1224 小年",
                    "1229 华严菩萨诞", "0100*除夕"
            };
            /**
             * 某月的第几个星期几
             */
            private static String[] wFtv = {
                    "0520 母亲节", "0716 合作节", "0730 被奴役国家周"
            };

            private static int toInt(String str) {
                try {
                    return Integer.parseInt(str);
                } catch (Exception e) {
                    return -1;
                }
            }

            private final static Pattern sFreg = Pattern.compile("^(\\d{2})(\\d{2})([\\s\\*])(.+)$");
            private final static Pattern wFreg = Pattern.compile("^(\\d{2})(\\d)(\\d)([\\s\\*])(.+)$");

            private synchronized void findFestival() {
                System.out.println("进入节日获取");
                int sM = this.getSolarMonth();
                int sD = this.getSolarDay();
                int lM = this.getLunarMonth();
                int lD = this.getLunarDay();
                int sy = this.getSolarYear();
                Matcher m;
                for (int i = 0; i < Lunar.sFtv.length; i++) {
                    m = Lunar.sFreg.matcher(Lunar.sFtv[i]);

                    if (m.find()) {
                        if (sM == Lunar.toInt(m.group(1)) && sD == Lunar.toInt(m.group(2))) {
                            this.isSFestival = true;
                            this.sFestivalName = m.group(4);
                            if ("*".equals(m.group(3))) {
                                this.isHoliday = true;
                            }
                            break;
                        }
                    }
                }
                for (int i = 0; i < Lunar.lFtv.length; i++) {
                    m = Lunar.sFreg.matcher(Lunar.lFtv[i]);
                    if (m.find()) {
                        if (lM == Lunar.toInt(m.group(1)) && lD == Lunar.toInt(m.group(2))) {
                            this.isLFestival = true;
                            this.lFestivalName = m.group(4);
                            if ("*".equals(m.group(3))) {
                                this.isHoliday = true;
                            }
                            break;
                        }
                    }
                }
                // 月周节日
                int w, d;
                for (int i = 0; i < Lunar.wFtv.length; i++) {
                    m = Lunar.wFreg.matcher(Lunar.wFtv[i]);
                    if (m.find()) {
                        if (this.getSolarMonth() == Lunar.toInt(m.group(1))) {
                            w = Lunar.toInt(m.group(2));
                            d = Lunar.toInt(m.group(3));
                            if (this.solar.get(Calendar.WEEK_OF_MONTH) == w
                                    && this.solar.get(Calendar.DAY_OF_WEEK) == d) {
                                this.isSFestival = true;
                                this.sFestivalName += "|" + m.group(5);
                                if ("*".equals(m.group(4))) {
                                    this.isHoliday = true;
                                }
                            }
                        }
                    }
                }
                if (sy > 1874 && sy < 1909) {
                    this.description = "光绪" + (((sy - 1874) == 1) ? "元" : "" + (sy - 1874));
                }
                if (sy > 1908 && sy < 1912) {
                    this.description = "宣统" + (((sy - 1908) == 1) ? "元" : String.valueOf(sy - 1908));
                }
                if (sy > 1911 && sy < 1950) {
                    this.description = "民国" + (((sy - 1911) == 1) ? "元" : String.valueOf(sy - 1911));
                }
                if (sy > 1949) {
                    this.description = "共和国" + (((sy - 1949) == 1) ? "元" : String.valueOf(sy - 1949));
                }
                this.description += "年";
                this.sFestivalName = this.sFestivalName.replaceFirst("^\\|", "");
                this.isFinded = true;
            }

            private boolean isFinded = false;
            private boolean isSFestival = false;
            private boolean isLFestival = false;
            private String sFestivalName = "";
            private String lFestivalName = "";
            private String description = "";
            private boolean isHoliday = false;

            /**
             * 返回农历年闰月月份
             *
             * @param lunarYear 指定农历年份(数字)
             * @return 该农历年闰月的月份(数字, 没闰返回0)
             */
            private static int getLunarLeapMonth(int lunarYear) {
                // 数据表中,每个农历年用16bit来表示,
                // 前12bit分别表示12个月份的大小月,最后4bit表示闰月
                // 若4bit全为1或全为0,表示没闰, 否则4bit的值为闰月月份
                int leapMonth = Lunar.lunarInfo[lunarYear - 1900] & 0xf;
                leapMonth = (leapMonth == 0xf ? 0 : leapMonth);
                return leapMonth;
            }


            /**
             * 返回农历年闰月的天数
             *
             * @param lunarYear 指定农历年份(数字)
             * @return 该农历年闰月的天数(数字)
             */
            private static int getLunarLeapDays(int lunarYear) {
                // 下一年最后4bit为1111,返回30(大月)
                // 下一年最后4bit不为1111,返回29(小月)
                // 若该年没有闰月,返回0
                return Lunar.getLunarLeapMonth(lunarYear) > 0 ? ((Lunar.lunarInfo[lunarYear - 1899] & 0xf) == 0xf ? 30
                        : 29)
                        : 0;
            }

            /**
             * 返回农历年的总天数
             *
             * @param lunarYear 指定农历年份(数字)
             * @return 该农历年的总天数(数字)
             */
            private static int getLunarYearDays(int lunarYear) {
                // 按小月计算,农历年最少有12 * 29 = 348天
                int daysInLunarYear = 348;
                // 数据表中,每个农历年用16bit来表示,
                // 前12bit分别表示12个月份的大小月,最后4bit表示闰月
                // 每个大月累加一天
                for (int i = 0x8000; i > 0x8; i >>= 1) {
                    daysInLunarYear += ((Lunar.lunarInfo[lunarYear - 1900] & i) != 0) ? 1
                            : 0;
                }
                // 加上闰月天数
                daysInLunarYear += Lunar.getLunarLeapDays(lunarYear);

                return daysInLunarYear;
            }

            /**
             * 返回农历年正常月份的总天数
             *
             * @param lunarYear  指定农历年份(数字)
             * @param lunarMonth 指定农历月份(数字)
             * @return 该农历年闰月的月份(数字, 没闰返回0)
             */
            private static int getLunarMonthDays(int lunarYear, int lunarMonth) {
                // 数据表中,每个农历年用16bit来表示,
                // 前12bit分别表示12个月份的大小月,最后4bit表示闰月
                int daysInLunarMonth = ((Lunar.lunarInfo[lunarYear - 1900] & (0x10000 >> lunarMonth)) != 0) ? 30
                        : 29;
                return daysInLunarMonth;
            }


            /**
             * 取 Date 对象中用全球标准时间 (UTC) 表示的日期
             *
             * @param date 指定日期
             * @return UTC 全球标准时间 (UTC) 表示的日期
             */
            public static synchronized int getUTCDay(Date date) {
                Lunar.makeUTCCalendar();
                synchronized (utcCal) {
                    utcCal.clear();
                    utcCal.setTimeInMillis(date.getTime());
                    return utcCal.get(Calendar.DAY_OF_MONTH);
                }
            }

            private static GregorianCalendar utcCal = null;

            private static synchronized void makeUTCCalendar() {
                if (Lunar.utcCal == null) {
                    Lunar.utcCal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                }
            }

            /**
             * 返回全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数。
             *
             * @param y   指定年份
             * @param m   指定月份
             * @param d   指定日期
             * @param h   指定小时
             * @param min 指定分钟
             * @param sec 指定秒数
             * @return 全球标准时间 (UTC) (或 GMT) 的 1970 年 1 月 1 日到所指定日期之间所间隔的毫秒数
             */
            public static synchronized long UTC(int y, int m, int d, int h, int min, int sec) {
                Lunar.makeUTCCalendar();
                synchronized (utcCal) {
                    utcCal.clear();
                    utcCal.set(y, m, d, h, min, sec);
                    return utcCal.getTimeInMillis();
                }
            }

            /**
             * 返回公历年节气的日期
             *
             * @param solarYear 指定公历年份(数字)
             * @param index     指定节气序号(数字,0从小寒算起)
             * @return 日期(数字, 所在月份的第几天)
             */
            private static int getSolarTermDay(int solarYear, int index) {

                return Lunar.getUTCDay(getSolarTermCalendar(solarYear, index));
            }

            /**
             * 返回公历年节气的日期
             *
             * @param solarYear 指定公历年份(数字)
             * @param index     指定节气序号(数字,0从小寒算起)
             * @return 日期(数字, 所在月份的第几天)
             */
            public static Date getSolarTermCalendar(int solarYear, int index) {
                long l = (long) 31556925974.7 * (solarYear - 1900)
                        + solarTermInfo[index] * 60000L;
                l = l + Lunar.UTC(1900, 0, 6, 2, 5, 0);
                return new Date(l);
            }

            private Calendar solar;
            private int lunarYear;
            private int lunarMonth;
            private int lunarDay;
            private boolean isLeap;
            private boolean isLeapYear;
            private int solarYear;
            private int solarMonth;
            private int solarDay;
            private int cyclicalYear = 0;
            private int cyclicalMonth = 0;
            private int cyclicalDay = 0;
            private int maxDayInMonth = 29;

            /**
             * 通过 Date 对象构建农历信息
             *
             * @param date 指定日期对象
             */
            public Lunar(Date date) {
                if (date == null) {
                    date = new Date();
                }
                this.init(date.getTime());
            }

            /**
             * 通过 TimeInMillis 构建农历信息
             *
             * @param TimeInMillis
             */
            public Lunar(long TimeInMillis) {
                this.init(TimeInMillis);
            }

            private void init(long TimeInMillis) {
                this.solar = Calendar.getInstance();
                this.solar.setTimeInMillis(TimeInMillis);
                Calendar baseDate = new GregorianCalendar(1900, 0, 31);
                long offset = (TimeInMillis - baseDate.getTimeInMillis()) / 86400000;
                // 按农历年递减每年的农历天数，确定农历年份
                this.lunarYear = 1900;
                int daysInLunarYear = Lunar.getLunarYearDays(this.lunarYear);
                while (this.lunarYear < 2100 && offset >= daysInLunarYear) {
                    offset -= daysInLunarYear;
                    daysInLunarYear = Lunar.getLunarYearDays(++this.lunarYear);
                }
                // 农历年数字

                // 按农历月递减每月的农历天数，确定农历月份
                int lunarMonth = 1;
                // 所在农历年闰哪个月,若没有返回0
                int leapMonth = Lunar.getLunarLeapMonth(this.lunarYear);
                // 是否闰年
                this.isLeapYear = leapMonth > 0;
                // 闰月是否递减
                boolean leapDec = false;
                boolean isLeap = false;
                int daysInLunarMonth = 0;
                while (lunarMonth < 13 && offset > 0) {
                    if (isLeap && leapDec) { // 如果是闰年,并且是闰月
                        // 所在农历年闰月的天数
                        daysInLunarMonth = Lunar.getLunarLeapDays(this.lunarYear);
                        leapDec = false;
                    } else {
                        // 所在农历年指定月的天数
                        daysInLunarMonth = Lunar.getLunarMonthDays(this.lunarYear, lunarMonth);
                    }
                    if (offset < daysInLunarMonth) {
                        break;
                    }
                    offset -= daysInLunarMonth;

                    if (leapMonth == lunarMonth && isLeap == false) {
                        // 下个月是闰月
                        leapDec = true;
                        isLeap = true;
                    } else {
                        // 月份递增
                        lunarMonth++;
                    }
                }
                this.maxDayInMonth = daysInLunarMonth;
                // 农历月数字
                this.lunarMonth = lunarMonth;
                // 是否闰月
                this.isLeap = (lunarMonth == leapMonth && isLeap);
                // 农历日数字
                this.lunarDay = (int) offset + 1;
                // 取得干支历
                this.getCyclicalData();
            }

            /**
             * 取干支历 不是历年，历月干支，而是中国的从立春节气开始的节月，是中国的太阳十二宫，阳历的。
             */
            private void getCyclicalData() {
                this.solarYear = this.solar.get(Calendar.YEAR);
                this.solarMonth = this.solar.get(Calendar.MONTH);
                this.solarDay = this.solar.get(Calendar.DAY_OF_MONTH);
                // 干支历
                int cyclicalYear = 0;
                int cyclicalMonth = 0;
                int cyclicalDay = 0;

                // 干支年 1900年立春後为庚子年(60进制36)
                int term2 = Lunar.getSolarTermDay(solarYear, 2); // 立春日期
                // 依节气调整二月分的年柱, 以立春为界
                if (solarMonth < 1 || (solarMonth == 1 && solarDay < term2)) {
                    cyclicalYear = (solarYear - 1900 + 36 - 1) % 60;
                } else {
                    cyclicalYear = (solarYear - 1900 + 36) % 60;
                }

                // 干支月 1900年1月小寒以前为 丙子月(60进制12)
                int firstNode = Lunar.getSolarTermDay(solarYear, solarMonth * 2); // 传回当月「节」为几日开始
                // 依节气月柱, 以「节」为界
                if (solarDay < firstNode) {
                    cyclicalMonth = ((solarYear - 1900) * 12 + solarMonth + 12) % 60;
                } else {
                    cyclicalMonth = ((solarYear - 1900) * 12 + solarMonth + 13) % 60;
                }

                // 当月一日与 1900/1/1 相差天数
                // 1900/1/1与 1970/1/1 相差25567日, 1900/1/1 日柱为甲戌日(60进制10)
                cyclicalDay = (int) (Lunar.UTC(solarYear, solarMonth, solarDay, 0, 0, 0) / 86400000 + 25567 + 10) % 60;
                this.cyclicalYear = cyclicalYear;
                this.cyclicalMonth = cyclicalMonth;
                this.cyclicalDay = cyclicalDay;
            }

            /**
             * 取农历年生肖
             *
             * @return 农历年生肖(例 : 龙)
             */
            public String getAnimalString() {
                return Lunar.Animals[(this.lunarYear - 4) % 12];
            }

            /**
             * 返回公历日期的节气字符串
             *
             * @return 二十四节气字符串, 若不是节气日, 返回空串(例 : 冬至)
             */
            public String getTermString() {
                // 二十四节气
                String termString = "";
                if (Lunar.getSolarTermDay(solarYear, solarMonth * 2) == solarDay) {
                    termString = Lunar.solarTerm[solarMonth * 2];
                } else if (Lunar.getSolarTermDay(solarYear, solarMonth * 2 + 1) == solarDay) {
                    termString = Lunar.solarTerm[solarMonth * 2 + 1];
                }
                return termString;
            }

            /**
             * 取得干支历字符串
             *
             * @return 干支历字符串(例 : 甲子年甲子月甲子日)
             */
            public String getCyclicalDateString() {
                return this.getCyclicaYear() + "年" + this.getCyclicaMonth() + "月"
                        + this.getCyclicaDay() + "日";
            }

            /**
             * 年份天干
             *
             * @return 年份天干
             */
            public int getTiananY() {
                return Lunar.getTianan(this.cyclicalYear);
            }

            /**
             * 月份天干
             *
             * @return 月份天干
             */
            public int getTiananM() {
                return Lunar.getTianan(this.cyclicalMonth);
            }

            /**
             * 日期天干
             *
             * @return 日期天干
             */
            public int getTiananD() {
                return Lunar.getTianan(this.cyclicalDay);
            }

            /**
             * 年份地支
             *
             * @return 年分地支
             */
            public int getDeqiY() {
                return Lunar.getDeqi(this.cyclicalYear);
            }

            /**
             * 月份地支
             *
             * @return 月份地支
             */
            public int getDeqiM() {
                return Lunar.getDeqi(this.cyclicalMonth);
            }

            /**
             * 日期地支
             *
             * @return 日期地支
             */
            public int getDeqiD() {
                return Lunar.getDeqi(this.cyclicalDay);
            }

            /**
             * 取得干支年字符串
             *
             * @return 干支年字符串
             */
            public String getCyclicaYear() {
                return Lunar.getCyclicalString(this.cyclicalYear);
            }

            /**
             * 取得干支月字符串
             *
             * @return 干支月字符串
             */
            public String getCyclicaMonth() {
                return Lunar.getCyclicalString(this.cyclicalMonth);
            }

            /**
             * 取得干支日字符串
             *
             * @return 干支日字符串
             */
            public String getCyclicaDay() {
                return Lunar.getCyclicalString(this.cyclicalDay);
            }

            /**
             * 返回农历日期字符串
             *
             * @return 农历日期字符串
             */
            public String getLunarDayString() {
                return Lunar.getLunarDayString(this.lunarDay);
            }

            /**
             * 返回农历日期字符串
             *
             * @return 农历日期字符串
             */
            public String getLunarMonthString() {
                return (this.isLeap() ? "闰" : "") + Lunar.getLunarMonthString(this.lunarMonth);
            }

            /**
             * 返回农历日期字符串
             *
             * @return 农历日期字符串
             */
            public String getLunarYearString() {
                return Lunar.getLunarYearString(this.lunarYear);
            }

            /**
             * 返回农历表示字符串
             *
             * @return 农历字符串(例 : 甲子年正月初三)
             */
            public String getLunarDateString() {
                return this.getLunarYearString() + "年"
                        + this.getLunarMonthString() + "月"
                        + this.getLunarDayString() + "日";
            }

            /**
             * 农历年是否是闰月
             *
             * @return 农历年是否是闰月
             */
            public boolean isLeap() {
                return isLeap;
            }

            /**
             * 农历年是否是闰年
             *
             * @return 农历年是否是闰年
             */
            public boolean isLeapYear() {
                return isLeapYear;
            }

            /**
             * 当前农历月是否是大月
             *
             * @return 当前农历月是大月
             */
            public boolean isBigMonth() {
                return this.getMaxDayInMonth() > 29;
            }

            /**
             * 当前农历月有多少天
             *
             * @return 当前农历月有多少天
             */
            public int getMaxDayInMonth() {
                return this.maxDayInMonth;
            }

            /**
             * 农历日期
             *
             * @return 农历日期
             */
            public int getLunarDay() {
                return lunarDay;
            }

            /**
             * 农历月份
             *
             * @return 农历月份
             */
            public int getLunarMonth() {
                return lunarMonth;
            }

            /**
             * 农历年份
             *
             * @return 农历年份
             */
            public int getLunarYear() {
                return lunarYear;
            }

            /**
             * 公历日期
             *
             * @return 公历日期
             */
            public int getSolarDay() {
                return solarDay;
            }

            /**
             * 公历月份
             *
             * @return 公历月份 (不是从0算起)
             */
            public int getSolarMonth() {
                return solarMonth + 1;
            }

            /**
             * 公历年份
             *
             * @return 公历年份
             */
            public int getSolarYear() {
                return solarYear;
            }

            /**
             * 星期几
             *
             * @return 星期几(星期日为 : 1, 星期六为 : 7)
             */
            public int getDayOfWeek() {
                return this.solar.get(Calendar.DAY_OF_WEEK);
            }

            /**
             * 黑色星期五
             *
             * @return 是否黑色星期五
             */
            public boolean isBlackFriday() {
                return (this.getSolarDay() == 13 && this.solar.get(Calendar.DAY_OF_WEEK) == 6);
            }

            /**
             * 是否是今日
             *
             * @return 是否是今日
             */
            public boolean isToday() {
                Calendar clr = Calendar.getInstance();
                return clr.get(Calendar.YEAR) == this.solarYear
                        && clr.get(Calendar.MONTH) == this.solarMonth
                        && clr.get(Calendar.DAY_OF_MONTH) == this.solarDay;
            }

            /**
             * 取得公历节日名称
             *
             * @return 公历节日名称, 如果不是节日返回空串
             */
            public String getSFestivalName() {
                return this.sFestivalName;
            }

            /**
             * 取得农历节日名称
             *
             * @return 农历节日名称, 如果不是节日返回空串
             */
            public String getLFestivalName() {
                return this.lFestivalName;
            }

            /**
             * 是否是农历节日
             *
             * @return 是否是农历节日
             */
            public boolean isLFestival() {
                if (!this.isFinded) {
                    this.findFestival();
                }
                return this.isLFestival;
            }

            /**
             * 是否是公历节日
             *
             * @return 是否是公历节日
             */
            public boolean isSFestival() {
                if (!this.isFinded) {
                    this.findFestival();
                }
                return this.isSFestival;
            }

            /**
             * 是否是节日
             *
             * @return 是否是节日
             */
            public boolean isFestival() {
                return this.isSFestival() || this.isLFestival();
            }

            /**
             * 是否是放假日
             *
             * @return 是否是放假日
             */
            public boolean isHoliday() {
                if (!this.isFinded) {
                    this.findFestival();
                }
                return this.isHoliday;
            }

            /**
             * 其它日期说明
             *
             * @return 日期说明(如 : 民国2年)
             */
            public String getDescription() {
                if (!this.isFinded) {
                    this.findFestival();
                }
                return this.description;
            }

            /**
             * 干支字符串
             *
             * @param cyclicalNumber 指定干支位置(数字,0为甲子)
             * @return 干支字符串
             */
            private static String getCyclicalString(int cyclicalNumber) {
                return Lunar.Tianan[Lunar.getTianan(cyclicalNumber)] + Lunar.Deqi[Lunar.getDeqi(cyclicalNumber)];
            }

            /**
             * 获得地支
             *
             * @param cyclicalNumber
             * @return 地支 (数字)
             */
            private static int getDeqi(int cyclicalNumber) {
                return cyclicalNumber % 12;
            }

            /**
             * 获得天干
             *
             * @param cyclicalNumber
             * @return 天干 (数字)
             */
            private static int getTianan(int cyclicalNumber) {
                return cyclicalNumber % 10;
            }

            /**
             * 返回指定数字的农历年份表示字符串
             *
             * @param lunarYear 农历年份(数字,0为甲子)
             * @return 农历年份字符串
             */
            private static String getLunarYearString(int lunarYear) {
                return Lunar.getCyclicalString(lunarYear - 1900 + 36);
            }

            /**
             * 返回指定数字的农历月份表示字符串
             *
             * @param lunarMonth 农历月份(数字)
             * @return 农历月份字符串 (例:正)
             */
            private static String getLunarMonthString(int lunarMonth) {
                String lunarMonthString = "";
                if (lunarMonth == 1) {
                    lunarMonthString = Lunar.lunarString2[4];
                } else {
                    if (lunarMonth > 9) {
                        lunarMonthString += Lunar.lunarString2[1];
                    }
                    if (lunarMonth % 10 > 0) {
                        lunarMonthString += Lunar.lunarString1[lunarMonth % 10];
                    }
                }
                return lunarMonthString;
            }

            /**
             * 返回指定数字的农历日表示字符串
             *
             * @param lunarDay 农历日(数字)
             * @return 农历日字符串 (例: 廿一)
             */
            private static String getLunarDayString(int lunarDay) {
                if (lunarDay < 1 || lunarDay > 30) {
                    return "";
                }
                int i1 = lunarDay / 10;
                int i2 = lunarDay % 10;
                String c1 = Lunar.lunarString2[i1];
                String c2 = Lunar.lunarString1[i2];
                if (lunarDay < 11) {
                    c1 = Lunar.lunarString2[0];
                }
                if (i2 == 0) {
                    c2 = Lunar.lunarString2[1];
                }
                return c1 + c2;
            }


            //日期工具辅助类
            private static class MyDate {

                private static final int[] dayMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                public int day;
                @SuppressWarnings("unused")
                public int dayCyl;
                @SuppressWarnings("unused")
                public boolean isLeap;
                @SuppressWarnings("unused")
                public int monCyl;
                public int month;
                public int year;
                @SuppressWarnings("unused")
                public int yearCyl;

                @SuppressWarnings("unused")
                public MyDate() {
                }

                @SuppressWarnings("unused")
                public MyDate(int paramInt1, int paramInt2, int paramInt3) {
                    this.year = paramInt1;
                    this.month = paramInt2;
                    this.day = paramInt3;
                }

                @SuppressWarnings("unused")
                public static int GetNumDayOfMonth(int paramInt1, int paramInt2) {
                    int i = dayMonth[(paramInt2 - 1)];
                    if ((IsBigYear(paramInt1)) && (paramInt2 == 2)) {
                        i++;
                    }
                    return i;
                }

                public static boolean IsBigYear(int paramInt) {
                    if (paramInt % 400 == 0) {
                        return true;
                    }
                    return (paramInt % 4 == 0) && (paramInt % 100 != 0);
                }

                public static long DateDays(Date aDate, Date aDate2) {
                    long myTime;
                    long myTime2;
                    long days = 0;
                    myTime = (aDate.getTime() / 1000);
                    // SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
                    myTime2 = (aDate2.getTime() / 1000);
                    if (myTime > myTime2) {
                        days = (myTime - myTime2) / (1 * 60 * 60 * 24);
                    } else {
                        days = (myTime2 - myTime) / (1 * 60 * 60 * 24);
                    }
                    return days;

                }
                // 求2个日期的天数

                @SuppressWarnings("unused")
                public static long DateDays(String date1, String date2) throws ParseException {

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    long myTime;
                    Date aDate2;
                    Date aDate;
                    long myTime2;
                    long days = 0;

                    aDate = formatter.parse(date1);// 任意日期，包括当前日期
                    myTime = (aDate.getTime() / 1000);

                    // SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
                    aDate2 = formatter.parse(date2);// 任意日期，包括当前日期
                    myTime2 = (aDate2.getTime() / 1000);

                    if (myTime > myTime2) {
                        days = (myTime - myTime2) / (1 * 60 * 60 * 24);
                    } else {
                        days = (myTime2 - myTime) / (1 * 60 * 60 * 24);
                    }


                    return days;

                }

                // 求2个日期的天数
                public static long DateDays2(int year1, int month1, int day1, int year2,
                                             int month2, int day2) throws ParseException, ParseException {

                    String date1;
                    String date2;
                    date1 = year1 + "-" + month1 + "-" + day1;
                    date2 = year2 + "-" + month2 + "-" + day2;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    long myTime;
                    Date aDate;
                    Date aDate2;
                    long myTime2;
                    long days = 0;

                    aDate = formatter.parse(date1);// 任意日期，包括当前日期
                    myTime = (aDate.getTime() / 1000);

                    // SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
                    aDate2 = formatter.parse(date2);// 任意日期，包括当前日期
                    myTime2 = (aDate2.getTime() / 1000);

                    if (myTime > myTime2) {
                        days = (myTime - myTime2) / (1 * 60 * 60 * 24);
                    } else {
                        days = (myTime2 - myTime) / (1 * 60 * 60 * 24);
                    }


                    return days;

                }

                public int GetNumDayFrom19000101() {
                    int day = 0;
                    try {
                        day = (int) DateDays2(this.year, this.month, this.day, 1900, 1, 1);
                    } catch (ParseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    return day;
                }

                @SuppressWarnings("unused")
                public long GetSFrom19000101() {
                    return 86400L * (1L + GetNumDayFrom19000101());
                }

            }


        }


    }

    /**
     * 手机屏幕操作
     */
    public static class ScreenOperation {

        /**
         * 点击屏幕 可根据 屏幕的比例 与 具体的 X,Y 坐标点击
         */
        public static class AutoTouch {
            private static int width = 0;
            private static int height = 0;

            /**
             * 传入在屏幕中的比例位置，坐标左上角为基准
             *
             * @param act    传入Activity对象
             * @param ratioX 需要点击的x坐标在屏幕中的比例位置
             * @param ratioY 需要点击的y坐标在屏幕中的比例位置
             */
            public static void autoClickRatio(Activity act, final double ratioX, final double ratioY) {
                width = act.getWindowManager().getDefaultDisplay().getWidth();
                height = act.getWindowManager().getDefaultDisplay().getHeight();
                Thread.runJava(new Runnable() {
                    @Override
                    public void run() {
                        // 线程睡眠0.3s
                        Thread.sleep(300);
                        // 生成点击坐标
                        int x = (int) (width * ratioX);
                        int y = (int) (height * ratioY);

                        // 利用ProcessBuilder执行shell命令
                        String[] order = {"input", "tap", "" + x, "" + y};
                        try {
                            new ProcessBuilder(order).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            /**
             * 传入在屏幕中的坐标，坐标左上角为基准
             *
             * @param act 传入Activity对象
             * @param x   需要点击的x坐标
             * @param y   需要点击的x坐标
             */
            public static void autoClickPos(Activity act, final double x, final double y) {
                width = act.getWindowManager().getDefaultDisplay().getWidth();
                height = act.getWindowManager().getDefaultDisplay().getHeight();
                // 利用ProcessBuilder执行shell命令
                String[] order = {"input", "tap", "" + x, "" + y};
                try {
                    new ProcessBuilder(order).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * @ApplicationUtils 应用程序的小工具集合
     */
    public static class ApplicationUtils {

        /**
         * 分享文字
         *
         * @param activity
         * @param title
         * @param content
         */
        public static void senText(Activity activity, String title, String content) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, content);
            shareIntent = Intent.createChooser(shareIntent, title);
            activity.startActivity(shareIntent);
        }

        /**
         * 分享文件
         *
         * @param activity
         * @param sharTitle
         * @param filePath
         */
        public static void shareFile(Activity activity, String sharTitle, String filePath) {
            Intent intent = new Intent(Intent.ACTION_SEND);// 发送多个文件
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, sharTitle));

        }

        /**
         * 弹出软件盘
         *
         * @param editText
         * @param activity
         */
        public static void editKeyboard(EditText editText, Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            editText.requestFocus();// 为搜索框 获取光标
        }

        /**
         * 收起软键盘
         *
         * @param editText
         * @param activity
         */
        public static void editKeyShrink(EditText editText, Activity activity) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        /**
         * 将字符串复制到粘贴板上
         *
         * @param context
         * @param text
         */
        public static void copyToClipboard(Context context, String text) {
            ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            systemService.setPrimaryClip(ClipData.newPlainText("text", text));
        }

        /**
         * 保存图片
         *
         * @param activity  上下文
         * @param view     保存图片的组件
         * @param fileName 文件名
         */
        public static void saveImage(Activity activity, View view, String savePath, String fileName) {

            // 保存图片
            Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            view.draw(canvas);

            // 更新图库
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();// 创建整个目录
            }

            try {
                FileOutputStream out = new FileOutputStream(savePath + fileName);
                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 通知系统更新图库
            if (file.exists()) {
                try {
                    MediaStore.Images.Media.insertImage(activity.getContentResolver(), savePath + fileName, fileName, null);// 把文件插入到系统图库
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(savePath + fileName))));// 发送广播通知系统
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }

        /**
         * 获取App名字
         *
         * @param context
         * @return
         */
        public static String getAppName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                int labelRes = packageInfo.applicationInfo.labelRes;
                return context.getResources().getString(labelRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 获取软件版本号
         *
         * @param mContext
         * @return
         */
        public static int getVersionCode(Context mContext) {
            int versionCode = 0;
            try {
                // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
                versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return versionCode;
        }

        /**
         * 获取版本号名称
         *
         * @param context
         * @return
         */
        public static String getVerName(Context context) {
            String verName = "";
            try {
                verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return verName;
        }

        /**
         * 解压文件
         *
         * @param zipPtath        解压文件的路径
         * @param outputDirectory 解压后的输出路径
         * @param isDeleteZipPage 是否保留压缩文件
         * @throws IOException
         */
        public static void unzipFile(String zipPtath, String outputDirectory, boolean isDeleteZipPage)
                throws IOException {
            /**
             * 解压assets的zip压缩文件到指定目录
             *
             * @param context上下文对象
             * @param assetName压缩文件名
             * @param outputDirectory输出目录
             * @param isReWrite是否覆盖
             * @throws IOException
             */

            //	        Log.i(TAG,"开始解压的文件： "  + zipPtath + "\n" + "解压的目标路径：" + outputDirectory );
            // 创建解压目标目录
            File file = new File(outputDirectory);
            // 如果目标目录不存在，则创建
            if (!file.exists()) {
                file.mkdirs();
            }
            // 打开压缩文件
            InputStream inputStream = new FileInputStream(zipPtath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            // 读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            // 解压时字节计数
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) { // 如果是一个文件
                    // 如果是文件
                    String fileName = zipEntry.getName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1); // 截取文件的名字 去掉原文件夹名字
                    file = new File(outputDirectory + File.separator + fileName); // 放到新的解压的文件路径

                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();

                }

                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            if (isDeleteZipPage) {
                new File(zipPtath).delete();// 删除当前补丁压缩包
            }

        }

        /**
         * 获取文件夹中所有文件名
         *
         * @param path
         * @return
         */
        public static List<String> getFilesAllName(String path) {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files == null) {
                Log.e("error", "空目录");
                return null;
            }
            List<String> s = new ArrayList<String>();
            for (int i = 0; i < files.length; i++) {
                s.add(files[i].getAbsolutePath());
            }
            return s;
        }

        /**
         * 清空文件夹中所有文件
         *
         * @param file         清空的文件路径
         * @param isSaveFolder 是否保存当前文件夹 true：b
         */
        public static void deleteAllFile(File file, boolean isSaveFolder) {

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    deleteAllFile(f, isSaveFolder);
                }
                if (!isSaveFolder) {// 是否保留本文件夹
                    file.delete();// 如要保留文件夹，只删除文件，请注释这行
                }
            } else if (file.exists()) {
                file.delete();
            }
        }

        /**
         * 获取手机根目录
         *
         * @return
         */
        public static String getAppDirectory() {
            return Environment.getExternalStorageDirectory().toString();
        }

        /**
         * 获取当前apk包名
         *
         * @param context
         * @return
         */
        public static String getPackageName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                return packageInfo.packageName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * @获取当前手机里的应用列表
         */
        public static class PrintPhoneAppList {

            public List<String> getAllAppList(Activity activity) {
                List<ResolveInfo> resolveInfos = getResolveInfos(activity);
                List<String> appData = getAppData(resolveInfos, activity);
                return appData;
            }

            private List<ResolveInfo> getResolveInfos(Activity activity) {
                List<ResolveInfo> appList = null;

                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                PackageManager pm = activity.getPackageManager();
                appList = pm.queryIntentActivities(intent, 0);
                Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));

                return appList;

            }

            private List<String> getAppData(List<ResolveInfo> resolveInfos, Activity activity) {

                List<String> appData = new ArrayList<>();
                PackageManager packageManager = activity.getPackageManager();
                for (int i = 0; i < resolveInfos.size(); i++) {
                    String pkg = resolveInfos.get(i).activityInfo.packageName;
                    String cls = resolveInfos.get(i).activityInfo.name;
                    String title = null;

                    try {
                        ApplicationInfo applicationInfo = packageManager.getPackageInfo(pkg, i).applicationInfo;
                        title = applicationInfo.loadLabel(packageManager).toString();
                    } catch (Exception e) {

                    }
                    appData.add(title + "：" + pkg + "/" + cls);
                }
                return appData;
            }

        }

        /**
         * 获取当前手机所有App信息
         *
         * @param context
         * @return
         */
        public static ArrayList<HashMap<String, Object>> getAllAppData(Context context) {
            PackageManager pckMan = context.getPackageManager();
            ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
            List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);
            for (PackageInfo pInfo : packageInfo) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("appimage", pInfo.applicationInfo.loadIcon(pckMan));
                item.put("packageName", pInfo.packageName);
                item.put("versionCode", pInfo.versionCode);
                item.put("versionName", pInfo.versionName);
                item.put("appName", pInfo.applicationInfo.loadLabel(pckMan).toString());
                items.add(item);
            }
            return items;
        }

    }



    //=========================================== 字符串加密类 =========================================

    /**
     * @加密类
     */
    public static class Encryption {

        /**
         * @MD5 加密算法
         */
        public static class MD5 {

            private static final String hexDigIts[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
                    "e", "f"};

            /**
             * MD5加密
             *
             * @param origin 字符
             * @return
             */
            public static String encryptMD5(String origin) {
                String resultString = null;
                try {
                    resultString = new String(origin);
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    if (null == "UTF-8" || "".equals("UTF-8")) {
                        resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
                    } else {
                        resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
                    }
                } catch (Exception e) {
                }
                return resultString;
            }

            private static String byteArrayToHexString(byte b[]) {
                StringBuffer resultSb = new StringBuffer();
                for (int i = 0; i < b.length; i++) {
                    resultSb.append(byteToHexString(b[i]));
                }
                return resultSb.toString();
            }

            private static String byteToHexString(byte b) {
                int n = b;
                if (n < 0) {
                    n += 256;
                }
                int d1 = n / 16;
                int d2 = n % 16;
                return hexDigIts[d1] + hexDigIts[d2];
            }

        }

        /**
         * @DES 加密算法
         */
        public static class DES {

            private static String paw = "☯✪☭☮♞";

            /**
             * 加密
             *
             * @param clearText
             * @return
             */
            public static String encryptPassword(Object clearText, Object password) {
                password += paw;
                try {
                    DESKeySpec keySpec = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        keySpec = new DESKeySpec(String.valueOf(password).getBytes(StandardCharsets.UTF_8));
                    }
                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                    SecretKey key = keyFactory.generateSecret(keySpec);

                    Cipher cipher = Cipher.getInstance("DES");
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        return Base64.encodeToString(cipher.doFinal(String.valueOf(clearText).getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return String.valueOf(clearText);
            }

            /**
             * 解密
             *
             * @param encryptedPwd
             * @return
             */
            public static String decryptPassword(Object encryptedPwd, Object password) {
                password += paw;
                try {
                    DESKeySpec keySpec = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        keySpec = new DESKeySpec(String.valueOf(password).getBytes(StandardCharsets.UTF_8));
                    }
                    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                    SecretKey key = keyFactory.generateSecret(keySpec);

                    byte[] encryptedWithoutB64 = Base64.decode(String.valueOf(encryptedPwd), Base64.DEFAULT);
                    Cipher cipher = Cipher.getInstance("DES");
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
                    return new String(plainTextPwdBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return String.valueOf(encryptedPwd);
            }
        }


    }



    /**
     * Window 窗体类
     */
    public static abstract class Window {

        /**
         * 屏幕常亮
         *
         * @param activity
         */
        public static void light(Activity activity) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        }

        /**
         * 设置全屏模式
         *
         * @param activity
         */
        public static void fullScreen(Activity activity) {
            activity.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        /**
         * 关闭虚拟按钮
         *
         * @param activity
         */
        public static void Close_virtualButton(Activity activity) {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else {
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }

        /**
         * 关闭虚拟按钮2
         *
         * @param window
         */
        public static void setHideVirtualKey(android.view.Window window) {
            //保持布局状态
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    //布局位于状态栏下方
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    //全屏
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    //隐藏导航栏
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= 19) {
                uiOptions |= 0x00001000;
            } else {
                uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            window.getDecorView().setSystemUiVisibility(uiOptions);
        }

        /**
         * 获取屏幕 宽度
         *
         * @param appCompatActivity
         * @return
         */
        public static int getWindowWidth(AppCompatActivity appCompatActivity) {
            int width = appCompatActivity.getWindowManager().getDefaultDisplay().getWidth();
            return width;
        }

        /**
         * 获取屏幕 高度
         *
         * @param appCompatActivity
         * @return
         */
        public static int getWindowHeight(AppCompatActivity appCompatActivity) {
            int height = appCompatActivity.getWindowManager().getDefaultDisplay().getHeight();
            return height;
        }

        /**
         * 隐藏状态栏
         *
         * @param activity
         */
        public static void hideStatusBar(AppCompatActivity activity) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }

        /**
         * 隐藏ActionBar
         *
         * @param activity
         */
        public static void hideActionBar(AppCompatActivity activity) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (activity != null) {
                actionBar.hide();
            }
        }

        /**
         * 隐藏导航栏
         *
         * @param activity
         */
        public static void hideNavigationBar(Activity activity) {
            if (Build.VERSION.SDK_INT >= 19) {
                View decorView = activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }

        /**
         * 透明状态栏
         *
         * @param activity
         */
        public static void lucencyStatusBar(Activity activity) {
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = activity.getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        /**
         * 透明导航栏
         *
         * @param activity
         */
        public static void lucencyNavigationBar(Activity activity) {
            if (Build.VERSION.SDK_INT >= 21) {
                View decorView = activity.getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                decorView.setSystemUiVisibility(option);
                activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }


        /**
         * 沉浸式模式 (隐藏状态栏，去掉ActionBar，隐藏导航栏)
         *
         * @param activity
         */
        public static void immersionMode(Activity activity) {
            if (Build.VERSION.SDK_INT >= 19) {
                View decorView = activity.getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }

        /**
         * 返回当前是 横屏 还是 竖屏
         *
         * @param activity
         * @return 横屏返回 true 竖屏返回 false
         */
        public static boolean isLandscapeAndPortrait(Activity activity) {
            Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信息
            if (mConfiguration.orientation == mConfiguration.ORIENTATION_LANDSCAPE) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * one_three:   0-6 的 值来进行强制的 横竖屏、自适应等
         * 执行强制的 横屏 或 竖屏
         *
         * @param activity
         */
        public static void AutoLandscapeAndPortrait(Activity activity, int one_three) {
            switch (one_three) {
                case 0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为正横屏
                    break;
                case 1:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//强制为反横屏
                    break;
                case 2:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为正竖屏
                    break;
                case 3:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);//强制为反竖屏
                    break;
                case 4:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);//为虚拟按键提供旋转屏幕提示
                    }
                    break;
                case 5:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//自动适宜 正横屏、反横屏、正竖屏
                    break;
                case 6:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//自动适宜 正横屏、反横屏
                    break;
            }


        }


    }

    /**
     * 设置触摸放大
     */
    public static class ViewTouchMagnify {
        private int viewWidth;        //保存按钮宽度
        private int viewHeight;        //保存按钮高度

        /**
         * 为按钮设置触摸事件
         *
         * @param view
         */
        public void touchZoomInView(final View view) {
            //设置按钮触摸事件
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if (arg1.getAction() == MotionEvent.ACTION_DOWN) {//如果用户手指触摸屏幕
                        viewWidth = view.getWidth();        //保存按钮的宽度
                        viewHeight = view.getHeight();        //保存按钮的高度
                        //                        view.setTextSize(18);								//设置按钮放大时字体大小
                        layoutParams.width = viewWidth + 20;                //设置按钮放大时的宽度
                        layoutParams.height = viewHeight + 10;            //设置按钮放大时的高度
                    } else if (arg1.getAction() == MotionEvent.ACTION_UP) {//如果用户手指离开屏幕
                        //                        button.setTextSize(15);							//设置按钮为原来字体大小
                        layoutParams.width = viewWidth;                //设置按钮为原来的宽度
                        layoutParams.height = viewHeight;                //设置按钮为原来的高度
                    }
                    view.setLayoutParams(layoutParams);    //提交事务
                    return false; //设置为未完成消耗掉的时间   如果将此返回为     true  那么按钮的  单击事件将会被屏蔽掉
                }
            });

        }

    }


    //============================================= 设备监听类 ======================================

    public static class DeviceListening {

        //获取当前手机信息
        public static class MobilePhoneAttribute {

            /**
             * 获取手机型号
             *
             * @return
             */
            public String getModel() {
                return Build.MODEL;
            }

            /**
             * 获取手机SDK版本号
             *
             * @return
             */
            public String getSDK() {
                return Build.VERSION.SDK;
            }

            /**
             * 获取手机系统版本号
             *
             * @return
             */
            public String getRELEASE() {
                return Build.VERSION.RELEASE;
            }

        }

        //AudioManager 安卓手机音量类
        public static class GT_AudioManager {
            /**
             * 提示：再设置音量大小时，请先搞清楚，该音量的最大值
             */
            private AudioManager mAudioManager;//定义 AudioManager
            private Activity activity;//定义 Activity
            private int max;//最大值
            private int current;//当前值

            /**
             * 初始化
             *
             * @param activity
             */
            public GT_AudioManager(Activity activity) {
                this.activity = activity;
                mAudioManager = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
            }//初始化

            /**
             * 获取 通话声音 最大值 与 当前通过的声音值
             *
             * @return
             */
            public int getVoiceCall() {
                current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                return current;
            }

            /**
             * 获取 音量操作类对象
             *
             * @return
             */
            public int getVoiceCallMax() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                return max;
            }

            /**
             * 设置音量大小
             *
             * @param value
             */
            public void setVoiceCallValue(int value) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value, AudioManager.FLAG_PLAY_SOUND);//设置 通话声音 音量大小为 0 静音
            }//设置 通话声音 的音量

            /**
             * 获取当前手机的声音值
             *
             * @return
             */
            public int getVoiceSystem() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                return current;
            }

            /**
             * 获取 系统音量 最大值
             *
             * @return
             */
            public int getVoiceSystemMax() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                return max;
            }

            /**
             * 设置系统音量值
             *
             * @param value
             */
            public void setVoiceSystemValue(int value) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, value, AudioManager.FLAG_PLAY_SOUND);//设置 通话声音 音量大小为 0 静音
            }//设置 系统音量 的音量

            /**
             * 获取 当前通过的声音值
             *
             * @return
             */
            public int getVoiceRing() {
                current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                return current;
            }

            /**
             * 铃声音量 最大值
             *
             * @return
             */
            public int getVoiceRingMax() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                return max;
            }

            /**
             * 设置 铃声音量 的音量
             *
             * @param value
             */
            public void setVoiceRingValue(int value) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_RING, value, AudioManager.FLAG_PLAY_SOUND);//设置 铃声音量 音量大小为 0 静音
            }//设置 铃声音量 的音量

            /**
             * 获取 当前通过的声音值
             *
             * @return
             */
            public int getVoiceMusic() {
                current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                return current;
            }

            /**
             * 获取 音乐音量(多媒体) 最大值
             *
             * @return
             */
            public int getVoiceMusicMax() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                return max;
            }

            /**
             * 设置 多媒体 的音量
             *
             * @param value
             */
            public void setMusicValue(int value) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_PLAY_SOUND);//设置多媒体音量大小为 0 静音
            }//设置 多媒体 的音量

            /**
             * 获取  当前通过的声音值
             *
             * @return
             */
            public int getVoiceAlarm() {
                current = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                return current;
            }

            /**
             * 获取 提示声音 音量 最大值
             *
             * @return
             */
            public int getVoiceAlarmMax() {
                max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                return max;
            }

            /**
             * 设置 提示声音 的音量
             *
             * @param value
             */
            public void setVoiceAlarmValue(int value) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, AudioManager.FLAG_PLAY_SOUND);//设置 铃声音量 音量大小为 0 静音
            }//设置 提示声音 的音量

            /**
             * 游戏过程中只允许调整多媒体音量，而不允许调整通话音量。
             */
            public void gemgMusiceNoSet() {
                activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            }//游戏过程中只允许调整多媒体音量，而不允许调整通话音量。

        }

        //ScreenListener 监听屏幕状态类
        public static class ScreenListener {
            /**
             * 监听
             * 手机屏幕点亮
             * 手机屏幕锁屏
             * 手机屏幕解锁
             * <p>
             * 使用实例:
             * GT.ScreenListener screenListener  = new GT.ScreenListener(this); //初始化 ScreenListener
             * screenListener.begin(new GT.ScreenListener.ScreenStateListener(){....} //new 一个匿名内部类 即可
             * 在销毁该 Activity 时一定要 调用该方法来注销广播
             * unregisterListener(); 方法来注销该广播
             */

            private Context context2;                                //联系上下文
            private ScreenBroadcastReceiver mScreenReceiver;        //定义一个广播
            private ScreenStateListener mScreenStateListener;       //定义个内部接口

            /**
             * 初始化
             */
            public ScreenListener(Context context) {
                this.context2 = context;
                mScreenReceiver = new ScreenBroadcastReceiver();//初始化广播
            }

            /**
             * 自定义接口
             */
            public interface ScreenStateListener {
                void onScreenOn();            //手机屏幕点亮

                void onScreenOff();            //手机屏幕锁屏

                void onUserPresent();        //手机屏幕解锁
            }

            /**
             * 获取screen的状态
             */
            private void getScreenState() {
                //初始化powerManager
                PowerManager manager = (PowerManager) context2.getSystemService(Context.POWER_SERVICE);
                if (manager.isScreenOn()) {   //如果监听已经开启
                    if (mScreenStateListener != null) {
                        mScreenStateListener.onScreenOn();
                    }
                } else {                      //如果监听没开启
                    if (mScreenStateListener != null) {
                        mScreenStateListener.onScreenOff();
                    }
                }
            }

            /**
             * 写一个内部的广播
             */
            private class ScreenBroadcastReceiver extends BroadcastReceiver {
                private String action = null;

                @Override
                public void onReceive(Context context, Intent intent) {
                    action = intent.getAction();
                    if (Intent.ACTION_SCREEN_ON.equals(action)) {        //屏幕亮时操作
                        mScreenStateListener.onScreenOn();
                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {   //屏幕关闭时操作
                        mScreenStateListener.onScreenOff();
                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {//解锁时操作
                        mScreenStateListener.onUserPresent();
                    }
                }
            }

            /**
             * 开始监听广播状态
             */
            public void begin(ScreenStateListener listener) {
                mScreenStateListener = listener;
                registerListener();                                //注册监听
                getScreenState();                                //获取监听
            }

            /**
             * 启动广播接收器
             */
            private void registerListener() {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON);            //屏幕亮起时开启的广播
                filter.addAction(Intent.ACTION_SCREEN_OFF);            //屏幕关闭时开启的广播
                filter.addAction(Intent.ACTION_USER_PRESENT);        //屏幕解锁时开启的广播
                context2.registerReceiver(mScreenReceiver, filter);    //发送广播

            }

            /**
             * 解除广播
             */
            public void unregisterListener() {
                context2.unregisterReceiver(mScreenReceiver); //注销广播
            }
        }

        //HeadsetPlugReceiver 监听耳机是否插入
        public static class GT_HeadsetPlugReceiver {

            /**
             * 监听 耳机
             * <p>
             * 使用实例:
             * GT.GT_HeadsetPlugReceiver gt_headsetPlugReceiver = new GT.GT_HeadsetPlugReceiver(this); //初始化 GT_HeadsetPlugReceiver
             * gt_headsetPlugReceiver.isHeadset_TF();    //获取当前耳机的状态  插入则返回 true 否则返回 false
             * 在销毁该 Activity 时一定要 调用该方法来注销广播
             * unregisterListener(); 方法来注销该广播
             */

            private Activity activity;
            private static boolean headset_TF;//定义耳机是否插入
            private HeadsetPlugReceiver headsetPlugReceiver;//监听手机是否有耳机插入广播

            /**
             * 检测是否插入耳机
             *
             * @return true则插入 false则未插入
             */
            public boolean isHeadset_TF() {
                registerHeadsetPlugReceiver();
                return headset_TF;
            }

            /**
             * 实例化 耳机监听
             *
             * @param activity
             */
            public GT_HeadsetPlugReceiver(Activity activity) {
                this.activity = activity;
            }

            /**
             * /注销广播
             */
            public void unregisterListener() {
                activity.unregisterReceiver(headsetPlugReceiver); //注销广播
            }

            /**
             * 注册 广播
             */
            public void registerHeadsetPlugReceiver() {
                headsetPlugReceiver = new HeadsetPlugReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.HEADSET_PLUG");
                activity.registerReceiver(headsetPlugReceiver, intentFilter);
            }

            /**
             * 内部类
             */
            private static class HeadsetPlugReceiver extends BroadcastReceiver {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra("state")) {
                        if (intent.getIntExtra("state", 0) == 0) {
                            if (LOG.GT_LOG_TF)
                                headset_TF = false;
                        } else if (intent.getIntExtra("state", 0) == 1) {
                            if (LOG.GT_LOG_TF)
                                headset_TF = true;
                        }
                    }
                }
            }

        }

        //Spiritleve 屏幕旋转监听
        public abstract static class Spiritleve implements SensorEventListener {
            /**
             * 用法如下：
             * //屏幕旋转监听 内部类
             * class SV extends SpiritleveView{
             * *
             * public SV(Context context) {
             * super(context);
             * }
             * *
             *
             * @Override protected void getPosition(float xAngle, float yAngle) {
             * super.getPosition(xAngle, yAngle);
             * GT.log("X:" + (int)xAngle + "," + "Y:" + (int)yAngle);
             * }
             * }
             * *
             * 最后再在方法中初始化
             * new SV(activity);
             * *
             */
            float[] acceleromterValues = new float[3];//加速度传感器的值
            float[] magneticValues = new float[3];//磁场传感器的值

            //动作定义 常量
            public static final int LIE_LOW = 0;         //平躺
            public static final int SLEEPER = 1;         //卧铺
            public static final int STAND_RIGHT = 2;     //右立;
            public static final int LEFT_STANDING = 3;   //左立
            public static final int STAND = 4;           //站立
            public static final int HANDSTAND = 5;       //倒立

            public static final int ZHP = 1;            //正横屏
            public static final int FHP = -1;           //反横屏
            public static final int ZSP = 2;            //正竖屏
            public static final int FSP = -2;           //反竖屏

            private SensorManager sensorManager;       //定义取消屏幕监听

            public Spiritleve(Context context) {
                sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);//获取传感器管理器
                //为磁场传感器注册监听器
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
                //为加速度传感器注册监听器
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            }

            @Override
            public void onSensorChanged(SensorEvent event) {    //值改变触发

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    acceleromterValues = event.values.clone();//获取加速度传感器的值
                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticValues = event.values.clone();//获取磁场传感器的值
                }

                float[] R = new float[9];//保存旋转数据的数组
                float[] values = new float[3];//保存方向数据的数组

                SensorManager.getRotationMatrix(R, null, acceleromterValues, magneticValues);//获得一个包含旋转矩阵的数组
                SensorManager.getOrientation(R, values);//获取方向值

                float xAngle = (float) Math.toDegrees(values[1]);//x轴旋转角度
                float yAngle = (float) Math.toDegrees(values[2]);//y轴旋转角度

                getPosition(xAngle, yAngle);//获取小球的位置坐标

            }

            //根据X轴和Y轴的旋转角度确定小篮球的位置
            protected void getPosition(float xAngle, float yAngle) {
                            /*
                                这里会返回具体的手机位置信息
                                使用 getMobilePosition 或 getScreenPosition 方法判定 当前手机的位置
                             */
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //精度改变触发
            }

            /**
             * 获取当前手机位置
             * 手机仰天躺(LIE_LOW)、手机卧铺躺(SLEEPER)、手机右立(STAND_RIGHT)、
             * 手机左立(LEFT_STANDING)、手机站起(STAND)、手机倒立(HANDSTAND)
             */
            public int getMobilePosition(float xAngle, float yAngle) {
                int X = (int) xAngle;
                int Y = (int) yAngle;
                if ((X >= -10 && X <= 10) && (Y >= -10 && Y <= 10)) {//手机仰天躺
                    return LIE_LOW;
                } else if ((X >= -10 && X <= 10) && (Y >= 170 && Y <= 179)) {//手机卧铺躺
                    return SLEEPER;
                } else if ((X >= 0 && X <= 2) && (Y >= 11 && Y <= 90)) {//手机右立
                    return STAND_RIGHT;
                } else if ((X >= 0 && X <= 2) && (Y <= -1 && Y >= -90)) {//手机左立
                    return LEFT_STANDING;
                } else if (X <= -50 && X >= -90) {//手机站起
                    return STAND;
                } else if ((X >= 50 && X <= 90)) {//手机倒立
                    return HANDSTAND;
                }
                return SLEEPER;//默认仰天躺
            }

            /**
             * 获取当前手机屏幕位置
             * 正横屏(ZHP.1)、反横屏(FHP.-1)、竖屏(SP.0)
             */
            public int getScreenPosition(float xAngle, float yAngle) {
                int X = (int) xAngle;
                int Y = (int) yAngle;

                if ((X >= -30 && X <= 0) && (Y <= -40 && Y >= -179)) {
                    return ZHP;
                } else if ((X >= -30 && X <= 0) && (Y >= 40 && Y <= 179)) {
                    return FHP;
                } else if ((X >= -90 && X <= -10) && (Y >= -39 && Y <= 179)) {
                    return ZSP;
                } else if ((X <= 90 && X >= 10) && (Y <= 39 && Y >= -179)) {
                    return FSP;
                }
                return -181;//默认正横屏
            }

            public void close() {
                sensorManager.unregisterListener(this);//取消注册的屏幕监听
            }

        }


    }


    //========================================== 线程 ==============================================

    //Thread 更新UI线程
    public static class Thread {

        /**
         * 更新 主线程 UI
         *
         * @param runnable
         */
        public static void runAndroid(Runnable runnable) {
            Looper.prepare();
            new Handler().postDelayed(runnable, 0);
            Looper.loop();
        }

        /**
         * 更新 主线程 UI
         *
         * @param runnable
         */
        public static void runAndroidAct(Runnable runnable) {
            if (getGT().getActivity() != null) {
                Activity activity = (Activity) getGT().getActivity();
                activity.runOnUiThread(runnable);
            } else {
                log(getLineInfo(), "当前未绑定 Activity 无法使用该方法创建 UI 线程");
            }
        }

        /**
         * @param runnable  run
         * @param sleepTime 延时时间
         * @主线程
         */
        public static void runAndroid(Runnable runnable, int sleepTime) {
            Looper.prepare();
            new Handler().postDelayed(runnable, sleepTime);
            Looper.loop();
        }

        /**
         * 更新 主线程 UI
         *
         * @param runnable
         */
        public static void runAndroid(View view, Runnable runnable) {
            view.post(runnable);
        }

        /**
         * 更新 主线程 UI
         *
         * @param runnable
         */
        public static void runAndroid(Activity activity, Runnable runnable) {
            activity.runOnUiThread(runnable);
        }

        /**
         * 开启 Java 子线程
         *
         * @param runnable
         */
        public static void runJava(Runnable runnable) {
            //注意：如果你在引用这个线程里引用了主线程的 对象 请在 run 方法中 加入 Looper.prepare(); 否则会在开始或结束Activity活动时 报异常
            new java.lang.Thread(runnable).start();
        }

        /**
         * 睡眠
         *
         * @param millis
         */
        public static void sleep(long millis) {
            try {
                java.lang.Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        /**
         * 循环 计时器
         *
         * @param delay     多少秒过后进行 开始计时器
         * @param period    每隔多少毫秒进行一次计时
         * @param timerTask 匿名类 new TimerTask 即可 然后在 run 方法中写耗时操作
         * @return
         */
        public static Timer Timer(long delay, long period, TimerTask timerTask) {
            /**
             * 参数 delay : 待多少秒过后进行 开始计时器
             * 参数 period : 每隔多少毫秒进行一次计时
             * 参数 timerTask : 匿名类 new TimerTask 即可 然后在 run 方法中写耗时操作
             */
            Timer timer = new Timer();
            timer.schedule(timerTask, delay, period);
            return timer;
        }

        /**
         * 简易 循环计时器
         *
         * @param timerTask
         * @return
         */
        public static Timer Timer(TimerTask timerTask) {
            /**
             * 参数 timerTask : 匿名类 new TimerTask 即可 然后在 run 方法中写耗时操作
             */
            Timer timer = new Timer();
            timer.schedule(timerTask, 0, 1);
            return timer;
        }

        /**
         * Timer 整体封装
         */
        public static class GT_Timer {

            private Timer timer;
            private TimerTask timerTask;

            /**
             * 初始化
             *
             * @param timerTask
             */
            public GT_Timer(TimerTask timerTask) {
                this.timerTask = timerTask;
                timer = new Timer();
            }

            /**
             * 开启循环计时
             *
             * @param delay
             * @param period
             * @return
             */
            public GT_Timer start(long delay, long period) {
                if (timer != null && timerTask != null) {
                    timer.schedule(timerTask, delay, period);
                }
                return this;
            }

            /**
             * 启动
             *
             * @return
             */
            public GT_Timer start() {
                if (timer != null && timerTask != null) {
                    timer.schedule(timerTask, 0, 1);
                }
                return this;
            }

            /**
             * 结束循环计时
             */
            public void close() {
                if (timer != null) {
                    timer.cancel();//停止计时
                    timer = null;
                    timerTask = null;
                }
            }


        }


        /**
         * AsyncTask 封装
         *
         * @param gtAsyncTask
         * @return
         */
        public static GTAsyncTask asyncTask(GTAsyncTask gtAsyncTask) {
            return gtAsyncTask;
        }

        /**
         * 自动开启的 AsyncTask 封装
         *
         * @param start
         * @param gtAsyncTask
         * @return
         */
        public static GTAsyncTask asyncTask(boolean start, GTAsyncTask gtAsyncTask) {
            if (start)
                gtAsyncTask.execute();//如果设置为
            return gtAsyncTask;
        }

        /**
         * AsyncTask 整体封装
         */
        public static class AsyncTask {
            /**
             * 使用实列:    GT.Thread.AsyncTask asyncTask = new GT.Thread.AsyncTask(new GT.Thread.GTAsyncTask(){....}
             */
            private GTAsyncTask gtAsyncTask;    //定义 GTAsyncTask

            /**
             * 获取 GTAsyncTask 对象
             *
             * @return
             */
            public GTAsyncTask getGtAsyncTask() {
                return gtAsyncTask;
            }

            /**
             * 初始化 GTAsyncTask
             *
             * @param gtAsyncTask
             */
            public AsyncTask(GTAsyncTask gtAsyncTask) {
                this.gtAsyncTask = gtAsyncTask;
            }

            //启动 GTAsyncTask
            public void start() {
                if (gtAsyncTask != null) {
                    try {
                        gtAsyncTask.execute();
                    } catch (IllegalStateException e) {
                        if (LOG.GT_LOG_TF) {
                            GT.log(getLineInfo(1), "无法执行任务:任务已在运行。");
                        }
                    }
                }
            }

            /**
             * 关闭 GTAsyncTask 并释放内存
             */
            public void close() {
                if (gtAsyncTask != null) {
                    gtAsyncTask.cancel(true);//强制关闭
                    gtAsyncTask = null;//置空
                }
            }

        }

        /**
         * 定义继承后要实现的类
         */
        public abstract static class GTAsyncTask extends android.os.AsyncTask<Object, Object, Object> {

            /**
             * 用法：继承该类并重写，或者利用 AsyncTask 封装类进行便捷操作
             * onPreExecute 用于初始化
             * onProgressUpdate 用于更新 UI 界面
             * doInBackground 用于进行耗时操作如网络请求、、
             * onPostExecute 用于反馈耗时完成、或者进行资源释放
             */

            @Override
            protected void onPreExecute() {
                //初始化
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                //更新 UI
                super.onProgressUpdate(values);
            }

            @Override
            protected Object doInBackground(Object... objects) {
                //阻塞操作 该方法并不在 主线程中调用，不能用于更新 UI 操作
                return null;
            }

            @Override
            protected void onPostExecute(Object object) {
                //在主线程中调用该方法，可以对 UI 进行修改
                super.onPostExecute(object);
            }
        }


    }


    //============================================ 随机类 ===============================================

    /**
     * 随机类
     */
    public static class GT_Random {
        private Random random;

        /**
         * 实例化 随机类 类
         */
        public GT_Random() {
            random = new Random();
        }

        /**
         * 获取一个未知整数
         *
         * @return int
         */
        public int getInt() {
            int min = -2147483648;
            int max = 2147483647;
            return random.nextInt(max) % (max - min + 1) + min;
        }

        /**
         * 获取随机范围的数
         *
         * @param min
         * @param max
         * @return
         */
        public int getInt(int min, int max) {
            return random.nextInt(max) % (max - min + 1) + min;
        }

    }
}