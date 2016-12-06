package com.hudongwx.test.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hudongwx.test.instance.History;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hudongwx on 16-11-19.
 */
public class SqliteUtil {
   MySqliteHelper mySqliteHelper;
    static SqliteUtil util;
    private SqliteUtil(Context context){
       mySqliteHelper = new MySqliteHelper(context);
    }
    public static  SqliteUtil getInstance(Context context){
        if (null==util){
       util= new SqliteUtil(context);
        }
        return  util;
    }

    public long insert(History history,String tableName){
        if (urlJudge(history.getUrl(),tableName)){

            return -1;
        }
        ContentValues values =new ContentValues();
        values.put("name",history.getName());
        values.put("url",history.getUrl());
        values.put("date",history.getDate());
        SQLiteDatabase database=mySqliteHelper.getWritableDatabase();
        return database.insert(tableName,null,values);
    }
    public int del(String url,String tableName){
        SQLiteDatabase database=mySqliteHelper.getWritableDatabase();
       return database.delete(tableName,"url=?",new String[]{url});
    }
    //只提供修改名字的方法
    public int update(String url,String newName,String tableName){
        ContentValues values =new ContentValues();
        values.put("name",newName);
        SQLiteDatabase database=mySqliteHelper.getWritableDatabase();
        return database.update(tableName,values,"url=?",new String[]{url});
    }
    public List<History> query(String tableName){
        SQLiteDatabase database=mySqliteHelper.getReadableDatabase();
        Cursor cursor = database.query(tableName, new String[]{"name", "url", "date"}, null, null, null, null, "date desc");
        List<History> histories=new ArrayList<>();
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex("name"));
            String url=cursor.getString(cursor.getColumnIndex("url"));
            String date=cursor.getString(cursor.getColumnIndex("date"));
            histories.add(new History(name,url,date));
        }
        cursor.close();
        return histories;

    }
    public boolean urlJudge(String url,String tableName){
        SQLiteDatabase database=mySqliteHelper.getReadableDatabase();
        Cursor cursor = database.query(tableName, new String[]{"url"}, "url=?", new String[]{url}, null, null, null);
        return cursor.moveToNext();

    }
    public void deleteAll(){
        SQLiteDatabase database=mySqliteHelper.getReadableDatabase();
        database.delete("history",null,null);
    }


}
