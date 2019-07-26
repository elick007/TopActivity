package com.mrcn.findmainact;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final long beginTime = System.currentTimeMillis();
    public final static String BEGIN_TIME = "begin_time";
    private TextView explain;
    private Button getPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.mrcn.findmainact.mybroadcast");
        registerReceiver(myBroadcastReceiver, intentFilter);

        Intent intent = new Intent("com.mrcn.findmainact.mybroadcast");
        //intent.putExtra("click",1000);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "1000")
                .setSmallIcon(R.drawable.mr_user_center_mine)
                .setContentTitle("点击开始获取mainActivity")
                .setContentText("请保证当前已弹出猫耳登录框或者是登录状态")
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1000, notification);
        getSharedPreferences("sharedpf", MODE_PRIVATE).edit().putLong(BEGIN_TIME, beginTime).apply();

        explain = findViewById(R.id.explain);
        getPermission = findViewById(R.id.get_permission);
        //build 22获取权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (!hasUsageStatPermission((UsageStatsManager) getSystemService(USAGE_STATS_SERVICE))){
                getPermission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent statsIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivityForResult(statsIntent, 1000);
                    }
                });
            }else {
                getPermission.setVisibility(View.GONE);
                explain.setText("打开游戏\n确保游戏已打开猫耳登录界面或已登录猫耳账号\n下拉状态栏点击获取主activity");
            }
        }else {
            explain.setText("打开游戏\n确保游戏已打开猫耳登录界面或已登录猫耳账号\n下拉状态栏点击获取主activity");
        }

//upgradeRootPermission(getPackageCodePath());

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean hasUsageStatPermission(UsageStatsManager usageStatsManager){
        long ts = System.currentTimeMillis();
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        return usageStats!=null&&usageStats.size()!=0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP) {
            if (hasUsageStatPermission((UsageStatsManager) getSystemService(USAGE_STATS_SERVICE))) {
                Log.d("mr_sdk_cn","has permission");
                explain.setText("打开游戏\n确保游戏已打开猫耳登录界面或已登录猫耳账号\n下拉状态栏点击获取主activity");
                getPermission.setVisibility(View.GONE);
            }else {
                Log.d("mr_sdk_cn","no permission");
            }
        }
    }

    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mr_sdk_cn", "click");
//           boolean isroot=EasyProtectorLib.checkIsRoot();
//           Log.d("mr_sdk_cn","isroot: "+isroot);
//           if (isroot){
//               try {
//                   Runtime runtime=Runtime.getRuntime();
//                   Process proc=runtime.exec("dumpsys activity top");
//                   InputStream inputStream=proc.getInputStream();
//                   InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
//                   BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
//                   String line="";
//                   StringBuilder stringBuilder=new StringBuilder();
//                   while ((line=bufferedReader.readLine())!=null){
//                       stringBuilder.append(line);
//                   }
//                   if (proc.waitFor()==0){
//                       Log.d("mr_sdk_cn","exec result:\n"+stringBuilder.toString());
//                   }else {
//                       Log.d("mr_sdk_cn","run shell error");
//                   }
//                   inputStream.close();
//                   inputStreamReader.close();
//                   bufferedReader.close();
//               } catch (IOException e) {
//                   e.printStackTrace();
//               } catch (InterruptedException e) {
//                   e.printStackTrace();
//               }
//           }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                handle22(context);
            } else {
                handle(context);
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        private void handle22(Context context) {
            long ts = System.currentTimeMillis();
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedpf", MODE_PRIVATE);
            long beginTime = sharedPreferences.getLong(BEGIN_TIME, System.currentTimeMillis() - 1000 * 60 * 5);
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, ts);
            String mainAct="";
            if (usageStats == null || usageStats.size() == 0) {
                Log.d("mr_sdk_cn", "usageStats none");
            } else {
                Collections.sort(usageStats, new RencentUseCompara());//mRecentComp = new RecentUseComparator()
                mainAct=usageStats.get(0).getPackageName();
                Log.d("mr_sdk_cn",mainAct);
            }
            handleCopy(context,mainAct);
        }

        private void handle(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ComponentName componentName = activityManager != null ? activityManager.getRunningTasks(1).get(0).topActivity : null;
            Log.d("mr_sdk_cn", "mainActivity: " + componentName.getClassName());
            handleCopy(context,componentName.getClassName());
        }

        private void handleCopy(Context context,String mainAct){
            if (TextUtils.isEmpty(mainAct)||mainAct.equals("com.mrcn.findmainact")){
                Toast.makeText(context,"还没打开游戏呐",Toast.LENGTH_SHORT).show();
            }else {
                ClipboardManager clipboardManager= (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("mainAct",mainAct);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context,mainAct+"\n已复制主activity到剪切板",Toast.LENGTH_LONG).show();
            }
        }
    }

    static class RencentUseCompara implements Comparator<UsageStats> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats lhs, UsageStats rhs) {
            return Long.compare(rhs.getLastTimeUsed(), lhs.getLastTimeUsed());
        }
    }
}
