package com.androidapp.compassappwibtn;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class CompassActivity extends Activity {
    CompassView compassView;
    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor magneticSensor, accelSensor;
    float [] magneticValues, accelValues;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassView = findViewById(R.id.compassView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER : accelValues = event.values.clone(); break;
                    case Sensor.TYPE_MAGNETIC_FIELD : magneticValues = event.values.clone(); break;
                    default: break;
                }
                if(magneticValues != null && accelValues != null){
                    float[] R = new float[16];
                    float[] I = new float[16];
                    SensorManager.getRotationMatrix(R, I, accelValues, magneticValues);
                    float[] values = new float[3];
                    SensorManager.getOrientation(R, values);

                    compassView.azimuth = (int) radian2degree(values[0]);
                    compassView.invalidate();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }
        };
        sensorManager.registerListener(listener, magneticSensor, sensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, accelSensor, sensorManager.SENSOR_DELAY_UI);
    }

    private float radian2degree(float radian) {return  radian * 180 / (float)Math.PI; }
}
