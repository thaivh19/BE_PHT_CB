package com.pht.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SysGroupFunc;

@Repository
public interface SysGroupFuncRepository extends BaseRepository<SysGroupFunc, Long> {

    @Query("SELECT gf FROM SysGroupFunc gf WHERE gf.groupId = :groupId")
    List<SysGroupFunc> findByGroupId(@Param("groupId") Long groupId);
}









