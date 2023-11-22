package com.example.hike_manager_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.hike_manager_app.database.DatabaseHandler;
import com.example.hike_manager_app.model.HikeModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditHikeFragment extends Fragment {

    private EditText nameEdt;
    private EditText locationEdt;
    private EditText lengthEdt;
    private EditText levelEdt;
    private EditText descriptionEdt;
    private TextView dateEdt;


    private boolean isParkingAvailable = false;

    private DatabaseHandler databaseHandler;

    final Calendar myCalendar = Calendar.getInstance();


    public EditHikeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_hike, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String uuid = EditHikeFragmentArgs.fromBundle(getArguments()).getHikeUUID();
        databaseHandler = new DatabaseHandler(requireContext());
        HikeModel hikeModel = databaseHandler.getHikeByUUID(uuid);


        Button btnSave;
        SwitchCompat isParkingAvailableSwitch;
        nameEdt = view.findViewById(R.id.nameEdt); //Required
        locationEdt = view.findViewById(R.id.locationEdt); //Required
        dateEdt = view.findViewById(R.id.dateEdt); //Required
        isParkingAvailableSwitch = view.findViewById(R.id.isParkingAvailable); //Required
        lengthEdt = view.findViewById(R.id.lengthEdt); //Required
        levelEdt = view.findViewById(R.id.levelEdt); //Required
        descriptionEdt = view.findViewById(R.id.descriptionEdt); //Optional

        btnSave = view.findViewById(R.id.btnSave);

        nameEdt.setText(hikeModel.getName());
        locationEdt.setText(hikeModel.getLocation());
        locationEdt.setText(hikeModel.getLocation());
        dateEdt.setText(hikeModel.getDate());
        lengthEdt.setText(hikeModel.getLength());
        levelEdt.setText(hikeModel.getLevel());
        descriptionEdt.setText(hikeModel.getDescription());
        boolean isCheck = false;
        if (hikeModel.getIsParkingAvailable().equals("Yes")) {
            isCheck = true;
        } else {
            isCheck = false;
        }
        isParkingAvailableSwitch.setChecked(isCheck);

        DatePickerDialog.OnDateSetListener date = (view1, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "MM/dd/yy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
            dateEdt.setText(dateFormat.format(myCalendar.getTime()));
        };

        dateEdt.setOnClickListener(v -> new DatePickerDialog(getContext(),
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        isParkingAvailableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isParkingAvailable = isChecked);

        btnSave.setOnClickListener(v -> {
            String name = nameEdt.getText().toString();
            String location = locationEdt.getText().toString();
            String dateString = dateEdt.getText().toString();
            String length = lengthEdt.getText().toString();
            String level = levelEdt.getText().toString();
            String desc = descriptionEdt.getText().toString();


            if (name.equals("")
                    || location.equals("")
                    || dateString.equals("")
                    || length.equals("")
                    || level.equals("")
            ) {
                Toast.makeText(getContext(), "Please fill to required field", Toast.LENGTH_SHORT).show();
            } else {
                HikeModel hikeModel1 = new HikeModel(
                        uuid,
                        name,
                        location,
                        dateString,
                        isParkingAvailable ? "Yes" : "No",
                        length,
                        level,
                        desc,
                        ""
                );
                databaseHandler.updateHike(hikeModel1, uuid);
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }
}