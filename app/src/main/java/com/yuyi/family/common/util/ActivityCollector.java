package com.yuyi.family.common.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.LinkedList;

public class ActivityCollector {
    public static LinkedList<Activity> activities = new LinkedList<Activity>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAll()
    {
        for(Activity activity:activities)
        {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public static void AppExit(Context context) {
        try {
            ActivityCollector.finishAll();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
//            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception ignored) {}
    }
}
