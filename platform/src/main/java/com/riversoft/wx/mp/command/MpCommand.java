package com.riversoft.wx.mp.command;

import java.util.Map;

/**
 * Created by Borball on 9/23/2015.
 */
public interface MpCommand {

    MpResponse execute(MpRequest request);

}
