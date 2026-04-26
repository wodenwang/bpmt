/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.riversoft.core.db.hbm.HbmClass;
import com.riversoft.core.db.hbm.HbmId;
import com.riversoft.core.db.hbm.HbmProperty;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;

/**
 * @author Woden
 * 
 */
public class HbmModelConverter {

    public static HbmClass toBean(InputStream is) {
        ExtendXStream xstream = new ExtendXStream();

        HibernateMappingModel hibernateMappingModel = (HibernateMappingModel) xstream.fromXML(is);

        return toHbmClass(hibernateMappingModel);
    }

    public static void toHbmFile(HbmClass hbmClass, OutputStream out) {
        ExtendXStream xstream = new ExtendXStream();
        HibernateMappingModel hibernateMappingModel = toHibernateMappingModel(hbmClass);
        String xml = xstream.toXML(hibernateMappingModel);

        try {
            out.write(xml.getBytes());
        } catch (IOException e) {
            throw new SystemRuntimeException(ExceptionType.HBM_PROCESS, e.getMessage());
        }

    }

    private static HbmClass toHbmClass(HibernateMappingModel model) {
        ClassModel classModel = model.getClassModel();

        // 设置类信息
        HbmClass hbmClass = new HbmClass();
        hbmClass.setTable(classModel.getTable());
        hbmClass.setComment(classModel.getComment());
        hbmClass.setEntityName(classModel.getEntityName());
        hbmClass.setEntityClass(classModel.getName());

        // 设置主键
        if (classModel.getId() != null) {// 单主键配置
            hbmClass.setId(getSingleHbmId(classModel));
        } else if (classModel.getCompositeId() != null) {// 多主键
            hbmClass.setId(getCompositeHbmId(classModel));
        } else {// 没有主键
            throw new SystemRuntimeException(ExceptionType.CODING, "hbm文件没有设置主键");
        }

        // 设置其他非主键字段
        List<HbmProperty> properties = getNormalHbmProperties(classModel);

        // 设置manytoone字段
        setManyToOnePropertys(properties, classModel);

        hbmClass.setPropertys(properties.toArray(new HbmProperty[properties.size()]));

        return hbmClass;
    }

    private static void setManyToOnePropertys(List<HbmProperty> properties, ClassModel classModel) {
        if (classModel.getManyToOneList() != null) {
            for (ManyToOneModel manyToOneModel : classModel.getManyToOneList()) {
                HbmProperty property = new HbmProperty();
                property.setType(manyToOneModel.getType());

                BeanUtils.copyProperties(manyToOneModel.getColumn(), property);
                property.setName(manyToOneModel.getName());
                property.setColumnName(manyToOneModel.getColumn().getName());

                properties.add(property);
            }
        }
    }

    private static List<HbmProperty> getNormalHbmProperties(ClassModel classModel) {
        List<HbmProperty> properties = new ArrayList<>();
        if (classModel.getProperties() != null) {
            for (PropertyModel propertyModel : classModel.getProperties()) {
                HbmProperty property = new HbmProperty();
                property.setType(propertyModel.getType());

                BeanUtils.copyProperties(propertyModel.getColumn(), property);
                property.setName(propertyModel.getName());
                property.setColumnName(propertyModel.getColumn().getName());

                properties.add(property);
            }
        }
        return properties;
    }

    private static HbmId getCompositeHbmId(ClassModel classModel) {
        HbmId hbmId = new HbmId();
        CompositeIdModel compositeIdModel = classModel.getCompositeId();
        GeneratorModel generatorModel = compositeIdModel.getGenerator();
        if (generatorModel != null) {
            hbmId.setGenerator(generatorModel.getClassName());
        }

        List<HbmProperty> properties = new ArrayList<>();
        for (KeyPropertyModel keyPropertyModel : compositeIdModel.getKeyProperties()) {
            HbmProperty property = new HbmProperty();

            property.setType(keyPropertyModel.getType());
            BeanUtils.copyProperties(keyPropertyModel.getColumn(), property);
            property.setName(keyPropertyModel.getName());
            property.setColumnName(keyPropertyModel.getColumn().getName());

            properties.add(property);
        }
        hbmId.setProperties(properties.toArray(new HbmProperty[properties.size()]));
        return hbmId;
    }

