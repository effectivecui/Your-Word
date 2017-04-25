package com.micsay.yourword.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.micsay.yourword.MyApplication;
import com.micsay.yourword.beans.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuicui on 17-3-22.
 */

public class DbHelper extends SQLiteOpenHelper{
    private Context context;
    private static DbHelper instance;
    private static final String CREATE_VOCABULARY = "CREATE TABLE vocabulary ("
            + "id integer primary key autoincrement, "
            + "en_word text unique, "
            + "image_uri text, "
            + "updated_at integer);";

    private static final String CREATE_MEAN = "CREATE TABLE mean ("
            + "id integer primary key autoincrement, "
            + "vocabulary_id int, "
            + "type text, "
            + "content text, "
            + "FOREIGN KEY(vocabulary_id) REFERENCES vocabulary(id));";

    private static final String CREATE_TRANSLATE = "CREATE TABLE translate ("
            + "id integer primary key autoincrement, "
            + "query text unique,"
            + "phonetic text);";

    private static final String CREATE_EXPLAINS = "CREATE TABLE explains ("
            + "id integer primary key autoincrement, "
            + "translate_id integer, "
            + "explain text, "
            + "FOREIGN KEY(translate_id) REFERENCES translate(id));";

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }
    public static SQLiteOpenHelper getInstance(){
        if(instance == null){
            synchronized (DbHelper.class){
                if(instance == null){
                    instance = new DbHelper(MyApplication.getContext(), "YourWord.db", null, 8);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_MEAN);
        db.execSQL(CREATE_VOCABULARY);
        db.execSQL(CREATE_TRANSLATE);
        db.execSQL(CREATE_EXPLAINS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists Vocabulary");
        db.execSQL("drop table if exists Mean");
        db.execSQL("drop table if exists Translate");
        db.execSQL("drop table if exists Explains");
        onCreate(db);
    }



}
