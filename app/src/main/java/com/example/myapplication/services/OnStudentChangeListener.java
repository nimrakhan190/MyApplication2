package com.example.myapplication.services;

import com.example.myapplication.models.Student;

public interface OnStudentChangeListener {
    void onStudentChange();
    void onStudentEdit(Student student);
}
