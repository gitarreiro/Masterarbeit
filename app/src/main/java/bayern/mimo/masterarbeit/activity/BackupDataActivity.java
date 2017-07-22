package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.listener.OnButtonBackupDataClickListener;

/**
 * Created by MiMo on 21.07.2017.
 */

public class BackupDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup_data);

        Button buttonBackup = (Button) findViewById(R.id.buttonBackupData);
        buttonBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData();

            }
        });

        Button buttonRestore = (Button) findViewById(R.id.buttonRestoreData);
        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreData();
            }
        });
    }


    private void backupData(){
        // TODO bcakup data
    }

    private void restoreData(){
        // TODO restore data
    }

}
