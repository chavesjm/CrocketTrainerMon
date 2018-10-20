package com.example.juanma.crockettrainermon;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import com.example.juanma.crockettrainermon.BluetoothServer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = BluetoothServer.class.getSimpleName();

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private double mCurrentValue = 2;
    private LineGraphSeries<DataPoint> mSeriesMax;
    private LineGraphSeries<DataPoint> mSeriesMin;
    private LineGraphSeries<DataPoint> mSeriesError;
    private BluetoothServer mBluetoothServer;
    private long mCont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);


        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeriesMax = new LineGraphSeries<>();
        mSeriesMin = new LineGraphSeries<>();
        mSeriesError = new LineGraphSeries<>();
        graph.addSeries(mSeriesMax);
        graph.addSeries(mSeriesMin);
        graph.addSeries(mSeriesError);

        mSeriesError.setColor(Color.RED);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        graph.getViewport().setMinY(-5);
        graph.getViewport().setMaxY(5);
        graph.getViewport().setYAxisBoundsManual(true);


//        mTimer1 = new Runnable() {
//            @Override
//            public void run() {
//                mCurrentValue = mCurrentValue * -1;
//
//                mSeriesMax.appendData(new DataPoint(System.currentTimeMillis(),3.0), true, 4000);
//                mSeriesMin.appendData(new DataPoint(System.currentTimeMillis(),-3.0), true, 4000);
//                mSeriesError.appendData(new DataPoint(System.currentTimeMillis(),mCurrentValue), true, 4000);
//
//                mHandler.postDelayed(mTimer1,200);
//            }
//        };
//
//        mHandler.postDelayed(mTimer1, 1000);

        mBluetoothServer = new BluetoothServer();
        mBluetoothServer.setListener(mBluetoothServerListener);
        try {
            mBluetoothServer.start();
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setMinError(double value, long count){

        mSeriesMin.appendData(new DataPoint(count,value), true, 100);
    }

    public void setMaxError(double value, long count){

        mSeriesMax.appendData(new DataPoint(count,value), true, 100);
    }

    public void setError(double value, long count){
        mSeriesError.appendData(new DataPoint(count,value), true, 100);
    }


    private void processMessage(String message) {

        String values[];
        values = message.split(",");

        mCont++;

        setMaxError(Double.parseDouble(values[11]),mCont);
        setMinError(Double.parseDouble(values[11]) * -1, mCont);

        setError(Double.parseDouble(values[8]), mCont);

    }

    private void writeMessage(String message) {

        Log.i(TAG, message);
    }

    /**
     * Bluetooth server events listener.
     */
    private BluetoothServer.IBluetoothServerListener mBluetoothServerListener =
            new BluetoothServer.IBluetoothServerListener() {
                @Override
                public void onStarted() {
                    writeMessage("*** Server has started, waiting for client connection ***");
                }

                @Override
                public void onConnected() {
                    writeMessage("*** Client has connected ***");
                }

                @Override
                public void onData(String data) {
                    processMessage(new String(data));
                }

                @Override
                public void onError(String message) {
                    writeMessage(message);
                }

                @Override
                public void onStopped() {
                    writeMessage("*** Server has stopped ***");
                }
            };
}
