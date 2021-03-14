package org.ff.dao;

import org.ff.domain.Student;

import java.util.List;

public interface StudentMapper {
    List<Student> listStudent();

    int addStudent(Student stu);

    int updateStudent(Student stu);

    int deleteStudent(Student stu);
}
