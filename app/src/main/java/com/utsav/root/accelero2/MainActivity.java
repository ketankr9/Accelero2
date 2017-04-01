package com.utsav.root.accelero2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
//    Button startLeft,stopLeft,startRight,stopRight,compareButton;
    File root,dir,sensorFile;
    private SensorManager senseManage;
    private Sensor senseAccelero;
    String position;
    TextView textview4,textview6,textview5;
    FileReadWrite fileHandler = new FileReadWrite();
    String entry;
    long startTime,endTime;
    EditText edittext,edittext2,edittext3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview4=(TextView)findViewById(R.id.textView4);
        textview6=(TextView)findViewById(R.id.textView6);
        textview5=(TextView)findViewById(R.id.textView5);
        edittext=(EditText)findViewById(R.id.editText);
        edittext2=(EditText)findViewById(R.id.editText2);
        edittext3=(EditText)findViewById(R.id.editText3);

        senseManage=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        senseAccelero=senseManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

//        senseManage.registerListener(this,senseAccelero,SensorManager.SENSOR_DELAY_NORMAL);

    }
    public void startLeft(View v){
        position="left";
        entry="";
        startTime=System.currentTimeMillis();
        textview5.setText("LEFT READING");
        senseManage.registerListener(this,senseAccelero,SensorManager.SENSOR_DELAY_FASTEST);
        Toast.makeText(getApplicationContext(), "startLeft", Toast.LENGTH_SHORT).show();

    }
    public void stopLeft(View v){
        position="left";
        endTime=System.currentTimeMillis();
        senseManage.unregisterListener(this);
        fileHandler.write("myleft.txt",entry);
        textview5.setText("SAVED LEFT ");
        Toast.makeText(getApplicationContext(), "stopLeft", Toast.LENGTH_SHORT).show();

    }
    public void startRight(View v){
        position="right";
        entry="";
        startTime=System.currentTimeMillis();
        textview5.setText("RIGHT READING");
        senseManage.registerListener(this,senseAccelero,SensorManager.SENSOR_DELAY_FASTEST);
        Toast.makeText(getApplicationContext(), "startRight", Toast.LENGTH_SHORT).show();

    }
    public void stopRight(View v){
        position="right";
        endTime=System.currentTimeMillis();
        senseManage.unregisterListener(this);
        fileHandler.write("myright.txt",entry);
        textview5.setText("SAVED RIGHT");
        Toast.makeText(getApplicationContext(), "stopRight", Toast.LENGTH_SHORT).show();

    }
    public void compareButton(View v){
        textview5.setText("Calculating");
        // read data from file
        Vector<Float[]> vec= fileHandler.read("myleft.txt");
        Float[] XL,YL,ZL,TL;
        TL=vec.get(0);/*XL=vec.get(1);YL=vec.get(2);*/ZL=vec.get(1);

        vec= fileHandler.read("myright.txt");
        Float[] XR,YR,ZR,TR;
        TR=vec.get(0);/*XR=vec.get(1);YR=vec.get(2);*/ZR=vec.get(1);

        int windowSize;
        Float thresholdK;
        try{
        windowSize=Integer.valueOf(edittext.getText().toString());
        }catch(Exception e) {
            windowSize=5;
        }
        try{
            thresholdK=Float.valueOf(edittext2.getText().toString());
        }
        catch(Exception e){
            thresholdK=10f;
        }

        Filters filter = new Filters();
      /*YR=filter.movingAverage(YR,windowSize);
        YL=filter.movingAverage(YL,windowSize);
        XR=filter.movingAverage(XR,windowSize);
        XL=filter.movingAverage(XL,windowSize);*/
        ZL=filter.lowpass(ZL,0.5f);
        for(int i=0;i<5;i++)
            ZL=filter.movingAverage(ZL,windowSize);
        filter.normalize(ZL);

        ZR=filter.lowpass(ZR,0.5f);
        for(int i=0;i<5;i++)
            ZR=filter.movingAverage(ZR,windowSize);
        filter.normalize(ZR);

        Log.d("ACCE", Arrays.toString(ZL));
        CompareData data=new CompareData();
        Log.d("ACC","Created object compare data");
        float ans=data.dwtXYZ(ZL,ZR,thresholdK);
        Log.d("ACC Calculated dwt",String.valueOf(ans));
        textview6.setText("DWT:"+String.valueOf(ans));
        textview5.setText("Finish Processing");
        Toast.makeText(getApplicationContext(), "Compare", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor=event.sensor;
        if(mySensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];
            long timeElapsed=System.currentTimeMillis()-startTime;
            textview4.setText(String.valueOf(timeElapsed));
            entry+=String.valueOf(timeElapsed)+"\t"+String.valueOf(x)+"\t"+String.valueOf(y)+"\t"+String.valueOf(z)+"\n";

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
