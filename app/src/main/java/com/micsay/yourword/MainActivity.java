package com.micsay.yourword;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.micsay.yourword.beans.Mean;
import com.micsay.yourword.beans.Vocabulary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAINACTIVITY";
    public static final int TAKE_PHOTO = 1;
    public static final int ADD_WORD = 2;
    public static final int EDIT_WORD = 3;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List<Vocabulary> vList = Vocabulary.findAll();//recycleView List
    private RecyclerView.Adapter adapter;//recycleViewAdapter=
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VocabularyAdapter(vList);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TAKE_PHOTO || requestCode == ADD_WORD || requestCode == EDIT_WORD){
            if(resultCode == RESULT_OK){
                Vocabulary vocabulary = data.getParcelableExtra("vocabulary");
                Log.d(TAG, "return result " + vocabulary.getImageUri());
                Vocabulary.remove(vocabulary);
                Vocabulary.add(vocabulary);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static void setResult(Context context, Vocabulary vocabulary){
        Intent intent = new Intent();
        intent.putExtra("vocabulary", vocabulary);
        ((Activity)context).setResult(RESULT_OK, intent);
    }

    @OnClick(R.id.fab)
    public void onClick(){
        AlertDialogActivity.actionStartForResult(this, null, ADD_WORD);
    }

    @Override
    public String toString(){
        return "MainActivity";
    }

    public static class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder>{
        private List<Vocabulary> vList;
        StringBuffer stringBuffer;
        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.en_word) TextView enWord;
            @BindView(R.id.cn_word) TextView cnWord;
            @BindView(R.id.bt_show) ImageView show;
            @BindView(R.id.bt_edit) ImageView edit;
            @BindViews({ R.id.bt_img, R.id.bt_del, R.id.bt_pronounce}) List<ImageView> imageViewList;
            @BindColor(R.color.colorPrimary) int colorPrimary;
            @BindColor(R.color.colorPrimaryDark) int colorPrimaryDark;
            Vocabulary vocabulary;
            Snackbar snackbar;
            public ViewHolder(View view){
                super(view);
                ButterKnife.bind(this, view);

                int colorPrimary = view.getContext().obtainStyledAttributes(new int[]{R.attr.colorPrimary}).getColor(0, Color.BLACK);
                for(ImageView iv : imageViewList){
                    iv.setColorFilter(colorPrimary);
                }
                show.setColorFilter(colorPrimary);
                edit.setColorFilter(colorPrimary);
            }
            public void setVocabulary(Vocabulary vocabulary){
                this.vocabulary = vocabulary;
            }

            @OnClick(R.id.bt_show)
            public void onShowClick(){
                cnWord.setVisibility(View.VISIBLE);
                show.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
            }

            @OnClick(R.id.bt_img)
            public void onImgClick(View view){
                ImageActivity.actionStartForResult(view.getContext(), vocabulary, MainActivity.TAKE_PHOTO);
            }

            @OnClick(R.id.bt_del)
            public void onDelClick(View view){
                if(snackbar == null){
                    snackbar = Snackbar.make(view, "Delete ?", Snackbar.LENGTH_LONG);
                    snackbar
                            .setAction("Yes", (View v)->{
                                Vocabulary.remove(vocabulary);
                                VocabularyAdapter.this.notifyDataSetChanged();
                            })
                            .setActionTextColor(Color.WHITE);
                    snackbar.getView().setBackgroundColor(colorPrimaryDark);
                }
                snackbar.show();
            }

            @OnClick(R.id.bt_edit)
            public void onEditClick(View view){
                AlertDialogActivity.actionStartForResult(view.getContext(), vocabulary, EDIT_WORD);
            }

            @OnClick(R.id.bt_pronounce)
            public void onPronouncClick(View view){
                PronounceActivity.actionStart(view.getContext(), vocabulary);
            }
        }
        public VocabularyAdapter(List<Vocabulary> vList){
            this.vList = vList;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vocabulary_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int postion){
            Vocabulary vocabulary = vList.get(postion);
            stringBuffer = new StringBuffer("");
            List<Mean> list = vocabulary.getCnList();
            for(Mean m : list){
                if(!m.getType().equals("")){
                    stringBuffer.append(m.getType());
                    stringBuffer.append("");
                    stringBuffer.append(m.getContent());
                    stringBuffer.append("\n");
                }
            }
            holder.cnWord.setText(stringBuffer);
            //first char caps
            stringBuffer = new StringBuffer(vocabulary.getEnWord());
            char c = stringBuffer.charAt(0);
            if(c >='a' && c <= 'z')c = (char)(c - 32);
            stringBuffer.setCharAt(0, c);
            holder.enWord.setText(stringBuffer.toString());
            holder.setVocabulary(vocabulary);
        }
        @Override
        public int getItemCount(){
            return vList.size();
        }
    }
}
