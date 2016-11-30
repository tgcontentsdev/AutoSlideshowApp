package jp.techacademy.haruki.saburi.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    ImageView imageView;
    Button backButton;
    Button nextButton;
    Button startButton;
    Timer timer;
    TimerTask timerTask;
    Handler handler = new Handler();
    boolean flag;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        nextButton = (Button) findViewById(R.id.nextbutton);
        nextButton.setOnClickListener(this);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageView);
        flag = true;
        i = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                // ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                // imageVIew.setImageURI(imageUri);
                // Log.d("ANDROID", "URI : " + imageUri.toString());
                uriArrayList.add(imageUri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        setImage();
    }

    private void setImage() {
        Uri imageUri = uriArrayList.get(0);
        imageView.setImageURI(imageUri);
    }

    @Override
    public void onClick(View v) {
        Uri imageUri;

        if (v.getId() == R.id.backButton) {
            if (i == 0) {
                imageUri = uriArrayList.get(uriArrayList.size() - 1);
                i = uriArrayList.size() - 1;
            } else {
                imageUri = uriArrayList.get(i - 1);
                i --;
            }
            imageView.setImageURI(imageUri);
        } else if (v.getId() == R.id.nextbutton) {
            nextAction();
        } else if (v.getId() == R.id.startButton) {
            if (flag) {
                flag = false;
                startButton.setText("■");
                backButton.setEnabled(false);
                nextButton.setEnabled(false);
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                nextAction();
                            }
                        });

                    }
                };
                timer.schedule(timerTask,0,2000);
            }else {
                startButton.setText("▶︎");
                backButton.setEnabled(true);
                nextButton.setEnabled(true);
                if (timer != null){
                    timer.cancel();
                    timer = null;
                }
            }
        }

    }

    public void nextAction(){
        Uri imageUri;
        if (i == uriArrayList.size() - 1) {
            imageUri = uriArrayList.get(0);
            i = 0;
        } else {
            imageUri = uriArrayList.get(i + 1);
            i ++;
        }
        imageView.setImageURI(imageUri);
    }
}