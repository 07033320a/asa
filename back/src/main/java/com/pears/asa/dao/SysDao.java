package com.pears.asa.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: pears
 * @description: sys
 * @date: 2018-11-20 15:08:45
 */
public interface SysDao {

    /**
     * 查询激活邮箱码列表
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> listSysUserActive(JSONObject jsonObject);
    /**
     * 查询用户列表
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> listPeriod(JSONObject jsonObject);

    /**
     * 设置Period列表
     *
     * @param jsonObject
     * @return
     */
    JSONObject addPeriod(JSONObject jsonObject);

    /**
     * 删除Period
     *
     */
    void deletePeriods();

}
