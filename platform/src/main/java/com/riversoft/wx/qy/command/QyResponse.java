package com.riversoft.wx.qy.command;

import com.riversoft.weixin.qy.message.json.JsonMessage;
import com.riversoft.wx.qy.builder.MessageBuilder;

import java.util.Map;

/**
 * Created by exizhai on 10/24/2015.
 */
public class QyResponse {

    private JsonMessage message;

    public QyResponse(int agentId, Object obj) {

        Map<String, Object> map = (Map<String, Object>) obj;
        if (map.containsKey("text")) {
            this.message = MessageBuilder.text(map);
        } else if (map.containsKey("news")) {
            this.message = MessageBuilder.news(map);
        } else if (map.containsKey("mpnews")) {
            this.message = MessageBuilder.mpnews(map);
        } else if (map.containsKey("image")) {
            this.message = MessageBuilder.image(map);
        } else if (map.containsKey("voice")) {
            this.message = MessageBuilder.voice(map);
        } else if (map.containsKey("video")) {
            this.message = MessageBuilder.video(map);
        }
        if (this.message != null) {
            this.message.agentId(agentId);
        }
    }

    public JsonMessage getMessage() {
        return message;
    }

    public QyResponse setUser(String uid) {
        if (this.message != null) {
            this.message.toUser(uid);
        }
        return this;
    }

    public QyResponse setGroup(String group) {
        if (this.message != null) {
            this.message.toParty(group);
        }
        return this;
    }

    public QyResponse setTag(String tag) {
        if (this.message != null) {
            this.message.toTag(tag);
        }
        return this;
    }
}
