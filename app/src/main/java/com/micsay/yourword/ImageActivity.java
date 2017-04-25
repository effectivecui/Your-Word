package com.micsay.yourword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.micsay.yourword.beans.Vocabulary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO = 1;
    private static final String TAG = "IMAGEACTIVITY";
    @BindView(R.id.img) ImageView imageView;
    Uri imageUri = null;
    Vocabulary vocabulary;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        vocabulary = intent.getParcelableExtra("vocabulary");
        String iu = vocabulary.getImageUri();
        if("".equals(iu)){
            takePhone();
        }else{
            imageUri = Uri.parse(vocabulary.getImageUri());
            loadImage();
        }
    }

    public void loadImage(){
        try{
            imageView.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri)));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void onFinish(){
        if(imageUri != null) vocabulary.setImageUri(imageUri.toString());
        else vocabulary.setImageUri("");

        MainActivity.setResult(this, vocabulary);
        finish();
    }

    @Override public void onBackPressed(){
        onFinish();
    }
    @Override public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete){
            takePhone();
        }else{
            onFinish();
        }
        return true;
    }
    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private void takePhone(){
        File outputImage = new File(getExternalCacheDir(), vocabulary.getEnWord()+".jpg");
        try{
            if(outputImage.exists()){
                vocabulary.setImageUri("");
                outputImage.delete();
            }
            outputImage.createNewFile();

        }catch (IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24)
            imageUri = FileProvider.getUriForFile(ImageActivity.this, "com.example.yourword.fileprovider", outputImage);
        else
            imageUri = Uri.fromFile(outputImage);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TAKE_PHOTO){
            if(resultCode == RESULT_OK){
                loadImage();
            }else {
                imageUri = null;
                onFinish();
            }
        }
    }
    public static void actionStartForResult(Context context, Vocabulary vocabulary, int requestCode){
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("vocabulary", vocabulary);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }
}
