package ted.bluetoothsensorreader;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ted on 11/16/2015.
 */
public class RadonSensorCSVWriter {

    private static final String TAG = "CSVWriter";
    private static final String attributes = "Timestamp,Temperature,Humidity,Light,UV,Radon,Home Location";

    //Assuming the structure of the received file is
    //First line: File name
    //Second line and onwards: contents of the file

    public static void writeCSV(String streamContents, double latitude, double longitude){
        String latLongFormat = String.format("(%f, %f)", latitude, longitude);

        String [] splitContents = streamContents.split("\n");
        String filename = splitContents[0];

        String root = Environment.getExternalStorageDirectory().toString();
        File csvDir = new File(root + "/IntelEdisonRadon");
        csvDir.mkdir();
        File file = new File (csvDir, filename);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(attributes);
            writer.newLine();
            for (int i = 1; i<splitContents.length; i++){
                writer.write(splitContents[i] + "," +latLongFormat);
                writer.newLine();
                writer.flush();
            }
            writer.close();
        }
        catch(IOException e){
            Log.wtf(TAG, "IOException", e);
        }
        Log.d(TAG, String.format("File is at: %s", file.getAbsolutePath()));
    }
}

