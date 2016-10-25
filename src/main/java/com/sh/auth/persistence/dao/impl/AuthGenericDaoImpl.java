package com.sh.auth.persistence.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.sh.auth.persistence.dao.AuthGenericDao;

/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic
 * 
 * @author RamaKavanan
 * @param <T>  a type variable
 * @param <PK> the primary key for that type
 */

@Transactional
public abstract class AuthGenericDaoImpl<T, PK extends Serializable> implements AuthGenericDao<T, PK> {

	private Class<T> persistentClass;
	
    private SessionFactory sessionFactory;
    
	/**
     * Constructor that takes in a class and sessionFactory for easy creation of DAO.
     *
     * @param persistentClass the class type you'd like to persist
     * @param sessionFactory  the pre-configured Hibernate SessionFactory
     */
    public AuthGenericDaoImpl( SessionFactory sessionFactory, Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Constructor that takes in a class to see which type of entity to persist.
     * Use this constructor when subclassing.
     *
     * @param persistentClass the class type you'd like to persist
     */
    public AuthGenericDaoImpl(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }
    
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    
    public Session getSession() throws HibernateException {
        Session sess = getSessionFactory().getCurrentSession();
        if (sess == null) {
            sess = getSessionFactory().openSession();
        }
        return this.sessionFactory.getCurrentSession();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Session sess = getSession();
        return sess.createCriteria(persistentClass).list();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        T entity = (T) byId.load(id);

        if (entity == null) {
            throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }

        return entity;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T save(T object) {
        Session sess = getSession();
        return (T) sess.save(object);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(T object) {
        Session sess = getSession();
        return (T) sess.merge(object);
    }
    
    /**
     * {@inheritDoc}
     */
    public void remove(T object) {
        Session sess = getSession();
        sess.delete(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        T entity = (T) byId.load(id);
        sess.delete(entity);
    }

}
