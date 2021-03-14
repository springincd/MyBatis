# Mybatis学习（参考：我没有三颗心脏博客）

Mybatis01: Mybatis的入门使用，crud操作

Mybatis02: 注解，高级查询



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

### 步骤：

- 创建数据库，创建符合关联关系的表
- 一对一关系查询
  - 返回类型resultType--通过继承父类扩展属性，设置子类为resultType的值
  - 返回类型resultMap--一样的是使用子类，只是子类数据的填充是自己定义resultMap
- 一对多查询
  - 通过resultMap配置返回结果，定义返回结果的包装类
- 多对查询
  - 和一对多查询一样



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

