package com.pears.asa.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pears.asa.config.properties.EmailConfig;
import com.pears.asa.dao.CourseStudentDao;
import com.pears.asa.dao.CourseTeacherDao;
import com.pears.asa.dao.SysDao;
import com.pears.asa.service.CourseTeacherService;
import com.pears.asa.util.CommonUtil;
import com.pears.asa.util.EmailUtil;
import com.pears.asa.util.constants.Constants;
import com.pears.asa.util.constants.ErrorEnum;
import com.pears.asa.util.model.MailVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author: pears
 * @description:
 * @date: 2017/10/24 16:07
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private static Logger logger = LoggerFactory.getLogger(CourseTeacherServiceImpl.class);
    @Autowired
    EmailConfig emailConfig;
    @Autowired
    private CourseTeacherDao courseTeacherDao;
    @Autowired
    private SysDao sysDao;
    @Autowired
    private CourseStudentDao courseStudentDao;


    /**
     * 新增课程
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject addCourse(JSONObject jsonObject) {
        Session session = SecurityUtils.getSubject().getSession();
        JSONObject userInfo = (JSONObject) session.getAttribute(Constants.SESSION_USER_INFO);
        if (checkCourseDate(jsonObject, jsonObject.getInteger("teacherName"))) return CommonUtil.errorJson(ErrorEnum.E_10004);
        jsonObject.put("updateUser",userInfo.getInteger("userId"));
        jsonObject.put("createUser",userInfo.getInteger("userId"));
        StringToJsonArrayObj(jsonObject, "grade");
        List<JSONObject> listPeriod = sysDao.listPeriod(new JSONObject());
        if(listPeriod.size()>0){
            jsonObject.put("startDate",listPeriod.get(0).getString("startDate"));
            jsonObject.put("endDate",listPeriod.get(0).getString("endDate"));
        }
        courseTeacherDao.addCourse(jsonObject);
        return CommonUtil.successJson(jsonObject);
    }

    private boolean checkCourseDate(JSONObject jsonObject, Integer teacherName) {
        List findInSetParams = (List<String>) jsonObject.get("courseDateArr");
        if(findInSetParams.size()>0){
            JSONObject jo = new JSONObject();
            jo.put("courseDateArr", findInSetParams);
            jo.put("author", teacherName);
            jo.put("idNotEqual", jsonObject.getInteger("idNotEqual"));
            int count = courseTeacherDao.countCourse(jo);
            if(count>0){
                return true;
            }
        }
        return false;
    }

    /**
     * 前端arr类型转java对象
     * @param key
     * @return
     */
    private void StringToJsonArrayObj(JSONObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            String jsonString = "";
            if(jsonObject.get(key) instanceof LinkedHashMap ){
                jsonString = JSON.toJSONString((LinkedHashMap<String, String>) jsonObject.get(key));
            }
            if(jsonObject.get(key) instanceof List ){
                ArrayList<Integer> list = (ArrayList) jsonObject.get(key);
                List<Integer> resultList = new ArrayList<Integer>();
                for(int i = list.get(0); i < (list.get(1)+1); i++){
                    resultList.add(i);
                }
                jsonString = JSON.toJSONString(resultList);
            }
            jsonObject.put(key, jsonString);
        }
    }

    /**
     * 课程列表
     *
     * @param jsonObject
     * @return
     */
    @Override
    public JSONObject listCourse(JSONObject jsonObject) {
        Session session = SecurityUtils.getSubject().getSession();
        if(jsonObject.containsKey("studentCanPick") && jsonObject.getBoolean("studentCanPick")){
            JSONObject userInfo = (JSONObject) session.getAttribute(Constants.SESSION_USER_INFO);
            jsonObject.put("studentCanPick",userInfo.getInteger("userId"));
        }
        JSONObject result = getList(jsonObject,session);
        List<JSONObject>  list = (List<JSONObject> )result.getJSONObject("returnData").get("list");
        list.stream().forEach(i->{
            JSONObject j = new JSONObject();
            j.put("courseId",i.get("id"));
            int num = courseStudentDao.countStudentDetail4Teacher(j);
            i.put("pickStudentNum",num);
        });
        return result;
    }

    /**
     * 我的课程列表
     *
     * @param jsonObject
     * @return
     */
    @Override
    public JSONObject listMyCourse(JSONObject jsonObject) {
        Session session = SecurityUtils.getSubject().getSession();
        JSONObject userInfo = (JSONObject) session.getAttribute(Constants.SESSION_USER_INFO);
        jsonObject.put("author",userInfo.getInteger("userId"));
        JSONObject result = getList(jsonObject,session);
        List<JSONObject>  list = (List<JSONObject> )result.getJSONObject("returnData").get("list");
        list.stream().forEach(i->{
            JSONObject j = new JSONObject();
            j.put("courseId",i.get("id"));
            List<JSONObject> l = courseStudentDao.listStudentDetail4Teacher(j);
            i.put("students",l);
            int num = courseStudentDao.countStudentDetail4Teacher(j);
            i.put("pickStudentNum",num);
        });
        return result;
    }

    private JSONObject getList(JSONObject jsonObject,Session session) {
        CommonUtil.fillPageParam(jsonObject);
        JSONObject userPermission = (JSONObject) session.getAttribute("userPermission");
        //学生查自己能看到的课程
        if(userPermission.getString("groupTag")!=null
                && userPermission.getString("groupTag").equalsIgnoreCase("1")
                && userPermission.getString("grade")!=null){
            //jsonObject
            jsonObject.put("studentGradeFilter",userPermission.getString("grade"));

        }
        int count = courseTeacherDao.countCourse(jsonObject);
        List<JSONObject> list = courseTeacherDao.listCourse(jsonObject);
        list.stream().forEach(p->{
            JSONArray gradeObj = p.getJSONArray("grade");
            if(null!=gradeObj){
                JSONArray gr = new JSONArray();
                gr.add(gradeObj.get(0));
                gr.add(gradeObj.get(gradeObj.size()-1));
                p.put("grade",gr);
            }
        });
        return CommonUtil.successPage(jsonObject, list, count);
    }

    /**
     * 更新课程
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updateCourse(JSONObject jsonObject) {
        Session session = SecurityUtils.getSubject().getSession();
        JSONObject userInfo = (JSONObject) session.getAttribute(Constants.SESSION_USER_INFO);
        jsonObject.put("idNotEqual",jsonObject.getInteger("id"));
        if (checkCourseDate(jsonObject, jsonObject.getInteger("teacherName"))) return CommonUtil.errorJson(ErrorEnum.E_10004);
        exeUpdateCourse(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 更新课程4 system
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updateCourse4Sys(JSONObject jsonObject) {
        courseTeacherDao.updateCourse(jsonObject);
        return CommonUtil.successJson();
    }

    @Override
    public JSONObject updateFinalTuition(JSONObject jsonObject) {
        courseTeacherDao.updateFinalTuition(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 更新课程
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject deleteCourse(JSONObject jsonObject) {

        JSONObject jo = new JSONObject();
        jo.put("courseId",jsonObject.getInteger("id"));
        int canDelete = courseStudentDao.countCourse(jo);
        if(canDelete>0){
            return CommonUtil.errorJson(ErrorEnum.E_10005);
        }
        exeUpdateCourse(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 更新课程
     *
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject disabledCourse(JSONObject jsonObject) {

        JSONObject jo = new JSONObject();
        jo.put("courseId",jsonObject.getInteger("id"));
        List<JSONObject> sunList = courseStudentDao.listCourse(jo);
        if(sunList.size()>0){
            sunList.stream().forEach(i->{
                JSONObject jov = new JSONObject();
                jov.put("id",i.get("id"));
                jov.put("deleteStatus","2");
                jov.put("adminDisabled","1");
                courseStudentDao.updateCourse(jov);
            });
        }
        exeUpdateCourse(jsonObject);

        if(StringUtils.hasText(jsonObject.getString("email"))){
            //send email
            MailVO vo = new MailVO();
            vo.setSubject(CommonUtil.getI18NMessage("email.disabledTitle",null));
            vo.setFromUser(Constants.EMAIL_SENDER);
            vo.setToUser(jsonObject.getString("email"));

            String url = CommonUtil.getI18NMessage("email.disabledContent",new String[]{jsonObject.getString("content")});

            StringBuffer sb = new StringBuffer();
            sb.append("<html><head></head><body><h1>"+CommonUtil.getI18NMessage("email.part1",null)+"</h1>")
                    .append("<p style=''>"+url+"</p><p>"+CommonUtil.getI18NMessage("email.part3",null)+"</p>");
            vo.setContent(sb.toString());
            EmailUtil e = new EmailUtil();
            if(e.sendMailHtml(vo,emailConfig)){
                return CommonUtil.successJson();
            }else{
                return CommonUtil.errorJson(ErrorEnum.E_00003);
            }
        }else{
            logger.info("无法找到接受者email！！！！！！！！！！");
            return CommonUtil.successJson();
        }


    }

    @Override
    public JSONObject listAllTeacher(JSONObject jsonObject) {
        return CommonUtil.successJson(courseTeacherDao.listAllTeacher(jsonObject));
    }

    @Override
    public JSONObject listCourseResult4Finance(JSONObject jsonObject) {
        CommonUtil.fillPageParam(jsonObject);

        if(StringUtils.hasText(jsonObject.getString("gradeAndClassRequest"))){
            String[] arr = jsonObject.getString("gradeAndClassRequest").split(",");
            jsonObject.put("gradeVal",arr[0]);
            if(arr.length>1){
                jsonObject.put("classVal",arr[1]);
            }
        }
        int count = courseTeacherDao.countCourseResult4Finance(jsonObject);
        List<JSONObject> list = courseTeacherDao.listCourseResult4Finance(jsonObject);
        return CommonUtil.successPage(jsonObject, list, count);

    }

    private void exeUpdateCourse(JSONObject jsonObject) {
        StringToJsonArrayObj(jsonObject, "grade");
        Session session = SecurityUtils.getSubject().getSession();
        JSONObject userInfo = (JSONObject) session.getAttribute(Constants.SESSION_USER_INFO);
        jsonObject.put("updateUser",userInfo.getInteger("userId"));

        courseTeacherDao.updateCourse(jsonObject);
    }

}
