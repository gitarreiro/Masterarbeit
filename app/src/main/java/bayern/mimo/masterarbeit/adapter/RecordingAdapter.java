package bayern.mimo.masterarbeit.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.data.DataRecording;

/**
 * Created by MiMo
 */

public class RecordingAdapter extends ArrayAdapter<DataRecording> {

    private Context context;


    public RecordingAdapter(Context context, List items){
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewToUse = null;

        DataRecording record = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.recording_list_item, null); //TODO check
        ViewHolder holder = new RecordingAdapter.ViewHolder();
        holder.textViewDate = (TextView)viewToUse.findViewById(R.id.dateTextView);
        holder.textViewSensors = (TextView) viewToUse.findViewById(R.id.texstViewNumberSensors);
        viewToUse.setTag(holder);

        holder.textViewDate.setText(record.getStartDate());
        holder.textViewSensors.setText("Used sensors: " + record.getSensorCount());

        return viewToUse;
    }


    private class ViewHolder{
        private TextView textViewDate;
        private TextView textViewSensors;
    }
}
