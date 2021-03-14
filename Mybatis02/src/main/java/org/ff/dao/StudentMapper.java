package org.ff.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.ff.domain.Student;

import java.util.List;

public interface StudentMapper {
    @Select("select * from student")
    List<Student> listStudent();

    @Insert("insert into student values(null,#{studentID},#{name})")
    int addStudent(Student stu);

    @Update("update student set studentID=#{studentID}, `name`=#{name} where id=#{id}")
    int updateStudent(Student stu);

    @Delete("delete from student where id = #{id}")
    int deleteStudent(Student stu);

    @Select("select * from student where id = #{id}")
    Student findStudentById(int id);
}
