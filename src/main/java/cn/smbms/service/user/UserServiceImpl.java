package cn.smbms.service.user;

import cn.smbms.dao.user.UserMapper;
import cn.smbms.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * service层捕获异常，进行事务处理 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * 
 * @author Administrator
 * 
 */
@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserMapper userDao;

	public UserServiceImpl() {

	}

	@Override
	public boolean add(User user) {

		boolean flag = false;
		try {
			int updateRows = userDao.add(user);
			if (updateRows > 0) {
				flag = true;
				System.out.println("add success!");
			} else {
				System.out.println("add failed!");
			}

		} catch (Exception e) {
		}
		return flag;
	}

	@Override
	public User login(String userCode, String userPassword) {
		User user = null;
		try {
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 匹配密码
		if (null != user) {
			if (!user.getUserPassword().equals(userPassword))
				user = null;
		}

		return user;
	}

	@Override
	public List<User> getUserList(String queryUserName, int queryUserRole,
                                  int currentPageNo, int pageSize) {
		List<User> userList = null;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		System.out.println("currentPageNo ---- > " + currentPageNo);
		System.out.println("pageSize ---- > " + pageSize);
		try {
			userList = userDao.getUserList(queryUserName, queryUserRole,
					currentPageNo, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public User selectUserCodeExist(String userCode) {
		User user = null;
		try {
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public boolean deleteUserById(Integer delId) {
		boolean flag = false;
		try {
			if (userDao.deleteUserById(delId) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public User getUserById(String id) {
		User user = null;
		try {
			user = userDao.getUserById(id);
		} catch (Exception e) {
			e.printStackTrace();
			user = null;
		}
		return user;
	}

	@Override
	public boolean modify(User user) {
		boolean flag = false;
		try {
			if (userDao.modify(user) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public boolean updatePwd(int id, String pwd) {
		boolean flag = false;
		try {
			if (userDao.updatePwd(id, pwd) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public int getUserCount(String queryUserName, int queryUserRole) {
		int count = 0;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		try {
			count = userDao.getUserCount(queryUserName, queryUserRole);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

}
