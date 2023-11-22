package com.example.hike_manager_app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hike_manager_app.adapter.HikeAdapter;
import com.example.hike_manager_app.database.DatabaseHandler;
import com.example.hike_manager_app.dialog.AddObservationDialog;
import com.example.hike_manager_app.model.HikeModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainFragment extends Fragment {

    private HikeAdapter hikeListAdapter;
    private final ArrayList<HikeModel> hikeModelArrayList = new ArrayList<>();

    DatabaseHandler appDatabaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabaseHandler = new DatabaseHandler(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<HikeModel> list = appDatabaseHandler.getAllHikeModel();
        hikeListAdapter.onDataChange(list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MaterialButton buttonClearAllDatabase;
        MaterialButton buttonAddNewHike;
        EditText searchFieldEditTex;
        RecyclerView recyclerViewHikeList;
        super.onViewCreated(view, savedInstanceState);

        recyclerViewHikeList = view.findViewById(R.id.listExpenseRv);
        searchFieldEditTex = view.findViewById(R.id.searchEdt);
        MaterialButton buttonSearchHikeByName = view.findViewById(R.id.btnSearch);
        buttonClearAllDatabase = view.findViewById(R.id.btnResetDatabase);
        MaterialButton buttonRefreshSearchInput = view.findViewById(R.id.btnRefreshSearch);

        hikeListAdapter = new HikeAdapter(
                getContext(),
                hikeModelArrayList,
                new HikeAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(String id) {
                        MainFragmentDirections.ActionMainFragmentToExpenseDetailFragment action = MainFragmentDirections.actionMainFragmentToExpenseDetailFragment();
                        action.setHikeUUID(id);
                        Navigation.findNavController(view).navigate(action);
                    }

                    @Override
                    public void onEditClick(String id) {
                        AddObservationDialog addObservationDialog = new AddObservationDialog(
                                requireContext(),
                                id
                        );
                        addObservationDialog.setOnAddExpenseListener(addObservationDialog::dismiss);

                        addObservationDialog.show();

                        Window window = addObservationDialog.getWindow();
                        window.setBackgroundDrawable(null);
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    @Override
                    public void onDeleteClick(String id) {
                        //Show dialog confirm
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Delete")
                                .setMessage("Do you really want to delete this hike?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                    appDatabaseHandler.deleteHike(id);
                                    hikeListAdapter.onDataChange(appDatabaseHandler.getAllHikeModel());
                                })
                                .setNegativeButton(android.R.string.no, (dialog1, which) -> dialog1.dismiss()).show();
                    }
                }
        );
        recyclerViewHikeList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewHikeList.setAdapter(hikeListAdapter);

        buttonAddNewHike = view.findViewById(R.id.btnAddNewExpense);
        buttonAddNewHike.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_addExpenseFragment));
        buttonSearchHikeByName.setOnClickListener(v -> {
            View view1 = requireActivity().getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (!searchFieldEditTex.getText().toString().equals("")) {
                String searchContentInput = searchFieldEditTex.getText().toString().trim().toLowerCase(Locale.ROOT);
                HikeModel hikeModel = appDatabaseHandler.getHikeByName(searchContentInput);
                if (hikeModel != null) {
                    ArrayList<HikeModel> list = new ArrayList<>();
                    list.add(hikeModel);
                    hikeListAdapter.onDataChange(list);
                } else {
                    Toast.makeText(requireContext(), "No HIKE found", Toast.LENGTH_SHORT).show();
                }

            } else {
                ArrayList<HikeModel> list = appDatabaseHandler.getAllHikeModel();
                hikeListAdapter.onDataChange(list);
            }
        });

        buttonClearAllDatabase.setOnClickListener(v -> {
            View currentFocus = requireActivity().getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            appDatabaseHandler.resetDatabase();
            ArrayList<HikeModel> list = appDatabaseHandler.getAllHikeModel();
            hikeListAdapter.onDataChange(list);
            buttonClearAllDatabase.setVisibility(View.GONE);
        });

        buttonRefreshSearchInput.setOnClickListener(v -> {
            View currentFocus = requireActivity().getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            searchFieldEditTex.setText("");
            ArrayList<HikeModel> list = appDatabaseHandler.getAllHikeModel();
            hikeListAdapter.onDataChange(list);
        });

        if (!appDatabaseHandler.getAllHikeModel().isEmpty()) {
            buttonClearAllDatabase.setVisibility(View.VISIBLE);
        } else {
            buttonClearAllDatabase.setVisibility(View.GONE);
        }
    }
}