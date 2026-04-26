package com.riversoft.wx.qy.command;

import java.util.Map;

/**
 * Created by Borball on 9/23/2015.
 */
public interface QyCommand {

    QyResponse execute(QyRequest request);

}
