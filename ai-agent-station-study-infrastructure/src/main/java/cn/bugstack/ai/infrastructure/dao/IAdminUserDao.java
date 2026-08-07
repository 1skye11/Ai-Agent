package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员用户表 DAO
 * @author bugstack虫洞�?
 * @description 管理员用户表数据访问对象
 */
@Mapper
public interface IAdminUserDao {

    /**
     * 插入管理员用�?
     * @param adminUser 管理员用户对�?
     * @return 影响行数
     */
    int insert(AdminUser adminUser);

    /**
     * 根据ID更新管理员用�?
     * @param adminUser 管理员用户对�?
     * @return 影响行数
     */
    int updateById(AdminUser adminUser);

    /**
     * 根据用户ID更新管理员用�?
     * @param adminUser 管理员用户对�?
     * @return 影响行数
     */
    int updateByUserId(AdminUser adminUser);

    /**
     * 根据ID删除管理员用�?
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据用户ID删除管理员用�?
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(String userId);

    /**
     * 根据ID查询管理员用�?
     * @param id 主键ID
     * @return 管理员用户对�?
     */
    AdminUser queryById(Long id);

    /**
     * 根据用户ID查询管理员用�?
     * @param userId 用户ID
     * @return 管理员用户对�?
     */
    AdminUser queryByUserId(String userId);

    /**
     * 根据用户名查询管理员用户
     * @param username 用户�?
     * @return 管理员用户对�?
     */
    AdminUser queryByUsername(String username);

    /**
     * 查询启用状态的管理员用户列�?
     * @return 管理员用户列�?
     */
    List<AdminUser> queryEnabledUsers();

    /**
     * 根据状态查询管理员用户列表
     * @param status 状�?
     * @return 管理员用户列�?
     */
    List<AdminUser> queryByStatus(Integer status);

    /**
     * 查询所有管理员用户
     * @return 管理员用户列�?
     */
    List<AdminUser> queryAll();

    /**
     * 用户登录验证
     * @param username 用户�?
     * @param password 密码
     * @return 管理员用户对�?
     */
    AdminUser queryByUsernameAndPassword(@Param("username") String username,
                                          @Param("password") String password);

}
