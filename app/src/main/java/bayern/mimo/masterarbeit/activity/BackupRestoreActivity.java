package bayern.mimo.masterarbeit.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import bayern.mimo.masterarbeit.R;
import bayern.mimo.masterarbeit.util.Config;

/**
 * Created by MiMo
 */

public class BackupRestoreActivity extends AppCompatActivity {

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
                System.out.println("restore button clicked");
                restoreData();
            }
        });
    }

    private void backupData() {
        // TODO backup data

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String packageName = getApplicationContext().getPackageName();

                System.out.println("package name is " + packageName);

                String liveDBPath = "//data//" + packageName + "//databases//" + Config.DB_NAME;
                String backupDBPath = Config.DB_NAME;
                File liveDB = new File(data, liveDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(liveDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "backup successful from " + liveDB.toString() + " to " + backupDB.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void restoreData() {
        // TODO restore data

        System.out.println("starting restore");

        try {

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            //System.out.println("data can write? " + data.canWrite());

            //if (data.canWrite()) {
            String packageName = getApplicationContext().getPackageName();

            System.out.println("package name is " + packageName);

            String backupDBPath = Config.DB_NAME;
            String liveDBPath = "//data//" + packageName + "//databases//" + Config.DB_NAME;
            File backupDB = new File(sd, backupDBPath);
            File liveDB = new File(data, liveDBPath);

            FileChannel src = new FileInputStream(backupDB).getChannel();
            FileChannel dst = new FileOutputStream(liveDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(getBaseContext(), "restored successfully from " + backupDB.toString() + " to " + liveDB.toString(), Toast.LENGTH_LONG).show();
            //}

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
