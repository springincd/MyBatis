DROP DATABASE IF EXISTS mybatis;
CREATE DATABASE mybatis DEFAULT CHARACTER SET utf8;

USE mybatis;
CREATE TABLE student(
                        id int(11) NOT NULL AUTO_INCREMENT,
                        studentID INT(11) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO student VALUES(null,1,"我没有三颗心脏");
INSERT INTO student VALUES(null,2,"我没有三颗心脏");
INSERT INTO student VALUES(null,3,"我没有三颗心脏");

SELECT * FROM student;
