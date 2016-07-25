package com.onpuri;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-04-05.
 */
public class ActivityList {
    //Singleton
    public static final ActivityList activityList = new ActivityList();
    private ArrayList<Activity> listActivity = null;

    private ActivityList()
    {
        listActivity = new ArrayList<Activity>();
    }

    public static ActivityList getInstance()
    {
        return activityList;
    }

    //custom method
    //add activity
    public void addActivity(Activity activity)
    {
        listActivity.add(activity);
    }

    //remove activity
    public boolean removeActivity(Activity activity)
    {
        return listActivity.remove(activity);
    }

    //all activity finish
    public void finishAllActivity()
    {
        for(Activity activity : listActivity)
        {
            activity.finish();
        }
    }

    //Getter, Setter
    public ArrayList<Activity> getListActivity()
    {
        return listActivity ;
    }

    public void setListActivity(ArrayList<Activity> listActivity)
    {
        this.listActivity = listActivity ;
    }
}
