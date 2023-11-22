package com.example.hike_manager_app.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hike_manager_app.R;
import com.example.hike_manager_app.database.DatabaseHandler;
import com.example.hike_manager_app.model.ObservationItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddObservationDialog extends Dialog {
    private EditText observation;
    private EditText comment;
    private TextView time;
    private TextView dateTv;
    private Button btnConfirm;

    private String hikeUuid;

    private OnAddExpenseListener onAddExpenseListener;

    final Calendar myCalendar = Calendar.getInstance();

    public AddObservationDialog(@NonNull Context context, String hikeUuid) {
        super(context);
        this.hikeUuid = hikeUuid;
    }

    public void setOnAddExpenseListener(OnAddExpenseListener onAddExpenseListener) {
        this.onAddExpenseListener = onAddExpenseListener;
    }

    public AddObservationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddObservationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_observation_dialog);

        observation = findViewById(R.id.observation);
        time = findViewById(R.id.time);
        dateTv = findViewById(R.id.date);
        comment = findViewById(R.id.comment);

        btnConfirm = findViewById(R.id.btnAddExpense);
        btnConfirm.setOnClickListener(v -> storeToDB());

        time.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> time.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });

        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            dateTv.setText(dateFormat.format(myCalendar.getTime()));
        };

        dateTv.setOnClickListener(v -> new DatePickerDialog(getContext(),
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show());
    }

    private void storeToDB() {
        String observationString = observation.getText().toString();
        String timeString = time.getText().toString();
        String dateString = dateTv.getText().toString();
        String commentString = comment.getText().toString();

        if (observation.equals("") || timeString.equals("") || dateString.equals("")) {
            Toast.makeText(getContext(), "Please fill to all field", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
            ObservationItem observationItem = new ObservationItem(
                    hikeUuid,
                    observationString,
                    dateString,
                    timeString,
                    commentString
            );
            databaseHandler.addObservationItem(observationItem);
            dismiss();
            onAddExpenseListener.onSuccess();
        }
    }

    public interface OnAddExpenseListener{
        void onSuccess();
    }
}