    private static HbmId getSingleHbmId(ClassModel classModel) {
        HbmId hbmId = new HbmId();
        IdModel idModel = classModel.getId();
        GeneratorModel generatorModel = idModel.getGenerator();
        if (generatorModel != null) {
            hbmId.setGenerator(generatorModel.getClassName());
        }

        HbmProperty property = new HbmProperty();

        property.setType(idModel.getType());
        BeanUtils.copyProperties(idModel.getColumn(), property);
        property.setName(idModel.getName());
        property.setColumnName(idModel.getColumn().getName());

        hbmId.setProperties(new HbmProperty[] { property });

        return hbmId;
    }

    private static HibernateMappingModel toHibernateMappingModel(HbmClass hbmClass) {
        HibernateMappingModel hibernateMappingModel = new HibernateMappingModel();
        ClassModel classModel = new ClassModel();
        classModel.setName(hbmClass.getEntityClass());
        classModel.setComment(hbmClass.getComment());
        classModel.setTable(hbmClass.getTable());
        classModel.setEntityName(hbmClass.getEntityName());

        HbmProperty[] ids = hbmClass.getId().getProperties();
        if (ids.length > 1) {
            classModel.setCompositeId(getCompositeIdModel(hbmClass.getId(), ids));
        } else if (ids.length == 1) {
            classModel.setId(getSingleIdModel(hbmClass.getId(), ids[0]));
        } else {
            throw new SystemRuntimeException(ExceptionType.CODING, "表模型没有主键");
        }

        HbmProperty[] normalProperties = hbmClass.getPropertys();

        if (normalProperties.length > 0) {
            classModel.setProperties(getNormalPropertyModels(normalProperties));
        }

        hibernateMappingModel.setClassModel(classModel);

        return hibernateMappingModel;

    }

    private static List<PropertyModel> getNormalPropertyModels(HbmProperty[] normalProperties) {
        List<PropertyModel> propertyModels = new ArrayList<>();

        for (HbmProperty hbmProperty : normalProperties) {
            PropertyModel propertyModel = new PropertyModel();
            propertyModel.setType(hbmProperty.getType());
            ColumnModel columnModel = new ColumnModel();
            BeanUtils.copyProperties(hbmProperty, columnModel);
            propertyModel.setName(hbmProperty.getName());
            columnModel.setName(hbmProperty.getColumnName());
            propertyModel.setColumn(columnModel);

            propertyModels.add(propertyModel);
        }
        return propertyModels;
    }

    private static CompositeIdModel getCompositeIdModel(HbmId hbmId, HbmProperty[] hbmProperties) {
        CompositeIdModel compositeIdModel = new CompositeIdModel();

        if (hbmId.getGenerator() != null) {
            GeneratorModel generatorModel = new GeneratorModel();
            generatorModel.setClassName(hbmId.getGenerator());
            compositeIdModel.setGenerator(generatorModel);
        }

        List<KeyPropertyModel> keyProperttModels = new ArrayList<>();

        for (HbmProperty hbmProperty : hbmProperties) {
            KeyPropertyModel keyPropertyModel = new KeyPropertyModel();
            keyPropertyModel.setType(hbmProperty.getType());
            ColumnModel columnModel = new ColumnModel();
            BeanUtils.copyProperties(hbmProperty, columnModel);
            keyPropertyModel.setName(hbmProperty.getName());
            columnModel.setName(hbmProperty.getColumnName());
            keyPropertyModel.setColumn(columnModel);
            keyProperttModels.add(keyPropertyModel);
        }
        compositeIdModel.setKeyProperties(keyProperttModels);
        return compositeIdModel;
    }

    private static IdModel getSingleIdModel(HbmId hbmId, HbmProperty hbmProperty) {
        IdModel idModel = new IdModel();

        if (hbmId.getGenerator() != null) {
            GeneratorModel generatorModel = new GeneratorModel();
            generatorModel.setClassName(hbmId.getGenerator());
            idModel.setGenerator(generatorModel);
        }

        ColumnModel columnModel = new ColumnModel();

        BeanUtils.copyProperties(hbmProperty, columnModel);
        columnModel.setName(hbmProperty.getColumnName());

        idModel.setColumn(columnModel);
        idModel.setName(hbmProperty.getName());
        idModel.setType(hbmProperty.getType());

        return idModel;

    }

}
