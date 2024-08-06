package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityStudentEditBinding;
import com.example.myapplication.R;
import com.example.myapplication.models.Student;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class StudentEditActivity extends AppCompatActivity {

    private ActivityStudentEditBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "cust_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Student student = (Student) intent.getSerializableExtra("std");

        if(student != null) {
            binding.editTextNameStudentEdit.setText(student.name);
            binding.editTextAgeStudentEdit.setText(String.valueOf(student.age));
            binding.editTextRollNoStudentEdit.setText(String.valueOf(student.rollNo));
            binding.editTextTitleStudentEdit.setText(student.title);
        } else {
            Log.e(TAG, "Error in Std Data");
            Snackbar.make(binding.getRoot(), "Error loading student data", Snackbar.LENGTH_LONG).show();
        }

        setSupportActionBar(binding.toolbarStudentEdit);
        binding.fabStudentEdit.setOnClickListener(v -> editData(student.docId));
    }

    private void editData(String docId) {
        String name = binding.editTextNameStudentEdit.getText().toString().trim();
        String ageStr = binding.editTextAgeStudentEdit.getText().toString().trim();
        String rollNoStr = binding.editTextRollNoStudentEdit.getText().toString().trim();
        String title = binding.editTextTitleStudentEdit.getText().toString().trim();

        if(name.isEmpty() || ageStr.isEmpty() || rollNoStr.isEmpty() || title.isEmpty()) {
            Snackbar.make(binding.getRoot(), "All fields are required", Snackbar.LENGTH_LONG).show();
            return;
        }

        int age, rollNo;
        try {
            age = Integer.parseInt(ageStr);
            rollNo = Integer.parseInt(rollNoStr);
        } catch(NumberFormatException e) {
            Snackbar.make(binding.getRoot(), "Age and Roll No must be numbers", Snackbar.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> student = new HashMap<>();
        student.put("name", name);
        student.put("roll_no", rollNo);
        student.put("age", age);
        student.put("title", title);

        db.collection("students").document(docId)
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document successfully updated");
                    Snackbar.make(binding.getRoot(), "Student updated successfully", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating document", e);
                    Snackbar.make(binding.getRoot(), "Error updating student", Snackbar.LENGTH_LONG).show();
                });
    }
}
