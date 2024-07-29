package cn.iocoder.yudao.module.digitalcourse.service.coursescenecomponents;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.digitalcourse.controller.app.coursescenecomponents.vo.*;
import cn.iocoder.yudao.module.digitalcourse.dal.dataobject.coursescenecomponents.CourseSceneComponentsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 存储每个场景中的组件信息，包括PPT、数字人等 Service 接口
 *
 * @author 芋道源码
 */
public interface CourseSceneComponentsService {

    /**
     * 创建存储每个场景中的组件信息，包括PPT、数字人等
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCourseSceneComponents(@Valid AppCourseSceneComponentsSaveReqVO createReqVO);

    /**
     * 更新存储每个场景中的组件信息，包括PPT、数字人等
     *
     * @param updateReqVO 更新信息
     */
    void updateCourseSceneComponents(@Valid AppCourseSceneComponentsSaveReqVO updateReqVO);

    /**
     * 删除存储每个场景中的组件信息，包括PPT、数字人等
     *
     * @param id 编号
     */
    void deleteCourseSceneComponents(Long id);

    /**
     * 获得存储每个场景中的组件信息，包括PPT、数字人等
     *
     * @param id 编号
     * @return 存储每个场景中的组件信息，包括PPT、数字人等
     */
    CourseSceneComponentsDO getCourseSceneComponents(Long id);

    /**
     * 获得存储每个场景中的组件信息，包括PPT、数字人等分页
     *
     * @param pageReqVO 分页查询
     * @return 存储每个场景中的组件信息，包括PPT、数字人等分页
     */
    PageResult<CourseSceneComponentsDO> getCourseSceneComponentsPage(AppCourseSceneComponentsPageReqVO pageReqVO);

}