package com.sh.auth.persistence.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sh.auth.persistence.dao.RBACPermissionDao;
import com.sh.auth.persistence.model.RBACPermission;

@Repository("rbacPermissionDao")
@Transactional
public class RBACPermissionDaoImpl extends AuthGenericDaoImpl<RBACPermission, Long> implements RBACPermissionDao {

	@Autowired
    public RBACPermissionDaoImpl(@Qualifier("oauthSessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory, RBACPermission.class);
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RBACPermission> getByResourceId(String resourceId) throws Exception {
		return (List<RBACPermission>) getSession().createCriteria(RBACPermission.class).add(getRBACBYResourceId(resourceId)).list();
	}
	
	private Example getRBACBYResourceId(String resourceId) {
		RBACPermission rbacPermission = new RBACPermission();
		rbacPermission.setResourceId(resourceId);
		return Example.create(rbacPermission);
		
	}

	@Override
	public RBACPermission saveRBAC(RBACPermission permission) throws Exception {
		return save(permission);
	}

	@Override
	public RBACPermission updateRABC(RBACPermission permission) throws Exception {
		return update(permission);
	}

	@Override
	public boolean delete(Long rbacId) throws Exception {
		return delete(rbacId);
	}
}
