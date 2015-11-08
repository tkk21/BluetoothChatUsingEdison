package ted.bluetoothsensorreader;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by ted on 11/7/2015.
 */
public class BluetoothSensorFragment extends Fragment {
    private static final String TAG = "BluetoothSensorFragment";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    //Layout views
    private Button mReceiveSensorsButton;
    private Button mConnectDeviceButton;

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
            initialize();
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
        //Covering edge case when app was paused before being able to onStart()

        //TODO might not need
        if (mBluetoothSensorService != null){
            if (mBluetoothSensorService.getState() == BluetoothSensorService.STATE_NONE){
                //mBluetoothSensorService.start();
            }
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_bluetooth_sensor_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState){
        mReceiveSensorsButton = (Button)view.findViewById(R.id.button_receive);
        mConnectDeviceButton = (Button)view.findViewById(R.id.button_connect);
    }

    private void initialize(){
        //initialize stuff
        mConnectDeviceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(intent, 0);

            }
        });
        mReceiveSensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send "start" to the server
            }
        });
        mBluetoothSensorService = new BluetoothSensorService(getActivity());
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
        mReceiveSensorsButton.setVisibility(View.VISIBLE);
    }





}
