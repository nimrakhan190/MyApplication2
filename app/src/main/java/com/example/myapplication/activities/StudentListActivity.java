package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.StudentAdapter;
import com.example.myapplication.models.Student;
import com.example.myapplication.services.OnStudentChangeListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StudentListActivity extends AppCompatActivity implements OnStudentChangeListener {

    private static final String TAG = "StudentListActivity";
    private FirebaseFirestore db;
    private List<Student> studentList;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set click listener for backButton
        Button backButton = findViewById(R.id.btn_back_to_main);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentListActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Initialize Firestore and the student list
        db = FirebaseFirestore.getInstance();
        studentList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList, this);
        recyclerView.setAdapter(adapter);

        // Fetch students from Firestore
        fetchStudentsFromFirestore();
    }

    private void fetchStudentsFromFirestore() {
        studentList.clear();
        db.collection("students").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    Log.d(TAG, document.getId() + " => " + data);
                    studentList.add(new Student(
                            document.getId(),
                            Objects.requireNonNull(data.get("name")).toString(),
                            Integer.parseInt(Objects.requireNonNull(data.get("age")).toString()),
                            Integer.parseInt(Objects.requireNonNull(data.get("roll_no")).toString()),
                            Objects.requireNonNull(data.get("title")).toString()
                    ));
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    @Override
    public void onStudentChange() {
        fetchStudentsFromFirestore();
    }

    @Override
    public void onStudentEdit(Student student) {
        // Implement this method to handle editing a student
        // For example, start an edit activity and pass the student object
        Intent intent = new Intent(StudentListActivity.this, StudentEditActivity.class);
        intent.putExtra("std", student);
        startActivity(intent);
    }
}
