package com.example.myapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityStudentAddBinding;
import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class StudentAddActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "cust_tag";
    private ActivityStudentAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String name = binding.editTextName.getText().toString().trim();
        String ageStr = binding.editTextAge.getText().toString().trim();
        String rollNoStr = binding.editTextRollNo.getText().toString().trim();
        String title = binding.editTextTitle.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || rollNoStr.isEmpty() || title.isEmpty()) {
            Snackbar.make(binding.getRoot(), "All fields are required", Snackbar.LENGTH_LONG).show();
            return;
        }

        int age, rollNo;
        try {
            age = Integer.parseInt(ageStr);
            rollNo = Integer.parseInt(rollNoStr);
        } catch (NumberFormatException e) {
            Snackbar.make(binding.getRoot(), "Age and Roll No must be numbers", Snackbar.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> student = new HashMap<>();
        student.put("name", name);
        student.put("roll_no", rollNo);
        student.put("age", age);
        student.put("title", title);

        db.collection("students")
                .add(student)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Record added with ID: " + documentReference.getId());
                    Snackbar.make(binding.getRoot(), "Student added successfully", Snackbar.LENGTH_LONG).show();
                    clearInputFields();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Snackbar.make(binding.getRoot(), "Failed to add student", Snackbar.LENGTH_LONG).show();
                });
    }

    private void clearInputFields() {
        binding.editTextName.setText("");
        binding.editTextAge.setText("");
        binding.editTextRollNo.setText("");
        binding.editTextTitle.setText("");
    }
}
