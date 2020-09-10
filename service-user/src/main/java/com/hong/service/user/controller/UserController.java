package com.hong.service.user.controller;

import com.hong.service.user.entity.SysUser;
import com.hong.service.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    @Autowired
    public SysUserService sysUserService;

    @GetMapping("{id}")
    public SysUser listByRoleId(@PathVariable Long id) {
        return sysUserService.getByPrimaryKey(id);
    }

    @GetMapping
    public List<SysUser> listAll() {
        return sysUserService.listAll();
    }
}
