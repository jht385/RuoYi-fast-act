package com.ruoyi.project.system.role.mapper;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.project.system.role.domain.Role;

public interface RoleExMapper {
	List<Role> listByRoleKeys(List<String> roleKeys);

	default String getRoleIdsByRoleKeys(String... roleKeys) {
		List<String> keyList = Arrays.asList(roleKeys);
		List<Role> roles = listByRoleKeys(keyList);
		StringBuilder sb = new StringBuilder();
		for (Role role : roles) {
			sb.append(role.getRoleId() + ",");
		}
		String temp = sb.toString();
		return temp.substring(0, temp.length() - 1);
	}
}
