package com.example.hike_manager_app;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hike_manager_app.dialog.ConfirmDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddHikeFragment extends Fragment {
    private EditText hikeNameEdittext;
    private EditText hikeLocationEditText;
    private EditText hikeLengthEditText;
    private EditText hikeLevelEditText;
    private EditText hikeDescriptionEditText;
    private TextView hikeDateTextView;
    private boolean isParkingAvailable = false;

    final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_hike, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        Button buttonSubmit;
        SwitchCompat isParkingAvailableSwitch;
        hikeNameEdittext = view.findViewById(R.id.nameEdt); //Required
        hikeLocationEditText = view.findViewById(R.id.locationEdt); //Required
        hikeDateTextView = view.findViewById(R.id.dateEdt); //Required
        isParkingAvailableSwitch = view.findViewById(R.id.isParkingAvailable); //Required
        hikeLengthEditText = view.findViewById(R.id.lengthEdt); //Required
        hikeLevelEditText = view.findViewById(R.id.levelEdt); //Required
        hikeDescriptionEditText = view.findViewById(R.id.descriptionEdt); //Optional

        buttonSubmit = view.findViewById(R.id.btnAdd);
        buttonSubmit.setOnClickListener(v -> {
            String name = hikeNameEdittext.getText().toString();
            String location = hikeLocationEditText.getText().toString();
            String date = hikeDateTextView.getText().toString();
            String length = hikeLengthEditText.getText().toString();
            String level = hikeLevelEditText.getText().toString();

            if (name.equals("")
                    || location.equals("")
                    || date.equals("")
                    || length.equals("")
                    || level.equals("")
            ) {
                Toast.makeText(getContext(), "Please fill to required field", Toast.LENGTH_SHORT).show();
            } else {
                showConfirmDialog();
            }
        });

        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "MM/dd/yy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            hikeDateTextView.setText(dateFormat.format(calendar.getTime()));
        };

        hikeDateTextView.setOnClickListener(v -> new DatePickerDialog(getContext(),
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        isParkingAvailableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isParkingAvailable = isChecked);
    }

    private void showConfirmDialog() {
        String hikeName = hikeNameEdittext.getText().toString();
        String location = hikeLocationEditText.getText().toString();
        String date = hikeDateTextView.getText().toString();

        String length = hikeLengthEditText.getText().toString();
        String lvl = hikeLevelEditText.getText().toString();
        String description = hikeDescriptionEditText.getText().toString();


        final ConfirmDialog dialog = new ConfirmDialog(requireContext(),
                AddHikeFragment.this,
                hikeName,
                location,
                date,
                isParkingAvailable ? "Yes" : "No",
                length,
                lvl,
                description,
                "",
                "",
                new ArrayList<>());
        dialog.setCancelable(false);
        dialog.setOnAddExpenseListener(() -> Navigation.findNavController(requireView()).navigate(R.id.action_addExpenseFragment_to_mainFragment));
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}