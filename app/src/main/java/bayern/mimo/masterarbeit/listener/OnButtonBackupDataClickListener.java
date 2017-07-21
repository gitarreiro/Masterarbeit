package bayern.mimo.masterarbeit.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import bayern.mimo.masterarbeit.activity.BackupDataActivity;
import bayern.mimo.masterarbeit.activity.ShowAndUploadDataActivity;

/**
 * Created by MiMo on 21.07.2017.
 */

public class OnButtonBackupDataClickListener implements View.OnClickListener {

    private Context context;

    public OnButtonBackupDataClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, BackupDataActivity.class);
        this.context.startActivity(intent);
    }
}
