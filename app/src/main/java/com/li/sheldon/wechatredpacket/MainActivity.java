package com.li.sheldon.wechatredpacket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.li.sheldon.weichatredpacket.R;

public class MainActivity extends Activity {

    private String TAG="Looter";
    private Button settingButton;
    private Button initPosButton;
    private TextView posInfo;
    private Intent accessibilityIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private boolean isSetting=false;
    private int mPosX;
    private int mPosY;
    private int mWidth;
    private int mHeight;

    public Handler mHandler;
    public LooterService looterServer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager wm = this.getWindowManager();

        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
        Log.i(TAG, "windows X = "+mWidth+",Y = "+mHeight);

        settingButton = (Button)findViewById(R.id.setButton);
        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(accessibilityIntent);
            }
        });

        posInfo = (TextView)findViewById(R.id.positionInfo);

        initPosButton = (Button)findViewById(R.id.initButton);
        initPosButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isSetting=true;
                initPosButton.setVisibility(View.INVISIBLE);
                settingButton.setVisibility(View.INVISIBLE);
                posInfo.setText("请点击您手机'開'红包的位置：");
            }
        });

        if(mHandler == null){
            Log.v(TAG, "create new mHandler :");
            mHandler = new Handler() {
                public void handleMessage(Message msg)
                {
                    switch (msg.what) {
                        case HandleMsg.MSG_CLICK:
                            //preview 800ms後,模擬點擊對焦調光,开启脸部识别
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    //execute the task
                                    setMouseClick(mPosX,mPosY);
                                }
                            }, 500);

                            break;
                        default:
                            Log.e(TAG, "mCamReceiver error message!");
                            break;
                    }
                }
            };
        }

        if(looterServer!=null){
            looterServer.setHandler(mHandler);
            Log.v(TAG, "looterServer!=null");
        }
        else {
            Log.v(TAG, "looterServer==null");
            looterServer=new LooterService();
            looterServer.setHandler(mHandler);
            Log.v(TAG, "setHandler done.");
        }
    }

    //simulateClick:模擬屏幕點擊開camera時自動對焦和調光,作用於Activity
    public void setMouseClick(int x, int y){
        Log.e(TAG, "setMouseClick : "+x+","+y);

        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100,
                MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(evenDownt);

        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100,
                MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);

        evenDownt.recycle();
        eventUp.recycle();
    }

    //際屏幕點擊位置计算----------------------------------
    private Point calculateTapPoint(float x, float y) {

        Point tapPoint = new Point();
        //ps:x-previewSize.height, y-previewSize.width
        if(mWidth>0 && mHeight>0){
            int centerX = (int)((x / mWidth) * 2000 - 1000);
            int centerY = (int)((y / mHeight) * 2000 - 1000);

            tapPoint.x=centerX;
            tapPoint.y=centerY;
        }
        return tapPoint;
    }

    //获取触摸事件----------------------------------
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "event.getPointerCount() = " + event.getPointerCount());

        int pX = (int) event.getRawX();
        int pY = (int) event.getRawY();
        Log.i(TAG, "getRawX = " + pX + ", getRawX = " + pY);

        if (event.getPointerCount() == 1 && isSetting == true) {
            isSetting=false;
            initPosButton.setVisibility(View.VISIBLE);
            settingButton.setVisibility(View.VISIBLE);

            //Point tapPosition = calculateTapPoint(pX, pY);
            //mPosX = tapPosition.x;
            //mPosY = tapPosition.y;

            mPosX = pX;
            mPosY = pY;

            posInfo.setText("'開'位置："+mPosX+" - "+mPosY);
            Log.i(TAG, "mPosX = " + mPosX + ", mPosY = " + mPosY);
        }
        return true;
    }
}
