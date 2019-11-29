package com.example.compas;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Float azimuth_angel;
    private SensorManager compassSensorManager;
    private float current_degree = 0f;
    Sensor accelerometer;
    Sensor megnetometer;
    TextView tv_degrees;
    ImageView iv_compass;
    float[] accel_read;
    float[] magnetic_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        megnetometer = compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        compassSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this, megnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        tv_degrees = (TextView) findViewById(R.id.tv_degrees);
        iv_compass = (ImageView) findViewById(R.id.iv_compass);
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accel_read = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetic_read = sensorEvent.values;
        if (accel_read != null && magnetic_read != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean successful_read = SensorManager.getRotationMatrix(R, I, accel_read, magnetic_read);
            if (successful_read) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth_angel = orientation[0];
                float degrees = ((azimuth_angel * 180f) / 3.14f);
                int degreesInt = Math.round(degrees);
                tv_degrees.setText(Integer.toString(degreesInt) + (char) 0x00B0 + " to absolute north.");
                RotateAnimation rotate = new RotateAnimation(current_degree, -degreesInt, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setFillAfter(true);

                iv_compass.startAnimation(rotate);
                current_degree = -degreesInt;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
