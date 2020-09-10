package com.hong.service.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hong.service.common.exception.ZhydCommentException;
import com.hong.service.common.exception.ZhydException;
import com.hong.service.common.holder.RequestHolder;
import com.hong.service.common.util.IpUtil;
import com.hong.service.user.entity.SysUser;
import com.hong.service.user.enums.UserNotificationEnum;
import com.hong.service.user.enums.UserPrivacyEnum;
import com.hong.service.user.enums.UserStatusEnum;
import com.hong.service.user.mapper.SysUserMapper;
import com.hong.service.user.service.SysUserService;
import com.hong.service.user.util.PasswordUtil;
import com.hong.service.user.vo.UserConditionVO;
import com.hong.service.user.vo.UserPwd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser insert(SysUser user) {
        Assert.notNull(user, "User不可为空！");
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setRegIp(IpUtil.getRealIp(RequestHolder.getRequest()));
        user.setPrivacy(UserPrivacyEnum.PUBLIC.getCode());
        user.setNotification(UserNotificationEnum.DETAIL.getCode());
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        sysUserMapper.insertSelective(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertList(List<SysUser> users) {
        Assert.notNull(users, "Users不可为空！");
        List<SysUser> sysUsers = new ArrayList<>();
        String regIp = IpUtil.getRealIp(RequestHolder.getRequest());
        for (SysUser user : users) {
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            user.setRegIp(regIp);
            user.setPrivacy(UserPrivacyEnum.PUBLIC.getCode());
            user.setNotification(UserNotificationEnum.DETAIL.getCode());
        }
        sysUserMapper.insertList(users);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByPrimaryKey(Long primaryKey) {
        return sysUserMapper.deleteByPrimaryKey(primaryKey) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSelective(SysUser user) {
        Assert.notNull(user, "User不可为空！");
        user.setUpdateTime(new Date());
        if (!StringUtils.isEmpty(user.getPassword())) {
            try {
                user.setPassword(PasswordUtil.encrypt(user.getPassword(), user.getUsername()));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ZhydCommentException("密码加密失败");
            }
        } else {
            user.setPassword(null);
        }
        return sysUserMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Override
    public SysUser getByPrimaryKey(Long primaryKey) {
        Assert.notNull(primaryKey, "PrimaryKey不可为空！");
        SysUser sysUser = sysUserMapper.selectByPrimaryKey(primaryKey);
        return sysUser;
    }

    @Override
    public List<SysUser> listAll() {
        List<SysUser> sysUsers = sysUserMapper.selectAll();

        if (CollectionUtils.isEmpty(sysUsers)) {
            return null;
        }

        return sysUsers;
    }

    @Override
    public PageInfo<SysUser> findPageBreakByCondition(UserConditionVO vo) {
        PageHelper.startPage(vo.getPageNumber(), vo.getPageSize());
        List<SysUser> sysUsers = sysUserMapper.findPageBreakByCondition(vo);
        if (CollectionUtils.isEmpty(sysUsers)) {
            return null;
        }
        PageInfo<SysUser> bean = new PageInfo<>(sysUsers);
        bean.setList(sysUsers);
        return bean;
    }

    /**
     * 更新用户最后一次登录的状态信息
     *
     * @param user
     * @return
     */
    @Override
    public SysUser updateUserLastLoginInfo(SysUser user) {
        if (user != null) {
            user.setLoginCount(user.getLoginCount() + 1);
            user.setLastLoginTime(new Date());
            user.setLastLoginIp(IpUtil.getRealIp(RequestHolder.getRequest()));
            user.setPassword(null);
            this.updateSelective(user);
        }
        return user;
    }

    /**
     * 根据用户名查找
     *
     * @param userName
     * @return
     */
    @Override
    public SysUser getByUserName(String userName) {
        SysUser user = new SysUser();
        user.setUsername(userName);
        SysUser sysUser = this.sysUserMapper.selectOne(user);
        return sysUser;
    }

    /**
     * 通过角色Id获取用户列表
     *
     * @param roleId
     * @return
     */
    @Override
    public List<SysUser> listByRoleId(Long roleId) {
        List<SysUser> sysUsers = sysUserMapper.listByRoleId(roleId);

        return sysUsers;
    }

    /**
     * 修改密码
     *
     * @param userPwd
     * @return
     */
    @Override
    public boolean updatePassword(UserPwd userPwd) throws Exception {
        if (!userPwd.getNewPassword().equals(userPwd.getNewPasswordRepeat())) {
            throw new ZhydException("新密码不一致！");
        }
        SysUser user = this.getByPrimaryKey(userPwd.getId());
        if (null == user) {
            throw new ZhydException("用户编号错误！请不要手动操作用户ID！");
        }

        if (!user.getPassword().equals(PasswordUtil.encrypt(userPwd.getPassword(), user.getUsername()))) {
            throw new ZhydException("原密码不正确！");
        }
        user.setPassword(userPwd.getNewPassword());

        return this.updateSelective(user);
    }

    @Override
    public SysUser getByUUIDAndSource(String uuid, String source) {
        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(source)) {
            return null;
        }
        SysUser user = new SysUser();
        user.setUuid(uuid);
        user.setSource(source);
        SysUser sysUser = sysUserMapper.selectOne(user);
        return sysUser;
    }
}
