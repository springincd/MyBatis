package org.ff.domain;

public class StudentWithCard {
    private Student student;
    private int id;
    private int number;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "StudentWithCard{" +
                "student=" + student +
                ", id=" + id +
                ", number=" + number +
                '}';
    }
}
