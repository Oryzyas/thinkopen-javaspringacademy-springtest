package it.thinkopen.academy.spring.service;

import it.thinkopen.academy.spring.entity.ToDo;

import java.util.List;

public interface ToDoService {
    void create(ToDo todo);
    void update(ToDo todo);
    void delete(ToDo todo);
    ToDo get(long id);
    List<ToDo> getAll(Boolean expired, Boolean done);
}
