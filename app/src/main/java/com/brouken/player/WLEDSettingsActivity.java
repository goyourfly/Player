package com.brouken.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;

public class WLEDSettingsActivity extends AppCompatActivity {
    public static Bitmap bitmap;

    EditText etIp;
    EditText etPort;

    ConfigView cvLeftNum;
    ConfigView cvTopNum;
    ConfigView cvRightNum;
    ConfigView cvBottomNum;
    ConfigView cvLeftMargin;
    ConfigView cvTopMargin;
    ConfigView cvRightMargin;
    ConfigView cvBottomMargin;
    ConfigView cvBrightness;
    ConfigView cvStrokeWidth;
    ConfigView cvScale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_settings_wled);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        etIp = findViewById(R.id.et_ip_address);
        etPort = findViewById(R.id.et_port);

        cvLeftNum = findViewById(R.id.cv_left_num);
        cvTopNum = findViewById(R.id.cv_top_num);
        cvRightNum = findViewById(R.id.cv_right_num);
        cvBottomNum = findViewById(R.id.cv_bottom_num);

        cvLeftMargin = findViewById(R.id.cv_left_margin);
        cvTopMargin = findViewById(R.id.cv_top_margin);
        cvRightMargin = findViewById(R.id.cv_right_margin);
        cvBottomMargin = findViewById(R.id.cv_bottom_margin);

        cvBrightness = findViewById(R.id.cv_brightness);
        cvStrokeWidth = findViewById(R.id.cv_stroke_width);
        cvScale = findViewById(R.id.cv_scale);

        WledInfo info = read();
        etIp.setText(info.ip);
        etPort.setText(String.valueOf(info.port));
        cvLeftNum.setValue(info.leftNum);
        cvTopNum.setValue(info.topNum);
        cvRightNum.setValue(info.rightNum);
        cvBottomNum.setValue(info.bottomNum);

        cvLeftMargin.setValue(info.leftMargin);
        cvTopMargin.setValue(info.topMargin);
        cvRightMargin.setValue(info.rightMargin);
        cvBottomMargin.setValue(info.bottomMargin);

        cvBrightness.setValue(info.brightness);
        cvStrokeWidth.setValue(info.strokeWidth);
        cvScale.setValue(info.scale);

        cvLeftNum.setMax(100);
        cvTopNum.setMax(100);
        cvRightNum.setMax(100);
        cvBottomNum.setMax(100);

        cvLeftMargin.setMax(bitmap.getWidth()/2);
        cvTopMargin.setMax(bitmap.getHeight()/2);
        cvRightMargin.setMax(bitmap.getWidth()/2);
        cvBottomMargin.setMax(bitmap.getHeight()/2);

        cvBrightness.setMax(255);
        cvStrokeWidth.setMax(Math.min(bitmap.getWidth(),bitmap.getHeight())/2);
        cvScale.setMax(32);

        refreshPreview(info);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wled, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done: {
                WledInfo info = generateInfo();
                save(info);
                refreshPreview(info);
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    public void onValueChanged(){
        WledInfo info = generateInfo();
        refreshPreview(info);
    }

    private WledInfo generateInfo(){
        WledInfo info = new WledInfo();
        info.ip = etIp.getText().toString();
        info.port = Integer.parseInt(etPort.getText().toString());
        info.leftNum = cvLeftNum.getValue();
        info.topNum = cvTopNum.getValue();
        info.rightNum = cvRightNum.getValue();
        info.bottomNum = cvBottomNum.getValue();
        info.leftMargin = cvLeftMargin.getValue();
        info.topMargin = cvTopMargin.getValue();
        info.rightMargin = cvRightMargin.getValue();
        info.bottomMargin = cvBottomMargin.getValue();
        info.brightness = cvBrightness.getValue();
        info.strokeWidth = cvStrokeWidth.getValue();
        info.scale = cvScale.getValue();
        return info;
    }

    private void refreshPreview(WledInfo info) {
        ImageView preview = findViewById(R.id.v_preview);
        Bitmap bitmap = WLEDSettingsActivity.bitmap;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) preview.getLayoutParams();
        if (bitmap != null) {
            params.dimensionRatio = "h," + bitmap.getWidth() + ":" + bitmap.getHeight();
        }

        Bitmap newBitmap = Bitmap.createBitmap(bitmap);
        List<Rect> list = Utils.measureRect(bitmap.getWidth(), bitmap.getHeight(),
                info.leftNum, info.topNum, info.rightNum, info.bottomNum,
                info.leftMargin, info.topMargin, info.rightMargin, info.bottomMargin,
                info.strokeWidth);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < list.size(); i++) {
            Rect rect = list.get(i);
            if (rect.width() <= 0 || rect.height() <= 0) {
                continue;
            }
            int[] pixels = new int[rect.width() * rect.height()];
            newBitmap.getPixels(pixels, 0, rect.width(), rect.left, rect.top, rect.width(), rect.height());
            int r = Color.red(pixels[0]);
            int g = Color.green(pixels[0]);
            int b = Color.blue(pixels[0]);
            int count = rect.width() * rect.height();
            for (int x = 1; x < count; x++) {
                r += Color.red(pixels[x]);
                g += Color.green(pixels[x]);
                b += Color.blue(pixels[x]);
            }
            r = r / count;
            g = g / count;
            b = b / count;
            int newColor = Utils.getBrightnessColor(r, g, b, info.brightness);
            paint.setStrokeWidth(8 / Math.max(info.scale,1));
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            canvas.drawRect(rect, paint);

            rect.inset(rect.width() / 4, rect.height() / 4);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(newColor);
            canvas.drawRect(rect, paint);
        }
        preview.setImageBitmap(newBitmap);
    }

    public static void save(WledInfo info) {
        Paper.book().write("wled_config", info);
    }

    public static WledInfo read() {
        WledInfo wledInfo = Paper.book().read("wled_config");
        if (wledInfo == null) {
            wledInfo = new WledInfo();
            wledInfo.ip = "192.168.2.247";
            wledInfo.port = 21324;
            wledInfo.leftNum = 40;
            wledInfo.topNum = 69;
            wledInfo.rightNum = 40;
            wledInfo.bottomNum = 0;
            wledInfo.leftMargin = 0;
            wledInfo.topMargin = 0;
            wledInfo.rightMargin = 0;
            wledInfo.bottomMargin = 0;
            wledInfo.brightness = 100;
            wledInfo.strokeWidth = 20;
            wledInfo.scale = 4;
            wledInfo.debug = false;
        }
        return wledInfo;
    }

    public static class WledInfo {
        public String ip;
        public int port;
        public int leftNum;
        public int topNum;
        public int rightNum;
        public int bottomNum;
        public int leftMargin;
        public int topMargin;
        public int rightMargin;
        public int bottomMargin;
        public int brightness;
        public int strokeWidth; // 宽度
        public int scale;
        public boolean debug;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
    }
}