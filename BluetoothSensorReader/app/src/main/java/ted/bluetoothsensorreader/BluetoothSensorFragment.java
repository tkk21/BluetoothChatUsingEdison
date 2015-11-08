package ted.bluetoothsensorreader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Local bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSensorService mBluetoothSensorService;

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

    @Override
    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else {
            pickDevice();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothSensorService != null){
            mBluetoothSensorService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBluetoothSensorService != null){
            //resume the service
        }
    }

    private void pickDevice(){
        //initialize stuff
        mBluetoothSensorService = new BluetoothSensorService(getActivity());

        //launch intent to pick the device to connect
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            connectToDevice(data);
        }
    }


    private void connectToDevice(Intent data){
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothSensorService.connect(device);

    }





}
