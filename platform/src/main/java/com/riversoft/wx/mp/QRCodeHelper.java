package com.riversoft.wx.mp;

import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.media.Materials;
import com.riversoft.weixin.mp.media.bean.Material;
import com.riversoft.weixin.mp.ticket.Tickets;
import com.riversoft.weixin.mp.ticket.bean.Ticket;
import com.riversoft.wx.mp.service.MpAppService;

import java.io.ByteArrayInputStream;

/**
 * @borball on 4/2/2016.
 */
public class QRCodeHelper {

    private AppSetting appSetting;

    public QRCodeHelper() {
    }

    public QRCodeHelper(String mpKey) {
        this.appSetting = MpAppService.getInstance().getAppSettingByPK(mpKey);
    }

    public QRCodeHelper(AppSetting appSetting) {
        this.appSetting = appSetting;
    }

    public void setAppSetting(AppSetting appSetting) {
        this.appSetting = appSetting;
    }

    /**
     * 生成临时二维码上传为永久图片
     * @param validity
     * @param sceneId
     * @return 返回永久图片素材ID
     */
    public String temporary(int validity, int sceneId) {
        Ticket ticket = Tickets.with(appSetting).temporary(validity, sceneId);
        byte[] bytes = Tickets.with(appSetting).getQrcode(ticket.getTicket());

        String fileName = sceneId + ".jpg";
        Material material = Materials.with(appSetting).addImage(new ByteArrayInputStream(bytes), fileName);
        return material.getMediaId();
    }

    /**
     * 生成永久二维码上传为永久图片
     * @param sceneId
     * @return 返回永久图片素材ID
     */
    public String permanent(int sceneId) {
        Ticket ticket = Tickets.with(appSetting).permanent(sceneId);
        byte[] bytes = Tickets.with(appSetting).getQrcode(ticket.getTicket());

        String fileName = sceneId + ".jpg";
        Material material = Materials.with(appSetting).addImage(new ByteArrayInputStream(bytes), fileName);
        return material.getMediaId();
    }

    /**
     * 生成永久二维码上传为永久图片
     * @param sceneIdStr
     * @return 返回永久图片素材ID
     */
    public String permanent(String sceneIdStr) {
        Ticket ticket = Tickets.with(appSetting).permanent(sceneIdStr);
        byte[] bytes = Tickets.with(appSetting).getQrcode(ticket.getTicket());

        String fileName = sceneIdStr + ".jpg";
        Material material = Materials.with(appSetting).addImage(new ByteArrayInputStream(bytes), fileName);
        return material.getMediaId();
    }
}
