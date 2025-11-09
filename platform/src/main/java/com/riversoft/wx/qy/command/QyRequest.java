package com.riversoft.wx.qy.command;

import com.riversoft.wx.context.Request;

/**
 * 回调包装类 <br>
 * Created by exizhai on 9/23/2015.
 */
public class QyRequest extends Request {

    /**
     * 用户
     */
    private String uid;

    /**
     * 应用
     */
    private int agentId;
    private String agentKey;

    /**
     * 进入应用
     */
    private boolean enterAgent;

    /**
     * 二维码扫描值
     */
    private String qrCode;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentKey() {
        return agentKey;
    }

    public void setAgentKey(String agentKey) {
        this.agentKey = agentKey;
    }

    public boolean isEnterAgent() {
        return enterAgent;
    }

    public void setEnterAgent(boolean enterAgent) {
        this.enterAgent = enterAgent;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

}
