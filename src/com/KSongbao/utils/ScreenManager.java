package com.KSongbao.utils;

import java.util.Stack;

import com.KSongbao.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;



/**
 * Activity 管理类
 */
public class ScreenManager {
    private static Stack<Activity> activityStack;
    private static ScreenManager instance;

    private ScreenManager() {
    }

    /**
     * 弹出所有activy，调到登录页
     */
    public void goLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
        popAll();
    }

    /**
     * 单例的构造方法
     *
     * @return
     */
    public static ScreenManager getScreenManager() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * 弹出最后一个Activity
     */
    public void popActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 弹出制定Activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获取当前的Activity
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 入栈一个Activity
     *
     * @param activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 弹出所有的Activity除了参数的那一个
     *
     * @param cls
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 弹出所有的Activity
     *
     * @param
     */
    public void popAll() {
        while (!activityStack.isEmpty()) {
            popActivity(currentActivity());
        }
    }
}
