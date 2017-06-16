package com.michael.shake;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.a720.shake.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michaelluo on 17/6/15.
 *
 * @desc 摇一摇页面
 */
public class ShakeActivity extends AppCompatActivity {
    @BindView(R.id.main_linear_top)
    public LinearLayout llTop;
    @BindView(R.id.main_linear_bottom)
    public LinearLayout llBottom;
    @BindView(R.id.main_shake_top_line)
    public ImageView ivTopLine;
    @BindView(R.id.main_shake_bottom_line)
    public ImageView ivBottomLine;

    private SensorManager sensorManager;//传感器管理者
    private Sensor accelerometerSensor;//传感器（加速）-其他：光线、温度、重力传感器等都通过该对象定义控制
    private ShakeHandler shakeHandler;//摇一摇控制者
    private ShakeListener shakeListener;//传感器监听事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        ButterKnife.bind(this);//绑定ButterKnife
        initView();//初始化视图
        shakeHandler = new ShakeHandler(this, llTop, llBottom, ivTopLine, ivBottomLine);//初始化handler
        shakeListener = new ShakeListener(shakeHandler);//初始化监听事件
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理者服务
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//通过管理者获取传感器类型
            if (accelerometerSensor != null) {
                sensorManager.registerListener(shakeListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);//注册监听事件
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //页面停止时取消传感器监听，防止界面退出后摇一摇依旧生效
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeListener);//取消注册监听事件
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //默认不显示分割线
        ivTopLine.setVisibility(View.GONE);
        ivBottomLine.setVisibility(View.GONE);
    }

}
