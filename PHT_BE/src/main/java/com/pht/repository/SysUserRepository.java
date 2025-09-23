package com.pht.repository;

import org.springframework.stereotype.Repository;

import com.pht.entity.SysUser;

@Repository
public interface SysUserRepository extends BaseRepository<SysUser, Long> {

    boolean existsByUsername(String username);
    
    SysUser findByUsername(String username);
}









