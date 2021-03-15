package org.ff.dao;

import org.ff.domain.StudentAndCard;

public interface StudentMapper {
    StudentAndCard findStudentByCard();
}
