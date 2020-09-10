package com.hong.service.user.service;


import com.github.pagehelper.PageInfo;
import com.hong.service.common.object.AbstractService;
import com.hong.service.user.entity.SysUser;
import com.hong.service.user.vo.UserConditionVO;
import com.hong.service.user.vo.UserPwd;

import java.util.List;

/**
 * 用户
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/16 16:26
 * @since 1.0
 */
public interface SysUserService extends AbstractService<SysUser, Long> {

    /**
     * 分页查询
     *
     * @param vo
     * @return
     */
    PageInfo<SysUser> findPageBreakByCondition(UserConditionVO vo);

    /**
     * 更新用户最后一次登录的状态信息
     *
     * @param user
     * @return
     */
    SysUser updateUserLastLoginInfo(SysUser user);

    /**
     * 根据用户名查找
     *
     * @param userName
     * @return
     */
    SysUser getByUserName(String userName);

    /**
     * 通过角色Id获取用户列表
     *
     * @param roleId
     * @return
     */
    List<SysUser> listByRoleId(Long roleId);

    /**
     * 修改密码
     *
     * @param userPwd
     * @return
     */
    boolean updatePassword(UserPwd userPwd) throws Exception;


    /**
     * 通过用户的uuid和source查询用户是否存在
     *
     * @param uuid
     * @param source
     * @return
     */
    SysUser getByUUIDAndSource(String uuid, String source);
}
