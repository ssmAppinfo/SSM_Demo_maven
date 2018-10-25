package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@Controller
public class UserController {
	@Resource
	private UserService service;
	@Resource
	private RoleService roleService;

	@RequestMapping(value = "/login.html")
	public String login() {
		return "login";
	}

	/**
	 * 处理用户登录的方法
	 * */
	@RequestMapping("/dologin.html")
	public String doLogin(@RequestParam("userCode") String userCode,
			@RequestParam("userPassword") String userPassword,
			HttpSession session, HttpServletRequest request) {
		User user = service.login(userCode, userPassword);
		if (user != null) {// 说明用户登录成功
			session.setAttribute(Constants.USER_SESSION, user);
			return "redirect:/user/main.html";// 将请求转发到/user/main.html
			// 相当于response.sendRedirect("frame.jsp");
			// 如果不加redirect: 相当于转发
			// request.getRequestDispatcher("frame.jsp").forward(request,response);
		} else {
			request.setAttribute("error", "用户名或密码不正确！");
			return "login";// 返回的是逻辑视图名
		}
	}

	@RequestMapping("main.html")
	public String main(HttpSession session) {
		if (session.getAttribute(Constants.USER_SESSION) == null) {
			// 说明用户没有进行登录。直接访问首页
			return "redirect:/user/login.html";
		}
		return "frame";
	}

	@RequestMapping("logout.html")
	public String logout(HttpSession session) {
		// 说明用户已经登录，并且session中有用户的信息
		if ((session.getAttribute(Constants.USER_SESSION)) != null) {
			session.removeAttribute(Constants.USER_SESSION);
		}
		return "login";
	}

	/**
	 * 测试登录异常
	 * */
	@RequestMapping(value = "exlogin.html", method = RequestMethod.GET)
	public String exLogin(@RequestParam("userCode") String userCode,
			@RequestParam("userPassword") String userPassword) {
		User user = service.login(userCode, userPassword);
		if (user == null) {
			// 说明没有此用户
			throw new RuntimeException("发生测试异常！");
		}
		return "redirect:/user/main.html";
	}

	/**
	 * 使用局部异常的方式处理
	 * */
	/*
	 * @ExceptionHandler(value = RuntimeException.class) public String
	 * handlerException(RuntimeException e, HttpServletRequest request) {
	 * request.setAttribute("e", e); return "testerror"; }
	 */

