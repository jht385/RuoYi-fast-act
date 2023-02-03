package com.ruoyi.project.system.user.mapper;

import java.util.List;

import com.ruoyi.project.system.user.domain.User;

public interface UserExMapper {

	public List<User> listUser4Select(User user);
	
	public List<User> listUserByUserIds(Long[] userIds);

	public List<User> listUserByRoleIds(String roleIds);

}
