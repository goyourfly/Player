package com.brouken.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;
import okhttp3.internal.Util;

public class WLEDSettingsActivity extends AppCompatActivity {
    public static Bitmap bitmap;

    EditText etIp;
    EditText etPort;

    EditText etLeftNum;
    EditText etTopNum;
    EditText etRightNum;
    EditText etBottomNum;

    EditText etLeftMargin;
    EditText etTopMargin;
    EditText etRightMargin;
    EditText etBottomMargin;

    EditText etBrightness;
    EditText etStrokeWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_settings_wled);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        etIp = findViewById(R.id.et_ip_address);
        etPort = findViewById(R.id.et_port);

        etLeftNum = findViewById(R.id.et_left_num);
        etTopNum = findViewById(R.id.et_top_num);
        etRightNum = findViewById(R.id.et_right_num);
        etBottomNum = findViewById(R.id.et_bottom_num);

        etLeftMargin = findViewById(R.id.et_left_margin);
        etTopMargin = findViewById(R.id.et_top_margin);
        etRightMargin = findViewById(R.id.et_right_margin);
        etBottomMargin = findViewById(R.id.et_bottom_margin);

        etBrightness = findViewById(R.id.et_brightness);
        etStrokeWidth = findViewById(R.id.et_stroke_width);

        WledInfo info = read();
        etIp.setText(info.ip);
        etPort.setText(String.valueOf(info.port));
        etLeftNum.setText(String.valueOf(info.leftNum));
        etTopNum.setText(String.valueOf(info.topNum));
        etRightNum.setText(String.valueOf(info.rightNum));
        etBottomNum.setText(String.valueOf(info.bottomNum));

        etLeftMargin.setText(String.valueOf(info.leftMargin));
        etTopMargin.setText(String.valueOf(info.topMargin));
        etRightMargin.setText(String.valueOf(info.rightMargin));
        etBottomMargin.setText(String.valueOf(info.bottomMargin));

        etBrightness.setText(String.valueOf(info.brightness));
        etStrokeWidth.setText(String.valueOf(info.strokeWidth));

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
                WledInfo info = new WledInfo();
                info.ip = etIp.getText().toString();
                info.port = Integer.parseInt(etPort.getText().toString());

                info.leftNum = Integer.parseInt(etLeftNum.getText().toString());
                info.topNum = Integer.parseInt(etTopNum.getText().toString());
                info.rightNum = Integer.parseInt(etRightNum.getText().toString());
                info.bottomNum = Integer.parseInt(etBottomNum.getText().toString());

                info.leftMargin = Integer.parseInt(etLeftMargin.getText().toString());
                info.topMargin = Integer.parseInt(etTopMargin.getText().toString());
                info.rightMargin = Integer.parseInt(etRightMargin.getText().toString());
                info.bottomMargin = Integer.parseInt(etBottomMargin.getText().toString());

                info.brightness = Integer.parseInt(etBrightness.getText().toString());
                info.strokeWidth = Integer.parseInt(etStrokeWidth.getText().toString());
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
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
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
        public boolean debug;
    }
}