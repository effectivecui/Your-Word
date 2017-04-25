package com.micsay.yourword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.micsay.yourword.beans.Mean;
import com.micsay.yourword.beans.Vocabulary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlertDialogActivity extends Activity {
    private final String TAG = "AlertDialogActivity";
    @BindView(R.id.en_word) EditText etEnWord;
    @BindView(R.id.cn_word) EditText etCnWord;
    @BindView(R.id.bt_ok) Button btOk;
    @BindColor(R.color.bt_color) int btColor;
    @BindView(R.id.bt_cancel) ImageView bt_cancel;
    @BindView(R.id.bt_n) Button preButton;
    @BindColor(R.color.colorPrimary) int colorPrimary;
    @BindColor(R.color.colorPrimaryLight) int colorPrimaryLight;
    boolean lastest = false;
    private Map<String, String> sMap = new HashMap<>();
    private Set<String> vSet = new HashSet<>();
    private Vocabulary vocabulary;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);
        bt_cancel.setColorFilter(colorPrimary);
        Intent intent = getIntent();
        vocabulary = (Vocabulary)intent.getParcelableExtra("vocabulary");
        if(vocabulary!=null){
            List<Mean> meanList = vocabulary.getCnList();
            for(Mean m : meanList){
                sMap.put(m.getType(), m.getContent());
            }
            etEnWord.setText(vocabulary.getEnWord());
            etEnWord.setEnabled(false);
            if(sMap.containsKey("n.")){
                etCnWord.setText(sMap.get("n."));
            }
            save(preButton);
        }else{
            save(preButton);
            List<Vocabulary> vList = Vocabulary.findAll();
            for(Vocabulary v : vList){
                vSet.add(v.getEnWord());
            }
            etEnWord.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(vSet.contains(charSequence.toString())){
                        btOk.setEnabled(false);
                        btOk.setBackgroundColor(colorPrimaryLight);
                    }else{
                        btOk.setEnabled(true);
                        btOk.setBackgroundColor(colorPrimary);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
    @Override
    public void onBackPressed(){
        if(etEnWord.getText().toString().equals("")){
            finish();
        }else{
            Toast.makeText(this, "Please finish it firstly", Toast.LENGTH_SHORT).show();
        }
    }
    public static void actionStartForResult(Context context, Vocabulary vocabulary, int requestCode){
        Intent intent = new Intent(context, AlertDialogActivity.class);
        intent.putExtra("vocabulary", vocabulary);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    @OnClick({R.id.bt_n, R.id.bt_vt, R.id.bt_vi, R.id.bt_adj, R.id.bt_adv, R.id.bt_other, R.id.bt_prep})
    public void onNClick(Button button){
        save(button);
    }

    private void save(Button button){
        sMap.put(preButton.getText().toString(), etCnWord.getText().toString());
        String btText = button.getText().toString();
        if(sMap.containsKey(btText)){
            etCnWord.setText(sMap.get(btText));
        }else{
            etCnWord.setText("");
        }
        preButton.setBackgroundResource(R.drawable.bt_corners_bg);
        button.setBackgroundColor(colorPrimary);
        preButton = button;
        lastest = false;
    }

    private void update(){
        if(!lastest)save(preButton);
    }
    @OnClick(R.id.bt_ok)
    public void onOkClick(Button button){
        update();
        if(vocabulary!=null){
            vocabulary.setCnList(sMap);
        }else{
            vocabulary = addVocabulary();
            if(vocabulary == null)return;
        }
        MainActivity.setResult(this, vocabulary);
        finish();
    }
    private Vocabulary addVocabulary(){
        if(vocabulary == null){
            String temp = etEnWord.getText().toString();
            if("".equals(temp)){
                return null;
            }
            return Vocabulary.newInstance(etEnWord.getText().toString(), sMap);
        }
        return null;
    }

    @OnClick(R.id.bt_cancel)
    public void onCancelClick(){
        finish();
    }
}
