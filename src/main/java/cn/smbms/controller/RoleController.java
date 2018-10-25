package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/role")
@Controller
public class RoleController {
	@Resource
	private RoleService roleService;

	@ResponseBody
	@RequestMapping(value = "rolelist", method = RequestMethod.GET)
	public String roleList() {
		List<Role> roleList = roleService.getRoleList();// 获取所有的角色信息的集合
		return JSONArray.toJSONString(roleList);
	}
}
