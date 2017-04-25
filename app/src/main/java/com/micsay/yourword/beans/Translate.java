package com.micsay.yourword.beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.micsay.yourword.database.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuicui on 17-3-26.
 */

public class Translate {
    private static Map<String, Translate> tMap = new HashMap<>();
    private String query;
    private int errorCode;
    private Basic basic;

    public Translate(String query, Basic basic){
        this.query = query;
        this.basic = basic;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public String toString(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Your query: ");
        stringBuffer.append(query);

        if(basic != null)
            stringBuffer.append(basic.toString());
        else
            stringBuffer.append("\nNo explains");

        return stringBuffer.toString();
    }
    public static class Basic {
        String phonetic;
        List<String> explains = new ArrayList<>();

        public Basic(String phonetic, List<String> explains){
            this.phonetic = phonetic;
            this.explains = explains;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }

        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append("\nPhonetic: ");
            sb.append(phonetic);
            sb.append("\nExplains: ");
            for(String s : explains){
                sb.append("\n");
                sb.append(s);
            }
            return sb.toString();
        }
    }

    private static void init(){

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        String sql = "SELECT * FROM translate;";
        Cursor cursorTranslate = db.rawQuery(sql, null);

        while(cursorTranslate.moveToNext()){
            int id = cursorTranslate.getInt(cursorTranslate.getColumnIndex("id"));
            String query = cursorTranslate.getString(cursorTranslate.getColumnIndex("query"));
            String phonetic = cursorTranslate.getString(cursorTranslate.getColumnIndex("phonetic"));
            List<String> explains = new ArrayList<>();
            sql = "SELECT * FROM explains WHERE translate_id = ?";
            Cursor cursorExplains = db.rawQuery(sql, new String[]{String.valueOf(id)});
            while(cursorExplains.moveToNext()){
                explains.add(cursorExplains.getString(cursorExplains.getColumnIndex("explain")));
            }
            tMap.put(query, new Translate(query, new Basic(phonetic, explains)));
        }
        Log.d("TRANSLATE", String.valueOf(tMap.size()));
    }

    public static Translate find(String query){
        if(tMap.size() <= 0)init();
        query = query.toLowerCase();

        if(tMap.containsKey(query)){
            return tMap.get(query);
        }

        return null;
    }

    private static int getTranslateId(String query){
        int result = -1;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        String sql = "SELECT * FROM translate WHERE query = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{query});
        if(cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("id"));
        }

        return result;
    }

    public static void add(Translate translate){
        if(translate == null) return;

        tMap.put(translate.query, translate);

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        try{
            String sql = "INSERT INTO translate (query, phonetic) VALUES (?, ?);";
            db.execSQL(sql, new String[]{translate.query, translate.basic.phonetic});

            int translateId = getTranslateId(translate.query);

            sql = "INSERT INTO explains (translate_id, explain) VALUES (?, ?);";
            for(String s : translate.basic.explains){
                db.execSQL(sql, new String[]{String.valueOf(translateId), s});
            }
        }catch (Exception e){

        }

    }
}
