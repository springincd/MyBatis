# Mybatis学习（我没有三颗心脏）

Mybatis01: Mybatis的入门使用，crud操作

Mybatis02: 注解，高级查询



## 参考：

[MyBatis(2)——MyBatis 深入学习](https://www.cnblogs.com/wmyskxz/p/8877109.html)

[Mybatis最新完整教程IDEA版通俗易懂](https://www.bilibili.com/video/BV1NE411Q7Nx?p=12)

## 学习内容：

- crud操作--ok
- 使用注解开发Mybatis--ok

- MyBatis高级映射：执行多表查询，复杂sql查询

- 动态代理

- 配置log4j日志输出

- MyBatis缓存原理
- 关于*Mapper.xml文件拷贝到target目录
- MybatisUtil.java工具类，EntityMapper接口



**Maven资源文件拷贝**

maven还有资源过滤这么一个说法，最终是将资源拷贝到target目录下，不添加会导致找不到mapper.xml文件

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





## 问题：

新增数据的时候遇到中文乱码问题？

**发现可以有2种方式去执行sql查询语句：**

- 为对应的查询语句创建mapper接口，通过sqlSession对象拿到mapper对象，调用对应的接口
- 直接拿到sqlSession对象，通过注册的*Mapper.xml文件，使用对应的sql语句Id

**关于*Mapper.java(接口)和 *Mapper.xml（sql查询语句）的引用问题？**

```xml
<mappers>
    <mapper class="org.ff.dao.StudentMapper"/>
    <!--<mapper resource="org/ff/dao/StudentMapper.xml"/>-->
</mappers>
```

StudentMapper.java

```java
package org.ff.dao;

import org.ff.domain.Student;

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

<mapper namespace="org.ff.dao.StudentMapper">
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

在mybatis.xml配置文件中mappers的mapper使用class方式引用或者使用resource方式引用都是一样的，只要StudentMapper.java 中的方法名和 StudentMapper.xml 中sql语句的id对应上就行。

执行sql语句的时候有2种方式：

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

mapper.xml中 namespace需要和对应的接口名一致，resultType在mybatis配置文件中如果配置了别名，就可以只写类名就行，没有配置别名就使用完全限定名称

![image-20210314164516378](C:\Users\jhon\AppData\Roaming\Typora\typora-user-images\image-20210314164516378.png)