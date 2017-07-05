package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.StoryBean;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;

import java.util.ArrayList;

/**
 * Created by Manish on 02-07-2017.
 */

public class StoryBll {

    public Context context;

    public StoryBll(Context context) {
        this.context = context;
    }


    public ArrayList<StoryBean> getStoryListWithGroup() {
        ArrayList<StoryBean> storyBeanArrayList = new ArrayList<StoryBean>();
        Mydb dbHelper = null;
        Cursor cursor = null;
        StoryBean storyBean;
        String sql;

        try {
            sql = "SELECT story.userid,user_tb.name,user_tb.avatar FROM story join user_tb on story.userid=user_tb.userid order by time desc";
            System.out.println("=====sql====" + sql);
            dbHelper = new Mydb(context);
            cursor = dbHelper.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    storyBean = new StoryBean();
                    storyBean.userId = cursor.getInt(0);
                    storyBean.userName = cursor.getString(1);
                    storyBean.userAvatar = cursor.getString(2);
                    storyBeanArrayList.add(storyBean);
                }
            }

        } catch (Exception e) {
            Log.print(this.getClass() + " :: getStory()" + e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();

            if (dbHelper != null)
                dbHelper.close();

        }


        return getGroupList(storyBeanArrayList);

    }

    public ArrayList<StoryBean> getGroupList(ArrayList<StoryBean> storyBeanArrayList) {


        for (int i = 0; i < storyBeanArrayList.size(); i++) {
            for (int j = i + 1; j < storyBeanArrayList.size(); j++) {
                if (storyBeanArrayList.get(i).userId == storyBeanArrayList.get(j).userId) {
                    storyBeanArrayList.remove(j);
                    getGroupList(storyBeanArrayList);
                }
            }
        }

        Log.print("===storyBeanArrayList===" + storyBeanArrayList.size());
        return storyBeanArrayList;
    }

}
