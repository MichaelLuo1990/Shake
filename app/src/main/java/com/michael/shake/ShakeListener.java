package com.michael.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by michaelluo on 17/6/15.
 *
 * @desc 摇一摇监听-实现传感器事件监听
 */

public class ShakeListener implements SensorEventListener {

    public static boolean isShake = false;//记录摇动状态-init false
    private ShakeHandler shakeHandler;//摇一摇控制者

    public ShakeListener(ShakeHandler shakeHandler) {
        this.shakeHandler = shakeHandler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //获取传感器类型：判断是否为加速类型
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            //取绝对值判定是否是摇动操作（数值可依据测试调整大小,理论上超过15即可）
            if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math
                    .abs(z) > 17) && !isShake) {
                isShake = true;//设置状态为true时改变显示动画状态
                //启动线程控制摇一摇震动效果
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d(Constants.TAG, "onSensorChanged: 摇动");
                            //开始震动 发出提示音 展示动画效果
                            shakeHandler.obtainMessage(Constants.SHAKE_START).sendToTarget();
                            Thread.sleep(500);
                            //再来一次震动提示
                            shakeHandler.obtainMessage(Constants.SHAKE_AGAIN).sendToTarget();
                            Thread.sleep(500);
                            shakeHandler.obtainMessage(Constants.SHAKE_END).sendToTarget();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
