package com.yeokm1.dvfsapp;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by yeokm1 on 25/2/2015.
 */
public class DVFSHandler {

    private static final String TAG = "DVFSHandler";

    private boolean isActive = false;

    public DVFSHandler(){

    }


    public void startDVFS(int lowBound, int highBound){
        if(isActive){
            stopDVFS();
        }

       Shell.SU.run("ls");


       isActive = true;
    }



    public void stopDVFS(){

        if(!isActive){
            return;
        }


        isActive = false;

    }
}
