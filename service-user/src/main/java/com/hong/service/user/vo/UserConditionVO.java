package com.hong.service.user.vo;

import com.hong.service.common.object.BaseConditionVO;
import com.hong.service.user.entity.SysUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/16 16:26
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserConditionVO extends BaseConditionVO {
    private SysUser user;
}
