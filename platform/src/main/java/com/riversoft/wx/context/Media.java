package com.riversoft.wx.context;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.weixin.qy.media.Medias;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by exizhai on 10/24/2015.
 */
public abstract class Media {

    private String mediaId;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public byte[] getBytes() {
        File file = Medias.defaultMedias().download(mediaId);
        try {
            return IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            throw new SystemRuntimeException(ExceptionType.WX, e);
        }
    }
}