	/**
	 * 查询用户列表信息
	 * */
	@RequestMapping("userlist.html")
	public String userList(
			@RequestParam(value = "queryname", required = false) String queryUserName,
			@RequestParam(value = "queryUserRole", required = false) String queryUserRole,
			@RequestParam(value = "pageIndex", required = false) String pageIndex,
			HttpServletRequest request, Model model) {
		// 查询用户列表
		int queryUserRole_ = 0;
		List<User> userList = null;
		// 设置页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		int currentPageNo = 1;
		System.out.println("queryUserName servlet--------" + queryUserName);
		System.out.println("queryUserRole servlet--------" + queryUserRole);
		System.out.println("query pageIndex--------- > " + pageIndex);
		if (queryUserName == null) {
			queryUserName = "";
		}
		if (queryUserRole != null && !queryUserRole.equals("")) {
			queryUserRole_ = Integer.parseInt(queryUserRole);
		}

		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				// response.sendRedirect("error.jsp");
				return "redirect:/user/error.html";
			}
		}
		// 总数量（表）
		int totalCount = service.getUserCount(queryUserName, queryUserRole_);
		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);

		int totalPageCount = pages.getTotalPageCount();

		// 控制首页和尾页
		if (currentPageNo < 1) {
			currentPageNo = 1;
		} else if (currentPageNo > totalPageCount) {
			currentPageNo = totalPageCount;
		}

		userList = service.getUserList(queryUserName, queryUserRole_,
				currentPageNo, pageSize);
		request.setAttribute("userList", userList);
		List<Role> roleList = null;

		roleList = roleService.getRoleList();
		model.addAttribute("roleList", roleList);
		model.addAttribute("queryUserName", queryUserName);
		model.addAttribute("queryUserRole", queryUserRole);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		// request.getRequestDispatcher("userlist.jsp").forward(request,
		// response);
		return "userlist";
	}

	/**
	 * 用户添加界面的展示
	 * */
	@RequestMapping("useradd.html")
	public String userAdd(@ModelAttribute("user") User user) {
		return "useradd";
	}

	/**
	 * 用户添加的方法
	 * */
	@RequestMapping("useraddsave.html")
	public String useraddsave(User user, HttpSession session,
			HttpServletRequest request,
			@RequestParam("attachs") MultipartFile[] attachs) {
		String idPicPath = null;
		String errorInfo = null;
		String workPicPath = null;
		boolean flag = true;// 标识是否进行保存到数据库中
		String path = request.getSession().getServletContext()
				.getRealPath("statics" + File.separator + "uploadfiles");
		// 判断文件是否为空
		for (int i = 0; i < attachs.length; i++) {
			MultipartFile attach = attachs[i];
			if (!attach.isEmpty()) {
				if (i == 0) {
					errorInfo = "uploadFileError";
				} else if (i == 1) {
					errorInfo = "uploadWpError";
				}
				String oldFileName = attach.getOriginalFilename();// 原文件名
				String suffix = FilenameUtils.getExtension(oldFileName);// 原文件的后缀名
				int fileSize = 500000;
				if (attach.getSize() > fileSize) {
					request.setAttribute(errorInfo, "* 上传大小不得超过500KB");
					flag = false;
				} else if (suffix.equalsIgnoreCase("jpg")
						|| suffix.equalsIgnoreCase("jpeg")
						|| suffix.equalsIgnoreCase("png")
						|| suffix.equalsIgnoreCase("pneg")) {
					String fileName = System.currentTimeMillis()
							+ RandomUtils.nextInt(1000000,0) + "_Personal.jpg";
					File targetFile = new File(path, fileName);
					if (!targetFile.exists()) {
						targetFile.mkdirs();
					}
					try {
						// 把MultipartFile中文件流的数据输出至目标文件中
						attach.transferTo(targetFile);
					} catch (Exception e) {
						e.printStackTrace();
						request.setAttribute("uploadFileError", "* 上传文件失败");
						flag = false;
					}
					if (i == 0) {
						idPicPath = path + File.separator + fileName;
					} else if (i == 1) {
						workPicPath = path + File.separator + fileName;
					}
				} else {
					request.setAttribute(errorInfo, "* 上传图片格式不正确");
					flag = false;
				}
			}
		}
		if (flag) {
			user.setCreatedBy(((User) session
					.getAttribute(Constants.USER_SESSION)).getId());
			user.setCreationDate(new Date());
			user.setIdPicPath(idPicPath);
			user.setWorkPicPath(workPicPath);
			if (service.add(user)) {
				return "redirect:/user/userlist.html";
			}
		}

		return "useradd";
	}

	/**
	 * 测试Spring的表单标签进行用户界面的展示
	 */

	@RequestMapping(value = "add.html", method = RequestMethod.GET)
	public String add(@ModelAttribute("user") User user) {
		return "useradd2";
	}

	/**
	 * 当点击提交按钮时，同样将请求发送至add.html 如何确定是提交还是界面展示？ method = RequestMethod.GET:
	 * 表示界面的展示 method = RequestMethod.POST : 表示数据的提交
	 * */
	@RequestMapping(value = "add.html", method = RequestMethod.POST)
	public String addsave(@Valid User user, BindingResult result,
			HttpSession session) {
		if (result.hasErrors()) {
			// 表示验证没有通过，出现了错误信息
			return "useradd2";
		}
		user.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION))
				.getId());
		user.setCreationDate(new Date());
		if (service.add(user)) {
			// 如果添加成功，将界面跳转到userlist.html
			return "redirect:/user/userlist.html";
		}
		return "useradd";
	}

	/**
	 * 跳转到修改界面 : id 目的是： 根据id 来获取指定id的对象
	 * */
	@RequestMapping(value = "modify.html", method = RequestMethod.GET)
	public String add(@RequestParam String uid, Model model) {
		User user = service.getUserById(uid);
		model.addAttribute(user);
		return "usermodify";
	}

	@RequestMapping(value = "modifysave.html")
	public String modifysave(User user, HttpSession session) {
		user.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION))
				.getId());
		user.setModifyDate(new Date());
		if (service.modify(user)) {
			return "redirect:/user/userlist.html";
		}
		return "usermodify";
	}

	/**
	 * 使用REST风格进行数据的展现
	 * */
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable String id, Model model) {
		User user = service.getUserById(id);
		model.addAttribute(user);
		return "userview";
	}

	/**
	 * 判断用户编码同名的验证
	 * */
	@ResponseBody
	@RequestMapping(value = "ucexist.html")
	public String userCodeExist(@RequestParam("userCode") String userCode) {

		Map<String, String> resultMap = new HashMap<String, String>();

		String jsonStr = null;
		if (com.mysql.jdbc.StringUtils.isNullOrEmpty(userCode)) {
			resultMap.put("userCode", "exist");
		} else {
			User user = service.selectUserCodeExist(userCode);
			if (user != null) {
				// 说明数据库中存在该用户
				resultMap.put("userCode", "exist");
			} else {
				resultMap.put("userCode", "noexist");
			}
		}
		jsonStr = JSONArray.toJSONString(resultMap);
		System.out.println(jsonStr);

		return jsonStr;

	}

	/**
	 * 使用异步的方式查看用户的信息
	 * */
	@ResponseBody
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	// public String viewByA(@RequestParam String id, Model model) {
	// String jsonStr = "";
	// if (id == null || "".equals(id)) {
	// jsonStr = "nodata";
	// } else {
	// try {
	// User user = service.getUserById(id);
	// jsonStr = JSON.toJSONString(user);
	// } catch (Exception e) {
	// e.printStackTrace();
	// jsonStr = "failed";
	// }
	// }
	// return jsonStr;
	// }
	public Object viewByA(@RequestParam String id, Model model) {
		User user = service.getUserById(id);
		return user;
	}

	/**
	 * 使用异步删除用户信息
	 * */
	@ResponseBody
	@RequestMapping("deluser.json")
	public Object deluser(@RequestParam String id){
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(com.mysql.jdbc.StringUtils.isNullOrEmpty(id)){
			resultMap.put("delResult", "notexist");
		}else{
			if(service.deleteUserById(Integer.parseInt(id)))
				resultMap.put("delResult", "true");
			else
				resultMap.put("delResult", "false");
		}
		return JSONArray.toJSONString(resultMap);
	}
}
