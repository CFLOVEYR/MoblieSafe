package com.tofirst.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tofirst.mobilesafe.R;

public class ToastLocationActivity extends Activity {

    private TextView textView;
    private int startX;
    private int startY;
    private SharedPreferences mpre;
    private int width;
    private int height;
    //设置双击事件的次数，点击多少次触发
    long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        textView = (TextView) findViewById(R.id.tv_toast_location);
        mpre = getSharedPreferences("config", MODE_PRIVATE);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        final int lastX = mpre.getInt("lastX", 0);
        final int lastY = mpre.getInt("lastY", 0);
        //初始化的时候绘制
//        textView.layout(lastX,lastY,lastX+textView.getWidth(),lastY+textView.getHeight());
        /**
         *    onMeasure(测量) ，onLayout(安放位置)， onDraw(绘制)
         */
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();//获取布局对象
        layoutParams.leftMargin = lastX;//左边距
        layoutParams.topMargin = lastY;//上边距
        chekText(lastY);        //上下任意位置的文本消失显示的判断
        textView.setLayoutParams(layoutParams);
        //点击事件
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *        判断是否双击方法的调用
                 */
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    textView.layout(width / 2 - textView.getWidth() / 2,
                            textView.getTop(), width / 2 + textView.getWidth() / 2,
                            textView.getBottom());
                }
            }
        });


        /**
         *  触发拖动事件
         */
        textView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获得起点坐标
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获得移动后的坐标
                        int endX = (int) motionEvent.getRawX();
                        int endY = (int) motionEvent.getRawY();
                        //移动的距离
                        float dx = endX - startX;
                        float dy = endY - startY;
                        //让图标坐标放生改变
                        int l = (int) (textView.getLeft() + dx);
                        int r = (int) (textView.getRight() + dx);
                        int t = (int) (textView.getTop() + dy);
                        int b = (int) (textView.getBottom() + dy);
                        //检查坐标的合理性，不然展示框超出界面
                        if (l < 0 || r > width || t < 0 || b > height) {
                            break;
                        }
                        //上下任意位置的文本消失显示的判断
                        chekText(t);
                        textView.layout(l, t, r, b);

                        //重置坐标
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //保存坐标
                        mpre.edit().putInt("lastX", textView.getLeft()).commit();
                        mpre.edit().putInt("lastY", textView.getTop()).commit();
                        break;
                }

                return false;//设置为false，表示处理完毕让下边的继续处理
            }
        });
    }

    private void chekText(int lastY) {
        if (lastY > height / 2 - textView.getHeight() / 2) {
            findViewById(R.id.tv_location_top).setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_location_bottom).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tv_location_top).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_location_bottom).setVisibility(View.INVISIBLE);
        }
    }

}
