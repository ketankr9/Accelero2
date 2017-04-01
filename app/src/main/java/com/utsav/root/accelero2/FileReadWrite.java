package com.utsav.root.accelero2;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by root on 3/22/17.
 */

public class FileReadWrite {

    final  String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AcceleroData/" ;
    final  String TAG = FileReadWrite.class.getName();

    public void write(String filename,String entry){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "AcceleroData");
        boolean success = true;
        if (!folder.exists()) {
            Log.d("ACCELERO","FOLDER DOESN't EXIST Creating File");
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
            Log.d("ACCELERO","File present");
            try{
                File ff=new File(folder,filename);
                FileWriter writer = new FileWriter(ff);
                writer.append(entry);
                writer.flush();
                writer.close();
            }catch(IOException e){
                Log.d("ACCELERO",String.valueOf(e));
            }
        } else {
            // Do something else on failure
            Log.d("ACCELERO","FAIL CREATING FILE");
        }


    }
    public Vector<Float[]> read(String fileName){

        String line = null;
        /*List<Float> alX=new ArrayList<Float>();List<Float> alY=new ArrayList<Float>();*/List<Float> alZ=new ArrayList<Float>();
        List<Float> Time=new ArrayList<Float>();

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {   String[] tokens=line.split("\t");
                Time.add(Float.valueOf(tokens[0]));
/*                alX.add(Float.valueOf(tokens[1]));
                alY.add(Float.valueOf(tokens[2]));*/
                alZ.add(Float.valueOf(tokens[3]));
//                Log.d("ACCELEROO",tokens[0]+" "+tokens[1]+" "+tokens[2]);
//                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
//            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d("ACCELERO", ex.getMessage());
        }catch(Exception e){
            Log.d("ACCELERO",e.getMessage());
        }

        Vector<Float[]> v=new Vector<Float[]>();
        v.add(Time.toArray(new Float[Time.size()]));
        /*v.add(alX.toArray(new Float[alX.size()]));
        v.add(alY.toArray(new Float[alY.size()]));*/
        v.add(alZ.toArray(new Float[alZ.size()]));
        return v;
    }

}
