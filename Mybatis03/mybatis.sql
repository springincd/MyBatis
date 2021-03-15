DROP DATABASE mybatis;
SHOW DATABASES;

-- 创建mybatis数据库 用于复杂查询
-- 创建一对一关系
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

