package cn.iocoder.yudao.module.iot.service.thinkmodelfunction;

import cn.iocoder.yudao.framework.common.validation.Telephone;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.ThingModelEvent;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.ThingModelProperty;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.ThingModelService;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.dataType.ThingModelArgument;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.dataType.ThingModelArraySpecs;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.dataType.ThingModelArrayType;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.thingModel.dataType.ThingModelTextType;
import cn.iocoder.yudao.module.iot.controller.admin.thinkmodelfunction.vo.IotThinkModelFunctionSaveReqVO;
import cn.iocoder.yudao.module.iot.convert.thinkmodelfunction.IotThinkModelFunctionConvert;
import cn.iocoder.yudao.module.iot.dal.dataobject.thinkmodelfunction.IotThinkModelFunctionDO;
import cn.iocoder.yudao.module.iot.dal.mysql.thinkmodelfunction.IotThinkModelFunctionMapper;
import cn.iocoder.yudao.module.iot.enums.product.IotThingModelTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.THINK_MODEL_FUNCTION_EXISTS_BY_IDENTIFIER;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.THINK_MODEL_FUNCTION_NOT_EXISTS;

@Slf4j
@Service
@Validated
public class IotThinkModelFunctionServiceImpl implements IotThinkModelFunctionService {

    @Resource
    private IotThinkModelFunctionMapper thinkModelFunctionMapper;

    @Override
    public Long createThinkModelFunction(IotThinkModelFunctionSaveReqVO createReqVO) {
        // 校验功能标识符在同一产品下是否唯一
        validateIdentifierUnique(createReqVO.getProductId(), createReqVO.getIdentifier());

        // 转换请求对象为数据对象
        IotThinkModelFunctionDO thinkModelFunction = IotThinkModelFunctionConvert.INSTANCE.convert(createReqVO);

        // 插入数据库
        thinkModelFunctionMapper.insert(thinkModelFunction);

        // 如果创建的是属性，需要更新默认的事件和服务
        if (Objects.equals(createReqVO.getType(), IotThingModelTypeEnum.PROPERTY.getType())) {
            generateDefaultEventsAndServices(createReqVO.getProductId(), createReqVO.getProductKey());
        }

        // 返回生成的 ID
        return thinkModelFunction.getId();
    }

    private void validateIdentifierUnique(Long productId, String identifier) {
        IotThinkModelFunctionDO existingFunction = thinkModelFunctionMapper.selectByProductIdAndIdentifier(productId, identifier);
        if (existingFunction != null) {
            throw exception(THINK_MODEL_FUNCTION_EXISTS_BY_IDENTIFIER);
        }
    }

    @Override
    public void updateThinkModelFunction(IotThinkModelFunctionSaveReqVO updateReqVO) {
        // 校验功能是否存在
        validateThinkModelFunctionExists(updateReqVO.getId());

        // 校验功能标识符是否唯一
        validateIdentifierUniqueForUpdate(updateReqVO.getId(), updateReqVO.getProductId(), updateReqVO.getIdentifier());

        // 转换请求对象为数据对象
        IotThinkModelFunctionDO thinkModelFunction = IotThinkModelFunctionConvert.INSTANCE.convert(updateReqVO);

        // 更新数据库
        thinkModelFunctionMapper.updateById(thinkModelFunction);

        // 如果更新的是属性，需要更新默认的事件和服务
        if (Objects.equals(updateReqVO.getType(), IotThingModelTypeEnum.PROPERTY.getType())) {
            generateDefaultEventsAndServices(updateReqVO.getProductId(), updateReqVO.getProductKey());
        }
    }

    private void validateIdentifierUniqueForUpdate(Long id, Long productId, String identifier) {
        IotThinkModelFunctionDO existingFunction = thinkModelFunctionMapper.selectByProductIdAndIdentifier(productId, identifier);
        if (existingFunction != null && !existingFunction.getId().equals(id)) {
            throw exception(THINK_MODEL_FUNCTION_EXISTS_BY_IDENTIFIER);
        }
    }


