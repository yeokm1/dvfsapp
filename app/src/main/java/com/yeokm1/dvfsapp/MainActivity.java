package com.yeokm1.dvfsapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.yeokm1.dvfsapp.RangeSeekBar.OnRangeSeekBarChangeListener;


public class MainActivity extends ActionBarActivity {

    private static final int FPS_MIN = 20;
    private static final int FPS_MAX = 60;

    private static final int FPS_DEFAULT_SELECTION_MIN = 30;
    private static final int FPS_DEFAULT_SELECTION_MAX = 35;

    private static final int FPS_FIXED_SELECTION_RANGE = 5;

    Button startStopButton;

    private DVFSHandler dvfsHandler;

    private static final String TAG = "MainActivity";


    private static final String INVALID_FPS_VALUES = "Invalid FPS values provided";

    private static final String DVFS_STOP = "Stop";
    private static final String DVFS_START = "Start";

    private int lowBound = FPS_DEFAULT_SELECTION_MIN;
    private int highBound = FPS_DEFAULT_SELECTION_MAX;


    TextView lowFPSSelection;
    TextView highFPSSelection;

    CheckBox dynamicRangeSelection;
    CheckBox bestPerformanceWhenChargingSelection;


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

        dynamicRangeSelection = (CheckBox) findViewById(R.id.main_checkbox_dynamic_range);
        bestPerformanceWhenChargingSelection = (CheckBox) findViewById(R.id.main_checkbox_charging_best_perf);

        dvfsHandler = new DVFSHandler(getApplicationContext());

        // create RangeSeekBar as Integer range between 20 and 75
        final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(FPS_MIN, FPS_MAX, this);

        seekBar.setSelectedMinValue(FPS_DEFAULT_SELECTION_MIN);
        seekBar.setSelectedMaxValue(FPS_DEFAULT_SELECTION_MAX);

        seekBar.setNotifyWhileDragging(true);
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {

                if(lowBound != minValue){
                    //Means user has changed min
                    int newMaxValue = minValue + FPS_FIXED_SELECTION_RANGE;

                    if(newMaxValue > FPS_MAX){
                        //Don't allow to change value at max
                        seekBar.setSelectedMinValue(lowBound);
                    } else {
                        seekBar.setSelectedMaxValue(newMaxValue);
                        lowBound = minValue;
                        highBound = newMaxValue;
                    }

                } else if(highBound != maxValue){
                    //Means user has changed max

                    int newMinValue = maxValue - FPS_FIXED_SELECTION_RANGE;

                    if(newMinValue < FPS_MIN){
                        //Don't allow to change value at max
                        seekBar.setSelectedMaxValue(highBound);
                    } else {
                        seekBar.setSelectedMinValue(newMinValue);
                        lowBound = newMinValue;
                        highBound = maxValue;
                    }
                }






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

                boolean dynamicRange = dynamicRangeSelection.isChecked();
                boolean bestPerfWhenCharging = bestPerformanceWhenChargingSelection.isChecked();

                int lower;
                int higher;

                if(dynamicRange){
                    lower = DVFSHandler.DYNAMIC_RANGE;
                    higher = DVFSHandler.DYNAMIC_RANGE;
                } else {
                    lower = lowBound;
                    higher = highBound;
                }

                dvfsHandler.startDVFS(lower, higher, bestPerfWhenCharging);

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
