package com.yeokm1.dvfsapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by yeokm1 on 25/2/2015.
 */
public class DVFSHandler {

    private static final String TAG = "DVFSHandler";

    private static final String DVFS_FILENAME = "dvfs-binary";
    private static final String DVFS_PATH = "/data/local/tmp/dvfs-binary";
    private static final String CHMOD_COMMAND = "chmod 777 /data/local/tmp/dvfs-binary";
    private static final String DVFS_COMMAND = "/data/local/tmp/dvfs-binary %d %d &";

    private static final String PS_COMMAND = "ps dvfs-binary";

    private boolean isActive = false;

    private Context context;


    public DVFSHandler(Context context){
        this.context = context;
    }


    public void startDVFS(int lowBound, int highBound){
        if(isActive){
            stopDVFS();
        }



        copyDVFSBinary();

        String dvfsCommand = String.format(DVFS_COMMAND, lowBound, highBound);

        Shell.SU.run(CHMOD_COMMAND);
        sudo(dvfsCommand);
    }

    public static void sudo(String...strings) {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }



    public void stopDVFS(){

        List<String> psOutput = Shell.SU.run(PS_COMMAND);

        if(psOutput.size() > 1){
            String resultLine = psOutput.get(1);

            String[] splitted  = resultLine.split(" ");


        }





        if(!isActive){
            return;
        }

        isActive = false;

    }

    private void copyDVFSBinary() {
        AssetManager assetManager = context.getAssets();

        String cachePath = context.getExternalCacheDir().getPath();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DVFS_FILENAME);
            File outFile = new File(cachePath, DVFS_FILENAME);
            out = new FileOutputStream(outFile);
            copyFile(in, out);

        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + DVFS_FILENAME, e);
        }   finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }

        }


        Shell.SU.run("cp " + cachePath + "/" + DVFS_FILENAME + " /data/local/tmp/" + DVFS_FILENAME);
    }





    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
