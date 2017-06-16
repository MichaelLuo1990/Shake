package com.michael.shake;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.a720.shake.R;

/**
 * Created by michaelluo on 17/6/15.
 *
 * @desc 摇一摇控制震动、声音、动画
 */

public class ShakeHandler extends Handler {

    private LinearLayout llTop;
    private LinearLayout llBottom;
    private ImageView ivTopLine;
    private ImageView ivBottomLine;
    private Vibrator vibrator;//震动器
    private SoundPool soundPool;//声音池-音效
    private int weiChatAudio;//声音资源文件（微信摇一摇声音）

    public ShakeHandler(Context context, LinearLayout llTop, LinearLayout llBottom, ImageView ivTopLine, ImageView ivBottomLine) {
        this.llTop = llTop;
        this.llBottom = llBottom;
        this.ivTopLine = ivTopLine;
        this.ivBottomLine = ivBottomLine;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); //获取震动服务
        soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);//初始化SoundPool
        weiChatAudio = soundPool.load(context, R.raw.weichat_audio, 1);//加载声音文件
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.SHAKE_START:
                vibrator.vibrate(300);
                soundPool.play(weiChatAudio, 1, 1, 0, 0, 1);//发出提示音
                ivTopLine.setVisibility(View.VISIBLE);
                ivBottomLine.setVisibility(View.VISIBLE);
                switchTopBottomLayout(false);//参数含义: 两张图片分散开的动画
                break;
            case Constants.SHAKE_AGAIN:
                vibrator.vibrate(300);
                break;
            case Constants.SHAKE_END:
                ShakeListener.isShake = false;//整体效果结束, 将震动设置为false
                switchTopBottomLayout(true);// 展示上下两种图片合起来的效果
                break;
        }
    }

    /**
     * 开关 摇一摇动画布局显示
     * @param isClose 是否是关闭状态（初始状态）
     */
    private void switchTopBottomLayout(boolean isClose) {

        float topFromY;
        float topToY;
        float bottomFromY;
        float bottomToY;
        if (isClose) {
            topFromY = -0.5f;
            topToY = 0;
            bottomFromY = 0.5f;
            bottomToY = 0;
        } else {
            topFromY = 0;
            topToY = -0.5f;
            bottomFromY = 0;
            bottomToY = 0.5f;
        }

        int type = Animation.RELATIVE_TO_SELF;//动画坐标移动的位置的类型是相对自己

        //顶部的动画效果
        TranslateAnimation topAnim = new TranslateAnimation(
                type, 0, type, 0, type, topFromY, type, topToY
        );
        topAnim.setDuration(200);
        topAnim.setFillAfter(true);//动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        llTop.startAnimation(topAnim);//设置动画

        //底部的动画效果
        TranslateAnimation bottomAnim = new TranslateAnimation(
                type, 0, type, 0, type, bottomFromY, type, bottomToY
        );
        bottomAnim.setDuration(200);
        bottomAnim.setFillAfter(true);
        llBottom.startAnimation(bottomAnim);

        //判断分割线：关闭时，中间的两根线GONE隐藏；监听动画结束状态隐藏分割线（顶部或底部监听任意状态皆可，cause：动画参数一致）
        if (isClose) {
            topAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //当动画结束后 , 将中间两条线GONE掉, 不让其占位
                    ivTopLine.setVisibility(View.GONE);
                    ivBottomLine.setVisibility(View.GONE);
                }
            });
        }
    }
}
