package bayern.mimo.masterarbeit.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import bayern.mimo.masterarbeit.R;

/**
 * Created by MiMo on 02.04.2017.
 */

public class MADialog extends Dialog implements AdapterView.OnItemSelectedListener {

    private List<String> categories;
    private Button buttonPositive;
    private Button buttonNegative;

    public MADialog(Context context, View.OnClickListener positive, View.OnClickListener negative) {
        super(context);
        setContentView(R.layout.dialog_ma);

        buttonPositive = (Button) findViewById(R.id.dialogButtonOK);
        buttonNegative = (Button) findViewById(R.id.dialogButtonCancel);

        if (positive != null)
            buttonPositive.setOnClickListener(positive);
        else
            buttonPositive.setVisibility(View.GONE);


        if(negative != null)
            buttonNegative.setOnClickListener(negative);
        else
            buttonNegative.setVisibility(View.GONE);

        categories = new ArrayList<>();
        categories.add("S0");
        categories.add("S1");
        categories.add("S2");
        categories.add("S3");
        categories.add("S4");
        categories.add("S5");

        //  buttonPositive.setTag(categories.get(0));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.dialogSpinner);
        spinner.setOnItemSelectedListener(this);

        spinner.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if(buttonPositive != null)
            buttonPositive.setTag(this.categories.get(position));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //TODO mal schaun
    }
}
