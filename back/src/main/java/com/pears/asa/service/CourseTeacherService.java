package com.pears.asa.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: pears
 * @date: 2017/10/24 16:06
 */
public interface CourseTeacherService {
    /**
     * 新增文章
     *
     * @param jsonObject
     * @return
     */
    JSONObject addCourse(JSONObject jsonObject);

    /**
     * 文章列表
     *
     * @param jsonObject
     * @return
     */
    JSONObject listCourse(JSONObject jsonObject);
    /**
     * 文章列表
     *
     * @param jsonObject
     * @return
     */
    JSONObject listMyCourse(JSONObject jsonObject);

    /**
     * 更新文章
     *
     * @param jsonObject
     * @return
     */
    JSONObject updateCourse(JSONObject jsonObject);
    /**
     * 更新文章
     *
     * @param jsonObject
     * @return
     */
    JSONObject deleteCourse(JSONObject jsonObject);
}