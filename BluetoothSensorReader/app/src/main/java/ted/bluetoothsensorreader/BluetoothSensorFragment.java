package ted.bluetoothsensorreader;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ted on 11/7/2015.
 */
public class BluetoothSensorFragment extends Fragment {
    private static final String TAG = "BluetoothSensorFragment";


    /**
     * Local bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//do I need this?

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Check if Bluetooth is supported on this device
        if (mBluetoothAdapter == null){
            Log.d(TAG, "Bluetooth not supported");
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    
}
