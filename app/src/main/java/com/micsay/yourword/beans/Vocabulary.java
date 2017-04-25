package com.micsay.yourword.beans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.micsay.yourword.database.DbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cuicui on 17-3-20.
 */

public class Vocabulary implements Parcelable{
    private static List<Vocabulary> vList = new ArrayList<>();
    private final String enWord;
    private List<Mean> cnList;
    private final long updated_at;

    private String imageUri;

    private Vocabulary(String enWord, List<Mean> cnList, long updated_at){
        this.enWord = enWord;
        this.imageUri = "";
        this.cnList = cnList;
        this.updated_at = updated_at;
    }

    public static Vocabulary newInstance(String enWord, Map<String, String> cnWord){
        return new Vocabulary(enWord, mapToList(cnWord), System.currentTimeMillis());
    }
    private static List<Mean> mapToList(Map<String, String> cnWord){
        List<Mean> list = new ArrayList<>();
        for(Map.Entry<String, String> entry : cnWord.entrySet()){
            String v = entry.getValue();
            if(!"".equals(v)){
                list.add(Mean.newInstance(entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }
    public List<Mean> getCnList() {
        return cnList;
    }

    public void setCnList(Map<String, String> cnWord) {
        this.cnList = mapToList(cnWord);
    }

    public String getEnWord() {
        return enWord;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public long getUpdated_at(){
        return this.updated_at;
    }
    public String toString(){
        return "Vocabulary";
    }
    public static List<Vocabulary> findAll(){
        if(vList.size() > 0)return vList;

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String sql = "SELECT * FROM vocabulary ORDER BY updated_at DESC";
        Cursor cursor = db.rawQuery(sql, null);
        Vocabulary vTemp = null;
        List<Mean> mList = null;
        while(cursor.moveToNext()){
            String enWord = cursor.getString(cursor.getColumnIndex("en_word"));
            String imageUri = cursor.getString(cursor.getColumnIndex("image_uri"));
            long updated_at = cursor.getLong(cursor.getColumnIndex("updated_at"));
            int vocabulary_id = cursor.getInt(cursor.getColumnIndex("id"));
            mList = Mean.findAll(vocabulary_id);
            vTemp = new Vocabulary(enWord, mList, updated_at);
            vTemp.setImageUri(imageUri);
            vList.add(vTemp);
        }
        return vList;
    }

    private static int getVocabularyId(Vocabulary vocabulary){
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String sql = "SELECT id FROM vocabulary WHERE en_word = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{vocabulary.getEnWord()});
        int id = -1;
        if(cursor.moveToFirst()){
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }
    public static void remove(Vocabulary vocabulary){
        if(vocabulary==null)return;
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        Mean.remove(getVocabularyId(vocabulary));

        String sql = "DELETE FROM vocabulary WHERE en_word = ?";
        db.execSQL(sql, new String[]{vocabulary.getEnWord()});

        for(int i=0, size=vList.size(); i<size; i++){
            Vocabulary temp = vList.get(i);
            if(temp.getEnWord().equals(vocabulary.getEnWord())){
                vList.remove(i);
                break;
            }
        }
    }

    public static void add(Vocabulary vocabulary){
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        String sql = "INSERT INTO vocabulary (en_word, image_uri, updated_at) VALUES(?, ?, ?)";

        try{
            db.execSQL(sql, new String[]{
                    vocabulary.getEnWord(),
                    vocabulary.getImageUri(),
                    String.valueOf(vocabulary.getUpdated_at())
            });
            List<Mean> mList = vocabulary.getCnList();
            int vocabulary_id = getVocabularyId(vocabulary);
            for(Mean m : mList){
                Mean.add(m, vocabulary_id);
            }
            vList.add(0, vocabulary);

        }catch (Exception e){
            Log.e("SQLITE", "sql unique error");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(enWord);
        parcel.writeList(cnList);
        parcel.writeLong(updated_at);
        parcel.writeString(imageUri);
    }
    public static final Creator<Vocabulary> CREATOR = new Creator<Vocabulary>(){
        @Override
        public Vocabulary createFromParcel(Parcel source){
            String enWord = source.readString();
            List<Mean> cnList = new ArrayList<>();
            cnList = source.readArrayList(Mean.class.getClassLoader());
            long updated_at = source.readLong();
            String imageUri = source.readString();
            Vocabulary vocabulary = new Vocabulary(enWord, cnList, updated_at);
            vocabulary.setImageUri(imageUri);
            return vocabulary;
        }

        @Override
        public Vocabulary[] newArray(int size){
            return new Vocabulary[size];
        }
    };
}
