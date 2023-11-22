package com.example.hike_manager_app;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hike_manager_app.adapter.ObservationAdapter;
import com.example.hike_manager_app.database.DatabaseHandler;
import com.example.hike_manager_app.model.HikeModel;
import com.example.hike_manager_app.model.ObservationItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HikeDetailFragment extends Fragment {

    private final int PICK_IMAGE_REQUEST = 1001;
    private final int REQUEST_WRITE_PERMISSION = 1002;
    DatabaseHandler appDatabaseHandler;
    private Button buttonImportImage;

    private ImageView hikeImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hike_detail, container, false);
    }

    private String uuid;

    @SuppressLint("WrongThread")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uuid = HikeDetailFragmentArgs.fromBundle(getArguments()).getHikeUUID();
        appDatabaseHandler = new DatabaseHandler(requireContext());
        HikeModel hikeModel = appDatabaseHandler.getHikeByUUID(uuid);
        ArrayList<ObservationItem> arrayList = appDatabaseHandler.getAllObservationItemByParentId(uuid);

        TextView nameTv = view.findViewById(R.id.nameValue);
        TextView locationTv = view.findViewById(R.id.locationValue);
        TextView dateTv = view.findViewById(R.id.dateValue);
        TextView isParkingTv = view.findViewById(R.id.isParkingValue);
        TextView lengthTv = view.findViewById(R.id.lengthValue);
        TextView levelTv = view.findViewById(R.id.levelValue);
        TextView descriptionTv = view.findViewById(R.id.descriptionValue);
        TextView imgLabel = view.findViewById(R.id.imgLabel);
        FloatingActionButton btnEdit = view.findViewById(R.id.btnEdit);


        hikeImageView = view.findViewById(R.id.imageView);
        buttonImportImage = view.findViewById(R.id.btnAddImage);
        RecyclerView rvObservation = view.findViewById(R.id.rvObservations);
        ObservationAdapter observationAdapter = new ObservationAdapter(getContext(), arrayList);

        rvObservation.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvObservation.setAdapter(observationAdapter);

        nameTv.setText(hikeModel.getName());
        locationTv.setText(hikeModel.getLocation());
        dateTv.setText(hikeModel.getDate());
        isParkingTv.setText(hikeModel.getIsParkingAvailable());
        lengthTv.setText(hikeModel.getLength());
        levelTv.setText(hikeModel.getLevel());
        descriptionTv.setText(hikeModel.getDescription());

        buttonImportImage.setOnClickListener(v -> pickImage());
        btnEdit.setOnClickListener(v -> {
            HikeDetailFragmentDirections.ActionExpenseDetailFragmentToEditHikeFragment action = HikeDetailFragmentDirections.actionExpenseDetailFragmentToEditHikeFragment();
            action.setHikeUUID(uuid);
            Navigation.findNavController(view).navigate(action);
        });

        String imagePath = hikeModel.getImage();
        if (!imagePath.isEmpty() && !imagePath.equals("null")) {
            imgLabel.setVisibility(View.VISIBLE);
            hikeImageView.setVisibility(View.VISIBLE);
            File imgFile = new  File(imagePath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                hikeImageView.setImageBitmap(myBitmap);
            }
        } else {
            imgLabel.setVisibility(View.GONE);
            hikeImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        appDatabaseHandler = new DatabaseHandler(getContext());
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String readImagePermission = Manifest.permission.READ_MEDIA_IMAGES;
            if (ContextCompat.checkSelfPermission(requireContext(), readImagePermission) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                requestPermissions(new String[]{readImagePermission}, REQUEST_WRITE_PERMISSION);
            }
            return;
        }

        boolean isGrantedPermission = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (isGrantedPermission) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String filePath = getRealPathFromURI(selectedImageUri);
            updateImage(selectedImageUri, filePath);
        }
    }

    public void updateImage(Uri uri, String filePath) {
        buttonImportImage.setVisibility(View.GONE);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            appDatabaseHandler.updateHikeImage(filePath, String.valueOf(uuid));
        } catch (IOException e) {
            appDatabaseHandler.close();
            e.printStackTrace();
        }
        hikeImageView.setVisibility(View.VISIBLE);
        hikeImageView.setImageBitmap(bitmap);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}