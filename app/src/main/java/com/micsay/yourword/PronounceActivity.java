package com.micsay.yourword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.micsay.yourword.beans.Vocabulary;
import com.micsay.yourword.network.HttpUtil;
import com.micsay.yourword.beans.Translate;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PronounceActivity extends Activity {
    private static final String TAG = "PRONOUNCEACTIVITY";
    @BindView(R.id.et_explains)
    TextView tvExplains;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindColor(R.color.bt_text_color)
    int colorWhite;
    private Translate translate;
    private Vocabulary vocabulary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronounce);
        ButterKnife.bind(this);
        setFinishOnTouchOutside(false);
        Intent intent = getIntent();
        vocabulary = intent.getParcelableExtra("vocabulary");
        if(vocabulary == null){
            finish();
            return;
        }

        translate = Translate.find(vocabulary.getEnWord());
        if(translate == null){
            progressBar.setVisibility(View.VISIBLE);
            Observable<Translate> observable = HttpUtil.getTranslate(vocabulary.getEnWord());
            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Translate>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Translate translate) {
                            if(translate == null)return;
                            if(translate.getQuery().equals(vocabulary.getEnWord())){
                                PronounceActivity.this.translate = translate;
                                tvExplains.setText(translate.toString());
                                Translate.add(translate);
                            }else{
                                tvExplains.setText("Your query: " + vocabulary.getEnWord() + "\nNo explains");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }else{
            tvExplains.setText(translate.toString());
        }
        tvExplains.setVisibility(View.VISIBLE);
    }

    public static void actionStart(Context context, Vocabulary vocabulary){
        Intent intent = new Intent(context, PronounceActivity.class);
        intent.putExtra("vocabulary", vocabulary);
        context.startActivity(intent);
    }
}
