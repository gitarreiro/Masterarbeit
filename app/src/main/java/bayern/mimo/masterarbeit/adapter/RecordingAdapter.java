package bayern.mimo.masterarbeit.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.data.DataRecording;

/**
 * Created by MiMo
 */

public class RecordingAdapter extends ArrayAdapter<DataRecording> {

    private Context context;


    public RecordingAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View viewToUse = null;

        DataRecording record = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.recording_list_item, null); //TODO check (ViewHolder verwenden?)

        ViewHolder holder;
        if (viewToUse.getTag() == null)
            holder = new RecordingAdapter.ViewHolder();
        else
            holder = (ViewHolder) viewToUse.getTag();

        holder.textViewDate = (TextView) viewToUse.findViewById(R.id.dateTextView);
        holder.textViewTitle = (TextView) viewToUse.findViewById(R.id.titleTextView);
        holder.textViewSensors = (TextView) viewToUse.findViewById(R.id.textViewNumberSensors);
        holder.textViewDescription = (TextView) viewToUse.findViewById(R.id.textViewDescription);
        holder.layoutStatus = (RelativeLayout) viewToUse.findViewById(R.id.layoutItemUploadStatus);

        viewToUse.setTag(holder);

        holder.textViewDate.setText(record.getStartDate());
        holder.textViewTitle.setText(record.getCategory());
        holder.textViewSensors.setText("Used sensors: " + record.getSensorCount());
        holder.textViewDescription.setText(record.getDetail());

        if(record.isUploaded()){
            holder.layoutStatus.setBackgroundColor(context.getResources().getColor( R.color.greenDeviceConnected, null));
        }

        return viewToUse;
    }

    private class ViewHolder {
        private TextView textViewDate;
        private TextView textViewTitle;
        private TextView textViewSensors;
        private TextView textViewDescription;
        private RelativeLayout layoutStatus;
    }


}
