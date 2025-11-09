/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.identity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;

/**
 * @author woden
 * 
 */
public class CustomGroupManagerFactory implements SessionFactory {

    @Override
    public Class<?> getSessionType() {
        return GroupIdentityManager.class;
    }

    @Override
    public Session openSession() {
        return new CustomGroupEntityManager();
    }
}

class CustomGroupEntityManager extends AbstractManager implements GroupIdentityManager {

    @Override
    public Group createNewGroup(String groupId) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    @Override
    public void insertGroup(Group group) {
        throw new SystemRuntimeException(ExceptionType.CODING);

    }

    @Override
    public void updateGroup(Group updatedGroup) {
        throw new SystemRuntimeException(ExceptionType.CODING);

    }

    @Override
    public void deleteGroup(String groupId) {
        throw new SystemRuntimeException(ExceptionType.CODING);

    }

    @Override
    public GroupQuery createNewGroupQuery() {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    /**
     * 通过user id找对应的"组织"
     */
    @Override
    public List<Group> findGroupsByUser(String userId) {
        UsGroup group = SessionManager.getGroup();
        UsRole role = SessionManager.getRole();
        // 不查询数据库,仅直接返回当前会话的"组织"信息
        return Arrays.asList((Group) new GroupEntity(group.getGroupKey() + ";" + role.getRoleKey()), new GroupEntity(
                group.getGroupKey() + ";"), new GroupEntity(";" + role.getRoleKey()));
    }

    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

    @Override
    public boolean isNewGroup(Group group) {
        throw new SystemRuntimeException(ExceptionType.CODING);
    }

}