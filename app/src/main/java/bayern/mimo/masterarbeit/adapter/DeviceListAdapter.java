package bayern.mimo.masterarbeit.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.wrapper.BluetoothDeviceWrapper;

/**
 * Created by MiMo on 16.02.2017.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDeviceWrapper> {

    private Context context;
    private BluetoothAdapter bTAdapter;

    public DeviceListAdapter(Context context, List items, BluetoothAdapter bTAdapter) {
        super(context, android.R.layout.simple_list_item_1, items);  //TODO gleich richtiges Layout inflaten?!

        this.bTAdapter = bTAdapter;
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        private TextView titleText;
        //TODO sauber aufbauen
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        View line = null;
        BluetoothDeviceWrapper wrapper = getItem(position);
        BluetoothDevice device = wrapper.getDevice();
        final String name = device.getName();
        TextView macAddress = null;
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.device_list_item, null); //TODO check
        holder = new DeviceListAdapter.ViewHolder();
        holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
        viewToUse.setTag(holder);

        macAddress = (TextView)viewToUse.findViewById(R.id.macAddress);
        line = viewToUse.findViewById(R.id.line);
        holder.titleText.setText(device.getName());
        macAddress.setText(device.getAddress());

        if ( device.getName().equals("No Devices")) {
            macAddress.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);//TODO auslagern ins xml?!
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }

        if(wrapper.isConnected())
            holder.titleText.setTextColor(ContextCompat.getColor(context, R.color.greenDeviceConnected));

        return viewToUse;
    }


}