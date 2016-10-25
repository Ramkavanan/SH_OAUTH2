package com.sh.auth.persistence.dao;

import java.util.List;

import com.sh.auth.persistence.model.Role;

public interface RoleDao extends AuthGenericDao<Role, Long> {

	Role insert(Role role) throws Exception;
	
	Role updateRole(Role role) throws Exception;
	
	boolean delete(Long roleId) throws Exception;
	
	Role getRole(Long roleId) throws Exception;
	
	List<Role> getRoles() throws Exception;
	
}