    @Override
    public void deleteThinkModelFunction(Long id) {
        // 校验功能是否存在
        IotThinkModelFunctionDO functionDO = thinkModelFunctionMapper.selectById(id);
        if (functionDO == null) {
            throw exception(THINK_MODEL_FUNCTION_NOT_EXISTS);
        }

        // 删除功能
        thinkModelFunctionMapper.deleteById(id);

        // 如果删除的是属性，需要更新默认的事件和服务
        if (Objects.equals(functionDO.getType(), IotThingModelTypeEnum.PROPERTY.getType())) {
            generateDefaultEventsAndServices(functionDO.getProductId(), functionDO.getProductKey());
        }
    }

    /**
     * 校验功能是否存在
     *
     * @param id 功能编号
     */
    private void validateThinkModelFunctionExists(Long id) {
        if (thinkModelFunctionMapper.selectById(id) == null) {
            throw exception(THINK_MODEL_FUNCTION_NOT_EXISTS);
        }
    }

    @Override
    public IotThinkModelFunctionDO getThinkModelFunction(Long id) {
        return thinkModelFunctionMapper.selectById(id);
    }

    @Override
    public List<IotThinkModelFunctionDO> getThinkModelFunctionListByProductId(Long productId) {
        return thinkModelFunctionMapper.selectListByProductId(productId);
    }

    /**
     * 生成默认的事件和服务
     */
    public void generateDefaultEventsAndServices(Long productId, String productKey) {
        // 获取当前产品的所有属性列表
        List<IotThinkModelFunctionDO> propertyList = thinkModelFunctionMapper.selectListByProductIdAndType(productId, IotThingModelTypeEnum.PROPERTY.getType());

        // 生成属性上报事件
        ThingModelEvent propertyPostEvent = generatePropertyPostEvent(propertyList);
        if (propertyPostEvent != null) {
            saveOrUpdateEvent(productId, productKey, propertyPostEvent);
        }

        // 生成属性设置服务
        ThingModelService propertySetService = generatePropertySetService(propertyList);
        if (propertySetService != null) {
            saveOrUpdateService(productId, productKey, propertySetService);
        }

        // 生成属性获取服务
        ThingModelService propertyGetService = generatePropertyGetService(propertyList);
        if (propertyGetService != null) {
            saveOrUpdateService(productId, productKey, propertyGetService);
        }
    }

    /**
     * 生成属性上报事件
     */
    private ThingModelEvent generatePropertyPostEvent(List<IotThinkModelFunctionDO> propertyList) {
        if (propertyList == null || propertyList.isEmpty()) {
            return null;
        }

        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier("post");
        event.setName("属性上报");
        event.setType("info");
        event.setDescription("属性上报事件");
        event.setMethod("thing.event.property.post");

        // 将属性列表转换为事件的输出参数
        List<ThingModelArgument> outputData = new ArrayList<>();
        for (IotThinkModelFunctionDO functionDO : propertyList) {
            ThingModelProperty property = functionDO.getProperty();
            ThingModelArgument arg = new ThingModelArgument();
            arg.setIdentifier(property.getIdentifier());
            arg.setName(property.getName());
            arg.setDataType(property.getDataType());
            arg.setDescription(property.getDescription());
            arg.setDirection("output"); // 设置为输出参数
            outputData.add(arg);
        }
        event.setOutputData(outputData);

        return event;
    }

    /**
     * 生成属性设置服务
     */
    private ThingModelService generatePropertySetService(List<IotThinkModelFunctionDO> propertyList) {
        if (propertyList == null || propertyList.isEmpty()) {
            return null;
        }

        List<ThingModelArgument> inputData = new ArrayList<>();
        for (IotThinkModelFunctionDO functionDO : propertyList) {
            ThingModelProperty property = functionDO.getProperty();
            if ("w".equals(property.getAccessMode()) || "rw".equals(property.getAccessMode())) {
                ThingModelArgument arg = new ThingModelArgument();
                arg.setIdentifier(property.getIdentifier());
                arg.setName(property.getName());
                arg.setDataType(property.getDataType());
                arg.setDescription(property.getDescription());
                arg.setDirection("input"); // 设置为输入参数
                inputData.add(arg);
            }
        }
        if (inputData.isEmpty()) {
            // 如果没有可写属性，不生成属性设置服务
            return null;
        }

        ThingModelService service = new ThingModelService();
        service.setIdentifier("set");
        service.setName("属性设置");
        service.setCallType("async");
        service.setDescription("属性设置服务");
        service.setMethod("thing.service.property.set");
        service.setInputData(inputData);
        // 属性设置服务一般不需要输出参数
        service.setOutputData(new ArrayList<>());

        return service;
    }

