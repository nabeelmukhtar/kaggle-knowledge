package com.github.chaparqanatoos.kaggle.knowledge.amazon;

import java.io.Serializable;

public class AmazonEacRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7652126961955548367L;
	
	private int id;
	private int resource;
	private int managerId;
	private int roleRollup1;
	private int roleRollup2;
	private int roleDepartmentName;
	private int roleTitle;
	private int roleFamilyDescription;
	private int roleFamily;
	private int roleCode;
	private boolean action;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getResource() {
		return resource;
	}
	public void setResource(int resource) {
		this.resource = resource;
	}
	public int getManagerId() {
		return managerId;
	}
	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	public int getRoleRollup1() {
		return roleRollup1;
	}
	public void setRoleRollup1(int roleRollup1) {
		this.roleRollup1 = roleRollup1;
	}
	public int getRoleRollup2() {
		return roleRollup2;
	}
	public void setRoleRollup2(int roleRollup2) {
		this.roleRollup2 = roleRollup2;
	}
	public int getRoleDepartmentName() {
		return roleDepartmentName;
	}
	public void setRoleDepartmentName(int roleDepartmentName) {
		this.roleDepartmentName = roleDepartmentName;
	}
	public int getRoleTitle() {
		return roleTitle;
	}
	public void setRoleTitle(int roleTitle) {
		this.roleTitle = roleTitle;
	}
	public int getRoleFamilyDescription() {
		return roleFamilyDescription;
	}
	public void setRoleFamilyDescription(int roleFamilyDescription) {
		this.roleFamilyDescription = roleFamilyDescription;
	}
	public int getRoleFamily() {
		return roleFamily;
	}
	public void setRoleFamily(int roleFamily) {
		this.roleFamily = roleFamily;
	}
	public int getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(int roleCode) {
		this.roleCode = roleCode;
	}
	public boolean isAction() {
		return action;
	}
	public void setAction(boolean action) {
		this.action = action;
	}
}
