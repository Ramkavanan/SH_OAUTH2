package com.sh.auth.persistence.dao;

import java.util.List;

import com.sh.auth.persistence.model.RBACPermission;

public interface RBACPermissionDao extends AuthGenericDao<RBACPermission, Long> {

	List<RBACPermission> getByResourceId(String resourceId) throws Exception;
	
	RBACPermission saveRBAC(RBACPermission permission) throws Exception;
	
	RBACPermission updateRABC(RBACPermission permission) throws Exception;
	
	boolean delete(Long rbacId) throws Exception;
	
}
