package cn.iocoder.yudao.module.digitalcourse.service.courses;

import cn.iocoder.yudao.module.digitalcourse.controller.admin.courses.vo.AppCoursesPageReqVO;
import cn.iocoder.yudao.module.digitalcourse.controller.admin.courses.vo.AppCoursesSaveReqVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import cn.iocoder.yudao.module.digitalcourse.dal.dataobject.courses.CoursesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.digitalcourse.dal.mysql.courses.CoursesMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.digitalcourse.enums.ErrorCodeConstants.*;

/**
 * 存储课程的基本信息，包括课程名称、时长、状态等 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CoursesServiceImpl implements CoursesService {

    @Resource
    private CoursesMapper coursesMapper;

    @Override
    public Long createCourses(AppCoursesSaveReqVO createReqVO) {
        // 插入
        CoursesDO courses = BeanUtils.toBean(createReqVO, CoursesDO.class);
        if (StringUtils.isBlank(courses.getName())) courses.setName("未命名草稿");
        if (StringUtils.isBlank(courses.getAspect())) courses.setAspect("16:9");
        if (courses.getDuration() == null) courses.setDuration(0);
        if (courses.getHeight() == null) courses.setHeight(0);
        if (courses.getWidth() == null) courses.setWidth(0);
        if (courses.getMatting() == null) courses.setMatting(0);
        if (courses.getPageMode() == null) courses.setPageMode(0);
        if (courses.getStatus() == null) courses.setStatus(0);
        if (courses.getPageInfo() == null) courses.setPageInfo("");
        if (courses.getSubtitlesStyle() == null) courses.setSubtitlesStyle("{}");
        coursesMapper.insert(courses);
        // 返回
        return courses.getId();
    }

    @Override
    public void updateCourses(AppCoursesSaveReqVO updateReqVO) {
        // 校验存在
        validateCoursesExists(updateReqVO.getId());
        // 更新
        CoursesDO updateObj = BeanUtils.toBean(updateReqVO, CoursesDO.class);
        coursesMapper.updateById(updateObj);
    }

    @Override
    public void deleteCourses(Long id) {
        // 校验存在
        validateCoursesExists(id);
        // 删除
        coursesMapper.deleteById(id);
    }

    private void validateCoursesExists(Long id) {
        if (coursesMapper.selectById(id) == null) {
            throw exception(COURSES_NOT_EXISTS);
        }
    }

    @Override
    public CoursesDO getCourses(Long id) {
        return coursesMapper.selectById(id);
    }

    @Override
    public PageResult<CoursesDO> getCoursesPage(AppCoursesPageReqVO pageReqVO) {
        return coursesMapper.selectPage(pageReqVO);
    }

}