    /**
     * 生成属性获取服务
     */
    private ThingModelService generatePropertyGetService(List<IotThinkModelFunctionDO> propertyList) {
        if (propertyList == null || propertyList.isEmpty()) {
            return null;
        }

        List<ThingModelArgument> outputData = new ArrayList<>();
        for (IotThinkModelFunctionDO functionDO : propertyList) {
            ThingModelProperty property = functionDO.getProperty();
            if ("r".equals(property.getAccessMode()) || "rw".equals(property.getAccessMode())) {
                ThingModelArgument arg = new ThingModelArgument();
                arg.setIdentifier(property.getIdentifier());
                arg.setName(property.getName());
                arg.setDataType(property.getDataType());
                arg.setDescription(property.getDescription());
                arg.setDirection("output"); // 设置为输出参数
                outputData.add(arg);
            }
        }
        if (outputData.isEmpty()) {
            // 如果没有可读属性，不生成属性获取服务
            return null;
        }

        ThingModelService service = new ThingModelService();
        service.setIdentifier("get");
        service.setName("属性获取");
        service.setCallType("async");
        service.setDescription("属性获取服务");
        service.setMethod("thing.service.property.get");

        // 定义输入参数：属性标识符列表
        ThingModelArgument inputArg = new ThingModelArgument();
        inputArg.setIdentifier("properties");
        inputArg.setName("属性标识符列表");
        inputArg.setDescription("需要获取的属性标识符列表");
        inputArg.setDirection("input"); // 设置为输入参数

        // 创建数组类型，元素类型为文本类型（字符串）
        ThingModelArrayType arrayType = new ThingModelArrayType();
        arrayType.setType("array");
        ThingModelArraySpecs arraySpecs = new ThingModelArraySpecs();
        ThingModelTextType textType = new ThingModelTextType();
        textType.setType("text");
        arraySpecs.setItem(textType);
        arrayType.setSpecs(arraySpecs);

        inputArg.setDataType(arrayType);

        service.setInputData(Collections.singletonList(inputArg));
        service.setOutputData(outputData);

        return service;
    }

    /**
     * 保存或更新事件
     */
    private void saveOrUpdateEvent(Long productId, String productKey, ThingModelEvent event) {
        // 检查是否已存在相同的事件
        IotThinkModelFunctionDO existingEvent = thinkModelFunctionMapper.selectByProductIdAndIdentifier(productId, event.getIdentifier());
        IotThinkModelFunctionDO functionDO = new IotThinkModelFunctionDO();
        functionDO.setProductId(productId);
        functionDO.setProductKey(productKey);
        functionDO.setIdentifier(event.getIdentifier());
        functionDO.setName(event.getName());
        functionDO.setDescription(event.getDescription());
        functionDO.setType(IotThingModelTypeEnum.EVENT.getType());
        functionDO.setEvent(event);

        if (existingEvent != null) {
            // 更新事件
            functionDO.setId(existingEvent.getId());
            thinkModelFunctionMapper.updateById(functionDO);
        } else {
            // 创建新的事件
            thinkModelFunctionMapper.insert(functionDO);
        }
    }

    /**
     * 保存或更新事服务
     */
    private void saveOrUpdateService(Long productId, String productKey, ThingModelService service) {
        // 检查是否已存在相同的服务
        IotThinkModelFunctionDO existingService = thinkModelFunctionMapper.selectByProductIdAndIdentifier(productId, service.getIdentifier());
        IotThinkModelFunctionDO functionDO = new IotThinkModelFunctionDO();
        functionDO.setProductId(productId);
        functionDO.setProductKey(productKey);
        functionDO.setIdentifier(service.getIdentifier());
        functionDO.setName(service.getName());
        functionDO.setDescription(service.getDescription());
        functionDO.setType(IotThingModelTypeEnum.SERVICE.getType());
        functionDO.setService(service);

        if (existingService != null) {
            // 更新服务
            functionDO.setId(existingService.getId());
            thinkModelFunctionMapper.updateById(functionDO);
        } else {
            // 创建新的服务
            thinkModelFunctionMapper.insert(functionDO);
        }
    }
}
