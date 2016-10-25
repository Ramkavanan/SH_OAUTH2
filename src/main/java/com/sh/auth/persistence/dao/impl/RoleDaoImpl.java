package com.sh.auth.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sh.auth.persistence.dao.RoleDao;
import com.sh.auth.persistence.model.Role;

@Repository("roleDao")
public class RoleDaoImpl extends AuthGenericDaoImpl<Role, Long> implements RoleDao {

	@Autowired
	public RoleDaoImpl(@Qualifier("oauthSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory, Role.class);
	}

	@Override
	public Role insert(Role role) throws Exception {
		role.setCreatedDate(new Date());
		role.setLastModifiedDate(new Date());
		return save(role);
	}

	@Override
	public Role updateRole(Role role) throws Exception {
		role.setLastModifiedDate(new Date());
		return role;
	}

	@Override
	public boolean delete(Long roleId) throws Exception {
		Role role = get(roleId);
		if(role == null)
			return false;
		role.setIsDeleted(true);
		role.setLastModifiedDate(new Date());
		update(role);
		return true;
	}

	@Override
	public Role getRole(Long roleId) throws Exception {
		return get(roleId);
	}

	@Override
	public List<Role> getRoles() throws Exception {
		return getAll();
	}

}
