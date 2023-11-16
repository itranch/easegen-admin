package cn.iocoder.yudao.module.infra.dal.mysql.demo.demo03;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.demo.demo03.Demo03CourseDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生课程 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03CourseMapper extends BaseMapperX<Demo03CourseDO> {

    default List<Demo03CourseDO> selectListByStudentId(Long studentId) {
        return selectList(Demo03CourseDO::getStudentId, studentId);
    }

    default int deleteByStudentId(Long studentId) {
        return delete(Demo03CourseDO::getStudentId, studentId);
    }

}