package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.StoryBean;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Manish on 02-07-2017.
 */

public class StoryBll {

    public Context context;

    public StoryBll(Context context) {
        this.context = context;
    }

    public void verify(StoryBean storyBean) {
        String sql = "";
        Mydb dbHelper = null;
        Cursor cursor;
        try {
            sql = "SELECT id FROM story where id=" + storyBean.id;
            System.out.println("=====sql====" + sql);
            dbHelper = new Mydb(context);
            cursor = dbHelper.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                updateStory(storyBean);
            } else {
                insertStory(storyBean);
            }
        } catch (Exception e) {

        }
    }

    public void insertStory(StoryBean storyBean) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO story VALUES (" + storyBean.id + "," + storyBean.userid + ",'" + storyBean.story_image + "','" + StringEscapeUtils.unescapeJava(storyBean.caption) + "','" + storyBean.create_time + "',0)";
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: insert()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public void updateStory(StoryBean storyBean) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "update story set userid= " + storyBean.userid + ", image= '" + storyBean.story_image + "',caption='" + StringEscapeUtils.unescapeJava(storyBean.caption) + "',time='" + storyBean.create_time + "' where id=" + storyBean.id;
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: update()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }


    public ArrayList<StoryBean> getStoryListWithGroup() {

        DeleteOldStory();

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
                    storyBean.userid = cursor.getInt(0);
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
                if (storyBeanArrayList.get(i).userid == storyBeanArrayList.get(j).userid) {
                    storyBeanArrayList.remove(j);
                    getGroupList(storyBeanArrayList);
                }
            }
        }

        Log.print("===storyBeanArrayList===" + storyBeanArrayList.size());
        return storyBeanArrayList;
    }

    public ArrayList<StoryBean> getStoryListByUserId(int userId) {
        ArrayList<StoryBean> storyBeanArrayList = null;
        Mydb dbHelper = null;
        Cursor cursor = null;
        StoryBean storyBean;
        String sql;

        try {
            sql = "SELECT * FROM story WHERE userid = " + userId + " order by time desc";
            dbHelper = new Mydb(context);
            cursor = dbHelper.query(sql);

            storyBeanArrayList = new ArrayList<StoryBean>();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    storyBean = new StoryBean();
                    storyBean.id = cursor.getInt(0);
                    storyBean.userid = cursor.getInt(1);
                    storyBean.story_image = cursor.getString(2);
                    storyBean.caption = cursor.getString(3);
                    storyBean.create_time = getDiff(cursor.getString(4));
                    storyBeanArrayList.add(storyBean);
                }
            }

        } catch (Exception e) {
            Log.print(this.getClass() + " :: getStatus()" + e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();

            if (dbHelper != null)
                dbHelper.close();

        }

        return storyBeanArrayList;

    }

    public String getDiff(String time) {
        Log.print("=======time========" + time);
        String datediff = " ";
        try {

            long diff = new Date().getTime() - Config.WebDateFormatter.parse(time).getTime();
            Log.print("======diff=====" + diff);
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            minutes = minutes - (hours * 60);
            seconds = seconds - (hours * 60 * 60);
            if (hours >= 1) {
                datediff = hours + "h ";
                datediff = datediff + minutes + "m";
            } else {
                datediff = datediff + minutes + "m ";
                datediff = datediff + seconds + "s";
            }


            Log.print("=======datediff=======" + datediff);
            return datediff;
        } catch (Exception e) {

        }
        return datediff;
    }

    public void DeleteOldStory() {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "delete from story where time < '" + get24HDate() + "'";
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: delete()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public String get24HDate() {
        Date date = new Date();
        Date dateBefore = new Date(date.getTime() - (24 * 3600 * 1000));
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateBefore);
    }

}
