package it.thinkopen.academy.spring.dao;

import it.thinkopen.academy.spring.Utils;
import it.thinkopen.academy.spring.entity.ToDo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class ToDoDaoImpl implements ToDoDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(final ToDo toDo) {
        if(toDo.getId() != null)
            em.merge(toDo);
        else
            em.persist(toDo);
    }

    @Override
    public void delete(ToDo toDo) {
        final ToDo fToDo = em.find(ToDo.class, toDo.getId());

        if(fToDo != null)
            em.remove(fToDo);
    }

    @Override
    public ToDo get(long id) {
        return em.find(ToDo.class, id);
    }

    @Override
    public List<ToDo> list(Boolean expired, Boolean done) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ToDo> criteriaQuery = cb.createQuery(ToDo.class);
        Root<ToDo> root = criteriaQuery.from(ToDo.class);
        Metamodel m = em.getMetamodel();
        EntityType<ToDo> e = m.entity(ToDo.class);
        List<Predicate> predicates = new ArrayList<>(2);

        if(expired != null) {
            final Predicate expPred = cb.lessThan(root.get(e.getAttribute("expiration").getName()), Utils.toSeconds(LocalDateTime.now()));
            final Predicate notExpPred = cb.ge(root.get(e.getAttribute("expiration").getName()), Utils.toSeconds(LocalDateTime.now()));

            predicates.add(expired ? expPred : notExpPred);
        }

        if(done != null) {
            final Predicate donePred = cb.equal(root.get(e.getAttribute("done").getName()), done);

            predicates.add(donePred);
        }

        criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        return em.createQuery(criteriaQuery).getResultList();
    }
}
