package it.thinkopen.academy.spring.dao;

import it.thinkopen.academy.spring.entity.ToDo;

import java.util.List;

public interface ToDoDao {
    void save(ToDo toDo);
    void delete(ToDo toDo);
    ToDo get(long id);
    List<ToDo> list(Boolean expired, Boolean done);
}
