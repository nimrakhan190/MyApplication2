package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapters.StudentAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.models.Student;
import com.example.myapplication.services.OnStudentChangeListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnStudentChangeListener {
    private ActivityMainBinding binding;
    private static final String TAG = "cust-tag";
    private List<Student> studentList = new ArrayList<>();
    private FirebaseFirestore db;
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize buttons
        Button addStudentButton = findViewById(R.id.btn_add_student);
        Button studentListButton = findViewById(R.id.btn_student_list);

        // Set up button click listeners
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
                startActivity(intent);
            }
        });

        studentListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentListActivity.class);
                startActivity(intent);
            }
        });

        // Setting up Recycler view
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList, this);
        binding.recyclerView.setAdapter(adapter);
    }

    public void setAdapter() {
        studentList.clear();
        db.collection("students").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    Log.d(TAG, document.getId() + " => " + data);
                    Student student = new Student(document.getId(),
                            Objects.requireNonNull(data.get("name")).toString(),
                            Integer.parseInt(Objects.requireNonNull(data.get("age")).toString()),
                            Integer.parseInt(Objects.requireNonNull(data.get("roll_no")).toString()),
                            Objects.requireNonNull(data.get("title")).toString());
                    studentList.add(student);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStudentChange() {
        setAdapter();
    }

    @Override
    public void onStudentEdit(Student student) {
        Intent intent = new Intent(MainActivity.this, StudentEditActivity.class);
        intent.putExtra("std", student);
        startActivity(intent);
    }
}
