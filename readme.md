# Mybatis学习（参考：我没有三颗心脏博客）

Mybatis01: Mybatis的入门使用，crud操作

Mybatis02: 注解，高级查询

Mybatis03：高级映射，resultType, resultMap



## 参考：

[MyBatis(2)——MyBatis 深入学习](https://www.cnblogs.com/wmyskxz/p/8877109.html)

[Mybatis最新完整教程IDEA版通俗易懂](https://www.bilibili.com/video/BV1NE411Q7Nx?p=12)

## 学习内容：

- crud操作--ok
- 使用注解开发Mybatis--ok

- MyBatis高级映射：执行多表查询，复杂sql查询

- Mapper动态代理--ok

- 配置log4j日志输出

- MyBatis缓存原理
- Maven资源文件拷贝 --ok



## Mapper动态代理

### 定义：

​	Mapper动态代理：在web开发过程中为了加快dao层的开发，将sql语句进行剥离，只需要定义与数据进行交互的接口Mapper + Mapper对应的映射文件（或者在接口方法使用注解）；

​	可以实现在开发的过程中，准确的说是在框架构建的过程中不分心到sql语句的编写只需要指导在数据交互的过程中需要传递什么数据，获得什么数据，具体的逻辑可以统一去进行编写。

### 实现步骤：

- 定义Mapper接口：包括数据操作方法；
- 将对应的Mapper接口再mybatis.xml中进行配置
- 通过Mapper.xml映射文件，或者注解的方式编写sql

### 相关代码：

StudentMapper.java （使用注解的方式编写sql代码）

```java
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
}
```

mybatis.xml：mybatis配置文件

```xml
<!--省略DOCTYPE-->
<configuration>    
    <!--省略配置文件-->
    <mappers>
        <mapper class="org.ff.dao.StudentMapper"/>
    </mappers>
</configuration>
```

MybatisUtil: 获得sqlSession工具类

```java
package org.ff.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MybatisUtil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
```

测试代码：

```java
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
    public void listStudentWithMybatisUtil(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> students = mapper.listStudent();
        for (Student student : students) {
            System.out.println(student);
        }
    }
    //省略其他crud操作
}
```

### 注意：

- 如果使用注解的方式编写sql语句，那么对应的Mapper.xml映射文件可以不写，mybatis中的mapper设置class属性。



## Maven资源文件拷贝

​	java包下*Mapper.xml文件拷贝到target目录，如果Mapper.xml映射文件添加在dao包下，不添加拷贝资源的脚本，会导致Resources.getResourceAsStream的时候找不到mapper.xml映射文件，在对应的target目录下没有对应的资源文件。

​	也可以直接将Mapper.xml映射文件直接写在Resources资源目录下，这样就不需要添加拷贝脚本（但是Mapper.java和Mapper.xml写在不同的地方不方便查找与管理）

```xml
<project>
	<build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
</project>
```



## MyBatis高级映射

### 目标：

- 创建数据库，创建符合关联关系的表
- 一对一关系查询
  - 返回类型resultType--通过继承父类扩展属性，设置子类为resultType的值
  - 返回类型resultMap--一样的是使用子类，只是子类数据的填充是自己定义resultMap
- 一对多查询
  - 通过resultMap配置返回结果，定义返回结果的包装类
- 多对查询
  - 和一对多查询一样

### 数据库创建：

```sql
-- 创建一对一关系 学生表与身份ID表
DROP DATABASE IF EXISTS mybatis;
CREATE DATABASE mybatis DEFAULT CHARACTER SET utf8;

USE mybatis;
CREATE TABLE student(
	id int(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	card_id int(11) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE card(
	id int(11) NOT NULL AUTO_INCREMENT,
	number int(11) NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO card VALUES(1,1111);
INSERT INTO card VALUES(2,2222);

INSERT INTO student	VALUES(1,'student1',1);
INSERT INTO student	VALUES(2,'student2',2);

SELECT * FROM student;
SELECT * FROM card;

-- 创建一对多关系 部门表department(id,name)和员工表employee(id,name,department_id)
CREATE TABLE department(
	id int(11) AUTO_INCREMENT,
	`name` VARCHAR(255),
	PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE employee(
	id int(11) AUTO_INCREMENT,
	`name` VARCHAR(255),
	department_id int(11),
	PRIMARY KEY(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO department VALUES(1,'d1');
INSERT INTO department VALUES(2,'d2');

INSERT INTO employee VALUES(1,'e1',1);
INSERT INTO employee VALUES(2,'e2',1);
INSERT INTO employee VALUES(3,'e3',2);

SELECT * from employee;
SELECT * from department;

-- 查询指定部门下所有员工
SELECT employee.*
FROM employee, department
WHERE employee.department_id = department.id
	AND department.`name` = 'd1'
```



### 一对一关系：

#### 实现步骤

- 创建数据库，创建工程，创建对应的包，配置mybatis.xml配置文件
- 创建对应的实体类，创建mapper.xml
- 编写sql，修改Mapper.xml，测试
- 测试返回值类型：resultType，resultMap

#### 返回值类型：resultType

Mapper.xml中resultType的值StudentCard对象的属性和select查询的结果集的字段需要对应上，如果无法对应上会导致赋值失败，查询的结果集只是增加了字段，StudentAndCard继承自Student，添加上对应多余的字段。

相关代码：Student.java, Card.java, StudentAndCard.java, StudentMapper.xml，

```java
package org.ff.domain;

public class Student {
    private int id;
    private String name;
    private int card_id;

    /* getter and setter */
}
```

```java
package org.ff.domain;

public class Card {
    private int id;
    private int number;

    /* getter and setter */
}
```

```java
package org.ff.domain;

public class StudentAndCard extends Student{
    private int number;

    /* getter and setter */
}
```

```xml
<?xml version="1.0" encoding="UTF8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.ff.dao.StudentMapper">
    <!--返回值类型 resultType -->
    <select id="findStudentByCard" parameterType="_int" resultType="StudentAndCard">
        -- 查询sql
        SELECT student.*, card.number AS card_number
        FROM student, card
        where student.card_id = card.id
          AND card.number = 1111
    </select>
</mapper>
```

```java
@Test
public void findStudentByCardTest(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    StudentAndCard studentAndCard = sqlSession.selectOne("findStudentByCard",1111);
    System.out.println(studentAndCard);

}
```

#### 返回值类型为：resultmap

相关代码：StudentWithCard.java, StudentMapper.xml

```java
package org.ff.domain;

public class StudentWithCard {
    private Student student;
    private int id;
    private int number;

    /* getter and setter */
}
```

```xml
<?xml version="1.0" encoding="UTF8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.ff.dao.StudentMapper">
    <select id="findStudentByCard" parameterType="_int" resultMap="studentInfoMap">
        -- 查询sql
        SELECT student.*, card.number AS card_number
        FROM student, card
        where student.card_id = card.id
          AND card.number = 1111
    </select>

    <resultMap id="studentInfoMap" type="StudentWithCard">
        <!--关键字：id-主键 result-结果集其他字段 column-结果集字段名 property-实体类属性名-->
        <id column="id" property="id"/>
        <result column="number" property="number"/>
        <!--关键字： association-表示关联的类，property-实体类属性名 javaType-实体类类名-->
        <association property="student" javaType="Student">
            <id column="id" property="id"/>
            <result column="name" property="name"/>
            <result column="card_id" property="card_id"/>
        </association>
    </resultMap>

</mapper>
```

```java
@Test
public void findStudentWithCardTest(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    StudentWithCard studentWithCard = sqlSession.selectOne("findStudentWithCard",1111);
    System.out.println(studentWithCard);

}
```

resultMap优点：

- 当结果集中的字段名与实体类中的字段名不一致的时候，可以通过配置resultmap进行转换；
- 在处理属性是引用数据类型的实体类的时候适用

### 一对多关系：

#### 实现步骤：

- 建表，创建对应的实体类，编写Mapper.xml，返回值类型使用reultType, resultMap都行

相关代码：Employee.java, Department.java, EmployeeMapper.xml

```java
package org.ff.domain;

public class Employee {
    private int id;
    private String name;
    private int department_id;

    /* getter and setter */
}
```

```java
package org.ff.domain;

public class Department {
    private int id;
    private String name;

    /* getter and setter */
}
```

```xml
<?xml version="1.0" encoding="UTF8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.ff.dao.EmployeeMapper">
    <select id="listEmployeeByDepartmentName" parameterType="java.lang.String" resultType="Employee">
        SELECT employee.*
        FROM employee, department
        WHERE employee.department_id = department.id
          AND department.`name` = #{value}
    </select>
</mapper>
```

### 多对多关系：



## 问题：

新增数据的时候遇到中文乱码问题？

**发现可以有2种方式去执行sql查询语句：**

- 为对应的查询语句创建mapper接口，通过sqlSession对象拿到mapper对象，调用对应的接口
- 直接拿到sqlSession对象，通过注册的*Mapper.xml文件，使用对应的sql语句Id

**关于*Mapper.java(接口)和 *Mapper.xml（sql查询语句）的引用问题？**

```xml
<mappers>
    <mapper class="org.org.ff.dao.StudentMapper"/>
    <!--<mapper resource="org/org.ff/dao/StudentMapper.xml"/>-->
</mappers>
```

StudentMapper.java

```java
package org.org.ff.dao;

import org.org.ff.domain.Student;

import java.util.List;

public interface StudentMapper {
    List<Student> listStudent();

    int addStudent(Student stu);

    int updateStudent(Student stu);

    int deleteStudent(Student stu);
}
```

 StudentMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.org.ff.dao.StudentMapper">
    <select id="listStudent" resultType="Student">
        select * from student
    </select>
    <insert id="addStudent" parameterType="Student">
        insert into student values(null,#{studentID},#{name})
    </insert>
    <update id="updateStudent" parameterType="Student">
        update student set studentID=#{studentID}, `name`=#{name} where id=#{id};
    </update>
    <delete id="deleteStudent" parameterType="Student">
        delete from student where id = #{id}
    </delete>
</mapper>
```

​	在mybatis.xml配置文件中mappers的mapper使用class方式引用或者使用resource方式引用都是一样的，只要StudentMapper.java 中的方法名和 StudentMapper.xml 中sql语句的id对应上就行。

​	执行sql语句的时候有2种方式：

```java
//拿到对应的*Mapper接口的实例化对象，调用相关sql方法
@Test
public void listStudent2(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
    List<Student> students = mapper.listStudent();
    for (Student student : students) {
        System.out.println(student);
    }
}

//直接通过*Mapper.xml中sql对应的id执行sql语句
@Test
public void listStudentByXmlTest(){
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    List<Student> students = sqlSession.selectList("listStudent");
    for (Student student : students) {
        System.out.println(student);
    }
}
```

***Mapper.xml注意事项：**

mapper.xml中namespace需要和对应的接口名一致，resultType在mybatis配置文件中如果配置了别名，就可以只写类名就行，没有配置别名就使用完全限定名称

**轻量级数据表设计可视化工具**