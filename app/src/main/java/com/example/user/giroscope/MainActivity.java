package com.example.user.giroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {

    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mGiroscope;
    GraphView graph;
    private double graph2LastXValue = 5d;
    private Double[] dataPoints;
    LineGraphSeries<DataPoint> series;

    private Thread thread;
    private boolean plotData = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mGiroscope = mSensorManager.getDefaultSensor(Sensor.
                TYPE_GYROSCOPE);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        if (mGiroscope != null) {
            mSensorManager.registerListener((SensorEventListener) this, mGiroscope,SensorManager.SENSOR_DELAY_GAME);

        }


        graph = (GraphView) findViewById(R.id.graph);

        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph.addSeries(series);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);

        feedMultiple();
    }
    public void addEntry(SensorEvent event) {
        /*     LineGraphSeries<DataPoint> series = new LineGraphSeries<>();*/
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];

        graph2LastXValue += 1d;
        series.appendData(new DataPoint(graph2LastXValue, y), true, 20);

        graph.addSeries(series);



        /*LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(x, y),
        });
        graph.addSeries(series);*/

        /*float  giroscopeSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        double giroscope = Math.sqrt(giroscopeSquareRoot);
        long actualTime = System.currentTimeMillis();
        graph2LastXValue += 1d;
        series.appendData(new GraphView(giroscopeSquareRoot,));
        addDataPoint(giroscope);
*/
    }
    private void addDataPoint(double giroscope) {
        dataPoints[499] = giroscope;
        //To change body of created methods use File | Settings | File Templates.
    }
    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    public void onSensorChanged(SensorEvent event) {
        /*     addEntry(event);*/
        if(plotData){
            addEntry(event);

            plotData = false;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i){

//    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this, mGiroscope, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener((SensorEventListener) MainActivity.this);
        thread.interrupt();
        super.onDestroy();
    }

}

