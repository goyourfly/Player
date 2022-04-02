package com.brouken.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ConfigView extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private TextView textView;
    private SeekBar seekBar;
    private String label;

    public ConfigView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ConfigView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ConfigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public ConfigView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);
        textView = new TextView(context);
        seekBar = new SeekBar(context);
        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params2.topMargin = getResources().getDimensionPixelSize(R.dimen.space_10);
        attachViewToParent(textView, 0, params1);
        attachViewToParent(seekBar, 1, params2);
        seekBar.setOnSeekBarChangeListener(this);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConfigView);
            label = typedArray.getString(R.styleable.ConfigView_label);
            int maxValue = typedArray.getInteger(R.styleable.ConfigView_maxValue,200);
            int progress = typedArray.getInteger(R.styleable.ConfigView_progress,0);
            seekBar.setMax(maxValue);
            seekBar.setProgress(progress);
            label = typedArray.getString(R.styleable.ConfigView_label);
            textView.setText(label);
            typedArray.recycle();
        }
        seekBar.setKeyProgressIncrement(1);
    }

    public void setValue(int value){
        seekBar.setProgress(value);
        textView.setText(label + value);
    }

    public void setMax(int max){
        seekBar.setMax(max);
        seekBar.setKeyProgressIncrement(1);
    }

    public int getValue(){
        return seekBar.getProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        textView.setText(label + i);
        if (b) {
            ((WLEDSettingsActivity) getContext()).onValueChanged();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
