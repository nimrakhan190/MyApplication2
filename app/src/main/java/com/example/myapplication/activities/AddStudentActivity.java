package com.example.myapplication.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextName, editTextAge, editTextRollNo, editTextTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextRollNo = findViewById(R.id.editTextRollNo);
        editTextTitle = findViewById(R.id.editTextTitle);

        // Initialize and set click listener for backButton
        Button backButton = findViewById(R.id.btn_back_to_main);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddStudentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize and set click listener for saveButton
        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from EditText fields
                String name = editTextName.getText().toString();
                String rollNo = editTextRollNo.getText().toString();
                String age = editTextAge.getText().toString();
                String title = editTextTitle.getText().toString();

                // Store to Firebase
                Map<String, Object> student = new HashMap<>();
                student.put("name", name);
                student.put("roll_no", rollNo);
                student.put("age", age);
                student.put("title", title);

                // Add a new document with a generated ID
                db.collection("students")
                        .add(student)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Record added with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding document", e);
                        });

                // Clear the EditText fields
                editTextName.setText("");
                editTextAge.setText("");
                editTextRollNo.setText("");
                editTextTitle.setText("");
            }
        });
    }
}
