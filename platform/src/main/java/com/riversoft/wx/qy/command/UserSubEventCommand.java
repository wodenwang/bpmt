package com.riversoft.wx.qy.command;

import com.riversoft.module.manager.user.UserService;

/**
 * Created by exizhai on 10/26/2015.
 */
public class UserSubEventCommand implements QyCommand {

    @Override
    public QyResponse execute(QyRequest qyRequest) {
        if (qyRequest.isSubscribe()) {
            UserService.getInstance().executeHQL("update UsUser set wxStatus = ? where uid = ?", 1, qyRequest.getUid());
        }
        if (qyRequest.isUnSubscribe()) {
            UserService.getInstance().executeHQL("update UsUser set wxStatus = ? where uid = ?", 4, qyRequest.getUid());
        }

        return null;
    }
}
