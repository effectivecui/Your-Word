package com.micsay.yourword.beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.micsay.yourword.database.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuicui on 17-3-22.
 */

public class Mean implements Parcelable{
    final String type;
    final String content;

    private Mean(String type, String content){
        this.type = type;
        this.content = content;
    }

    public static Mean newInstance(String type, String content){
        return new Mean(type, content);
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public static List<Mean> findAll(int vocabulary_id){
        List<Mean> mList = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String sql = "SELECT * FROM mean WHERE vocabulary_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(vocabulary_id)});

        Mean mTemp = null;
        Log.d("Database: ", String.valueOf(vocabulary_id));
        while(cursor.moveToNext()){
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            mTemp = new Mean(type, content);
            Log.d("Database: ", type+content);
            mList.add(mTemp);
        }
        return mList;
    }
    public static void add(Mean mean, int vocabulary_id){
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        String sql = "INSERT INTO mean (type, content, vocabulary_id) VALUES(?, ?, ?)";
        Runnable runnable = ()->{
            db.execSQL(sql, new String[]{
                    mean.getType(),
                    mean.getContent(),
                    String.valueOf(vocabulary_id)
            });
        };
        runnable.run();
    }

    public static void remove(int vocabulary_id){
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        String sql = "DELETE FROM mean WHERE vocabulary_id = ?";
        Runnable runnable = ()->{
            db.execSQL(sql, new String[]{String.valueOf(vocabulary_id)});
        };
        runnable.run();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(content);
    }

    public static final Creator<Mean> CREATOR = new Creator<Mean>(){

        @Override
        public Mean createFromParcel(Parcel parcel) {
            String type = parcel.readString();
            String content = parcel.readString();
            return new Mean(type, content);
        }

        @Override
        public Mean[] newArray(int i) {
            return new Mean[i];
        }
    };
}
