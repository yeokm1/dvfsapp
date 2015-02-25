package com.yeokm1.dvfsapp;

/**
 * Created by yeokm1 on 25/2/2015.
 */
public class DVFSHandler {

    private boolean isActive = false;

    public DVFSHandler(){

    }


    public void startDVFS(int lowBound, int highBound){
        if(isActive){
            stopDVFS();
        }

        isActive = true;
    }



    public void stopDVFS(){

        if(!isActive){
            return;
        }


        isActive = false;

    }
}
