package bayern.mimo.masterarbeit.wrapper;

import android.bluetooth.BluetoothDevice;

/**
 * Created by MiMo on 14.03.2017.
 */

public class BluetoothDeviceWrapper {

    private BluetoothDevice device;
    private boolean isConnected = false;

    public BluetoothDeviceWrapper(BluetoothDevice device){
        this.device = device;
    }

    public BluetoothDeviceWrapper(BluetoothDevice device, boolean isConnected){
        this(device);
        this.isConnected = isConnected;
    }

    public void setConnected (boolean isConnected)  {
        this.isConnected = isConnected;
    }

    public boolean isConnected(){
        return this.isConnected;
    }

    public BluetoothDevice getDevice(){
        return this.device;
    }
}
