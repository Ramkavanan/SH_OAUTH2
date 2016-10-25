package com.sh.auth.persistence.dao;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sh.auth.persistence.model.User;

public interface UserDao extends AuthGenericDao<User, Long>{
	
	/**
     * Gets users information based on login name.
     * 
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException thrown when user not
     * found in database
     */
/*    @Transactional
*/    User loadUserByUsername(String username) throws UsernameNotFoundException;
    
    /**
     * Gets a list of users ordered by the uppercase version of their username.
     *
     * @return List populated list of users
     */
    List<User> getUsers();

    /**
     * Saves a user's information.
     * 
     * @param user the object to be saved
     * @return the persisted User object
     */
    User saveUser(User user);

    
	/**
     * Retrieves the password in DB for a user
     * 
     * @param userId the user's id
     * @return the password in DB, if the user is already persisted
	 * @throws Exception 
     */
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getUserPassword(Long userId) throws Exception;
    
    boolean inActiveUser(Long userId) throws Exception;
    
    boolean delete(Long userId) throws Exception;
}
