package com.codepath.simpletodo.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.simpletodo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {

    private static final String TAG = "SemerdaApp";

    int pos;
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private boolean isImportant;
    private boolean isUrgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit");

        // Item detail describing what needs to be done
        String detail = getIntent().getStringExtra("detail");
        this.pos = getIntent().getIntExtra("pos", 0);
        EditText editTextItem = (EditText)findViewById(R.id.etDetail);
        editTextItem.setText(detail);

        // Using Dwight Eisenhower’s urgency-importance decision matrix
        // Ref: http://www.gsdfaster.com/blog/how-to/dwight-eisenhowers-urgency-importance-decision-matrix/
        // ---
        isImportant = (getIntent().getIntExtra("isImportant", 0) == 1);
        Switch isImportantSwitch = (Switch)findViewById(R.id.sIsImportant);
        isImportantSwitch.setChecked(isImportant);

        isUrgent = (getIntent().getIntExtra("isUrgent", 0) == 1);
        Switch isUrgentSwitch = (Switch)findViewById(R.id.sIsUrgent);
        isUrgentSwitch.setChecked(isUrgent);
        // ---

        Integer dueDate = getIntent().getIntExtra("dueDate", 0);
        if (dueDate != 0) {
            Date dd = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            try {
                dd = sdf.parse(dueDate.toString());
            } catch(ParseException ex) {
                Log.w(TAG, ex);
            }
            calendar = Calendar.getInstance();
            calendar.setTime(dd);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            day++;
        }
        showDate(year, month, day);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Set Date", Toast.LENGTH_SHORT)
                .show();
    }

    public void showDate(int year, int month, int day){
        TextView tvDate = (TextView)findViewById(R.id.tvDate);
        String sd = month + "/" + day + "/" + year;
        tvDate.setText(sd);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, dateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2;
            day = arg3;
            showDate(year, month, day);
        }
    };

    public void onSaveItem(View v) {
        EditText detailTextItem = (EditText) findViewById(R.id.etDetail);
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        String dateStr = tvDate.getText().toString();
        Switch isImportantSwitch = (Switch)findViewById(R.id.sIsImportant);
        Switch isUrgentSwitch = (Switch)findViewById(R.id.sIsUrgent);

        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("detail", detailTextItem.getText().toString());
        data.putExtra("isImportant", (isImportantSwitch.isChecked() ? 1 : 0));
        data.putExtra("isUrgent", (isUrgentSwitch.isChecked() ? 1 : 0));
        data.putExtra("pos", this.pos);
        data.putExtra("dueDate", dateToInteger());
        data.putExtra("code", 200);
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    private Integer dateToInteger() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date dd = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyMMdd");
        Integer intDate = Integer.parseInt(dateFormat.format(dd));

        return intDate;
    }
}