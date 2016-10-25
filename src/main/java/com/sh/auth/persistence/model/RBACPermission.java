package com.sh.auth.persistence.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rbacpermission")
public class RBACPermission  extends BaseObject implements Serializable{

private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="url", nullable=false)
	private String url;
	
	@Column(name="resourceId", nullable=false)
	private String resourceId;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = { @JoinColumn(name = "permission_id") },
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
	private Set<Role> roles = new HashSet<Role>();
	
	@Column(name = "scope")
	private String scope;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@Transient
	public List<LabelValue> getRoleList() {
		List<LabelValue> rbacRoles = new ArrayList<LabelValue>();

		if (this.roles != null) {
			for (Role role : roles) {
				// convert the user's roles to LabelValue Objects
				rbacRoles.add(new LabelValue(role.getName(), role.getName()));
			}
		}

		return rbacRoles;
	}
	
	@Transient
	public String getRolesAsString() {
		String rbacRoles = "";
		if (this.roles != null) {
			for (Role role : roles) {
				rbacRoles += "'"+role.getName()+"'"+",";
			}
		}
		if(!rbacRoles.isEmpty() && rbacRoles.substring(rbacRoles.length()-1, rbacRoles.length()).equalsIgnoreCase(",")) {
			rbacRoles = rbacRoles.substring(0, rbacRoles.length()-1);
		}
		return rbacRoles;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("RBAC={")
				.append("resourceId='").append(resourceId).append("'")
				.append(",scope='").append(scope).append("'")
				.append(",url='").append(url).append("'").append("}").toString();
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if (!(o instanceof RBACPermission)) {
            return false;
        }
		return false;
	}

	@Override
	public int hashCode() {
		return (resourceId != null ? resourceId.hashCode() : 0);
	}
	
}
