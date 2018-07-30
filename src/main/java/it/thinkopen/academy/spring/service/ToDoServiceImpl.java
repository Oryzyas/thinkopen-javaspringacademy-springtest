package it.thinkopen.academy.spring.service;

import it.thinkopen.academy.spring.dao.ToDoDao;
import it.thinkopen.academy.spring.entity.ToDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoDao toDoDao;

    @Transactional(readOnly = false)
    @Override
    public void create(ToDo todo) {
        toDoDao.save(todo);
    }

    @Transactional(readOnly = false)
    @Override
    public void update(ToDo todo) {
        toDoDao.save(todo);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(ToDo todo) {
        toDoDao.delete(todo);
    }

    @Override
    public ToDo get(long id) {
        return toDoDao.get(id);
    }

    @Override
    public List<ToDo> getAll(Boolean expired, Boolean done) {
        return toDoDao.list(expired, done);
    }
}
