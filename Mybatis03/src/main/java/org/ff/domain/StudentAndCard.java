package org.ff.domain;

public class StudentAndCard extends Student{
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return super.toString() +  " StudentAndCard{" +
                "number=" + number +
                '}';
    }
}
