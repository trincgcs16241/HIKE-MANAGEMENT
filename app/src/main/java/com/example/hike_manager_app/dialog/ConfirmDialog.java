package com.example.hike_manager_app.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hike_manager_app.R;
import com.example.hike_manager_app.adapter.ObservationAdapter;
import com.example.hike_manager_app.database.DatabaseHandler;
import com.example.hike_manager_app.model.ObservationItem;
import com.example.hike_manager_app.model.HikeModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ConfirmDialog extends Dialog {
    private Boolean isShow = false;
    private String name, location, date, isParkingAvailable, length, level, description;
    private TextView titleConfirm, nameTv, locationTv, dateTv, isParkingTv, lengthTv, levelTv, descriptionTv, expenseListLabelTv;

    private Button btnCancel, btnConfirm, btnAddImage;

    private ImageView imageView;
    private RecyclerView rvExpenseItem;
    private ObservationAdapter observationAdapter;

    private ArrayList<ObservationItem> arrayList = new ArrayList<>();

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    private Fragment fragment;

    private String id;
    private String image;

    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private OnAddExpenseListener onAddExpenseListener;

    public ConfirmDialog(@NonNull Context context,
                         Fragment fragment,
                         String name,
                         String location,
                         String date,
                         String isParkingAvailable,
                         String length,
                         String level,
                         String description,
                         String id,
                         String image,
                         ArrayList<ObservationItem> arrayList) {
        super(context);
        this.fragment = fragment;

        this.name = name;
        this.location = location;
        this.date = date;
        this.isParkingAvailable = isParkingAvailable;
        this.description = description;
        this.length = length;
        this.level = level;

        this.image = image;
        this.id = id;
        this.arrayList = arrayList;
    }

    public void setOnAddExpenseListener(OnAddExpenseListener onAddExpenseListener) {
        this.onAddExpenseListener = onAddExpenseListener;
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_dialog);

        titleConfirm = findViewById(R.id.titleConfirm);

        nameTv = findViewById(R.id.nameValue);
        locationTv = findViewById(R.id.destinationValue);
        dateTv = findViewById(R.id.dateValue);
        isParkingTv = findViewById(R.id.isParkingValue);
        lengthTv = findViewById(R.id.lengthValue);
        levelTv = findViewById(R.id.levelValue);
        descriptionTv = findViewById(R.id.descriptionValue);
        expenseListLabelTv = findViewById(R.id.expenseListLabel);

        imageView = findViewById(R.id.imageView);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnAddImage = findViewById(R.id.btnAddImage);
        rvExpenseItem = findViewById(R.id.rv_expense_item);
        observationAdapter = new ObservationAdapter(getContext(), arrayList);
        rvExpenseItem.setAdapter(observationAdapter);

        if (arrayList.isEmpty()) {
            expenseListLabelTv.setVisibility(View.GONE);
            rvExpenseItem.setVisibility(View.GONE);
        } else {
            expenseListLabelTv.setVisibility(View.VISIBLE);
            rvExpenseItem.setVisibility(View.VISIBLE);
        }

        if (isShow) {
            titleConfirm.setText("Expense Detail");
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnAddImage.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> storeToDB());

        nameTv.setText(name);
        locationTv.setText(location);
        dateTv.setText(date);
        isParkingTv.setText(isParkingAvailable);
        lengthTv.setText(length + " km");
        levelTv.setText(level);
        descriptionTv.setText(description);


        if (!image.equals("")) {
            btnAddImage.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(image);
            imageView.setImageBitmap(bitmap);

        } else {
            imageView.setVisibility(View.GONE);
        }

        btnAddImage.setOnClickListener(v -> pickImage());

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void storeToDB() {
        try {
            DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
            HikeModel hikeModel = new HikeModel(
                    name,
                    location,
                    date,
                    isParkingAvailable,
                    length,
                    level,
                    description,
                    "null"
            );
            databaseHandler.addHikeModel(hikeModel);
            dismiss();
            onAddExpenseListener.onSuccess();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void updateImage(Uri uri) {
        btnAddImage.setVisibility(View.GONE);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            Uri tempUri = getImageUri(getContext(), bitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            Log.d("TAG", "onActivityResult: ");

            DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
            databaseHandler.updateHikeImage(finalFile.getPath(), id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);


    }

    public interface OnAddExpenseListener {
        void onSuccess();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
