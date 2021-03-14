package org.ff.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.ff.domain.Student;
import org.ff.util.MybatisUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    @Test
    public void listStudent() throws IOException {
        String resource = "mybatis.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> students = mapper.listStudent();
        for (Student stu :
                students) {
            System.out.println(stu);
        }
    }

    @Test
    public void listStudentWithMybatisUtil(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> students = mapper.listStudent();
        for (Student student : students) {
            System.out.println(student);
        }
    }

    @Test
    public void listStudentBySessionTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        List<Student> students = sqlSession.selectList("listStudent");
        for (Student student : students) {
            System.out.println(student);
        }
    }

    @Test
    public void insertStudentByMapperTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        Student student = new Student();
        student.setStudentID(12);
        student.setName("jack12");
        int rows = mapper.addStudent(student);
        System.out.println("effect rows: " + rows);
        sqlSession.commit(); //新增完之后需要手动进行提交
        sqlSession.close(); //执行sqlSession.commit()之后会自动执行close()吗？还是要手动的进行close()?
    }

    @Test
    public void updateStudentByMapperTest(){
        Student student = new Student();
        student.setId(5);
        student.setStudentID(5);
        student.setName("jack5");

        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        int effectRows = mapper.updateStudent(student);
        System.out.println("effectRows " + effectRows);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void deleteStudentByMapperTest(){
        Student student = new Student();
        student.setId(12);

        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        int effectRows = mapper.deleteStudent(student);
        System.out.println("effectRows " + effectRows);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void findStudentByIdTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        Student student = mapper.findStudentById(2);
        System.out.println("student " + student);
    }
}
