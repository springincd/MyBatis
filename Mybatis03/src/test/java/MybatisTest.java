import org.apache.ibatis.session.SqlSession;
import org.ff.domain.Employee;
import org.ff.domain.StudentAndCard;
import org.ff.domain.StudentWithCard;
import org.junit.Test;
import org.ff.util.MybatisUtil;

import java.util.List;

public class MybatisTest {

    @Test
    public void findStudentByCardTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentAndCard studentAndCard = sqlSession.selectOne("findStudentByCard",1111);
        System.out.println(studentAndCard);

    }

    @Test
    public void findStudentWithCardTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentWithCard studentWithCard = sqlSession.selectOne("findStudentWithCard",1111);
        System.out.println(studentWithCard);

    }

    @Test
    public void listEmployeeByDepartmentNameTest(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        List<Employee> employees = sqlSession.selectList("listEmployeeByDepartmentName", "d1");
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }
}
