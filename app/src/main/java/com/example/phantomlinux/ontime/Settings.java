package com.example.phantomlinux.ontime;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity implements View.OnClickListener {


    private Button btnLockIn;
    private EditText txtIntakeCode;
    private String restoredIntakeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Init all views
        btnLockIn = (Button) findViewById(R.id.btnLockIn);
        txtIntakeCode = (EditText) findViewById(R.id.txtIntake);

        //Set edittext all caps
        txtIntakeCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        //Button set on click listener
        btnLockIn.setOnClickListener(this);

        //Check for shred pref intake code
        SharedPreferences prefs = getSharedPreferences("sharedpref", MODE_PRIVATE);
        restoredIntakeCode = prefs.getString("intakecode", null);

        //Log.v("log",""+restoredIntakeCode);

        if (restoredIntakeCode != null) {
            //Disable edit text field
            disableEditText(restoredIntakeCode);
            //txtIntakeCode.setEnabled(false);
        }

    }


    public void disableEditText(String input) {

        // Disable edit text
        txtIntakeCode.setText(input);
        txtIntakeCode.setEnabled(false);
        txtIntakeCode.setInputType(InputType.TYPE_NULL);
        btnLockIn.setText("Unlock");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLockIn:

                if (btnLockIn.getText() == "Unlock") {
                    //txtIntakeCode.setText(txtIntakeCode);
                    txtIntakeCode.setEnabled(true);
                    txtIntakeCode.setInputType(InputType.TYPE_CLASS_TEXT);
                    btnLockIn.setText("Lock In");
                    break;
                }

                if (txtIntakeCode.getText().length() == 0) {
                    txtIntakeCode.setError("Please enter your intake code before lock in.");
                    break;
                }

                if (txtIntakeCode.getText().length() > 0) {

                    String intakeCode = txtIntakeCode.getText().toString();
                    intakeCode = intakeCode.replace(" ","");
                    intakeCode = intakeCode.toUpperCase();
                    intakeCode = intakeCode.trim();

                    //Save intake code in shared preference
                    SharedPreferences.Editor editor = getSharedPreferences("sharedpref", MODE_PRIVATE).edit();
                    editor.putString("intakecode", intakeCode);
                    editor.commit();

                    //Disable edit text field
                    disableEditText(txtIntakeCode.getText().toString());
                    break;
                }

        }
    }
}
