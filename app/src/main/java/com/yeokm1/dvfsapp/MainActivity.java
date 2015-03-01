package com.yeokm1.dvfsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yeokm1.dvfsapp.RangeSeekBar.OnRangeSeekBarChangeListener;


public class MainActivity extends ActionBarActivity {

    private static final int FPS_MIN = 20;
    private static final int FPS_MAX = 60;

    private static final int FPS_DEFAULT_MIN = 30;
    private static final int FPS_DEFAULT_MAX = 60;

    Button startStopButton;

    private DVFSHandler dvfsHandler;

    private static final String TAG = "MainActivity";


    private static final String INVALID_FPS_VALUES = "Invalid FPS values provided";

    private static final String DVFS_STOP = "Stop";
    private static final String DVFS_START = "Start";

    private int lowBound = FPS_DEFAULT_MIN;
    private int highBound = FPS_DEFAULT_MAX;


    TextView lowFPSSelection;
    TextView highFPSSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = (Button) findViewById(R.id.main_button_start_stop);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPress();
            }
        });

        lowFPSSelection = (TextView) findViewById(R.id.main_textview_selected_low_fps);
        highFPSSelection = (TextView) findViewById(R.id.main_textview_selected_high_fps);

        dvfsHandler = new DVFSHandler(getApplicationContext());

        // create RangeSeekBar as Integer range between 20 and 75
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(FPS_MIN, FPS_MAX, this);

        seekBar.setSelectedMinValue(FPS_DEFAULT_MIN);
        seekBar.setSelectedMaxValue(FPS_DEFAULT_MAX);

        seekBar.setNotifyWhileDragging(true);
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                lowBound = minValue;
                highBound = maxValue;
                refreshFPSText();
            }
        });

        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout_for_seekbar);
        layout.addView(seekBar);

        refreshFPSText();
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshButtonText();
    }

    public void refreshFPSText(){
          lowFPSSelection.setText(Integer.toString(lowBound));
          highFPSSelection.setText(Integer.toString(highBound));
    }

    public void buttonPress(){

        if(dvfsHandler.isDVFSActive()){
            dvfsHandler.stopDVFS();
        } else {
            try {

                dvfsHandler.startDVFS(lowBound, highBound);

            }catch(NumberFormatException e){
                Toast.makeText(this, INVALID_FPS_VALUES, Toast.LENGTH_SHORT).show();
            }
        }

        refreshButtonText();
    }


    private void refreshButtonText(){

        boolean status = dvfsHandler.isDVFSActive();

        if(status){
            startStopButton.setText(DVFS_STOP);
        } else {
            startStopButton.setText(DVFS_START);
        }


    }

}
