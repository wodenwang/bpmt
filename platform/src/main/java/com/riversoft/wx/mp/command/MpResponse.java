package com.riversoft.wx.mp.command;

import com.riversoft.weixin.common.message.Article;
import com.riversoft.weixin.common.message.News;
import com.riversoft.weixin.mp.care.bean.Music;
import com.riversoft.weixin.mp.care.bean.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by exizhai on 12/8/2015.
 */
public class MpResponse {

    private Map<String, Object> message = new HashMap<>();

    public MpResponse(Map<String, Object> map) {
        if (map.containsKey("text")) {
            text((String) map.get("text"));
        } else if (map.containsKey("image")) {
            image((String) map.get("image"));
        } else if (map.containsKey("voice")) {
            voice((String) map.get("voice"));
        } else if (map.containsKey("card")) {
            card((String) map.get("card"));
        } else if (map.containsKey("news")) {
            news(map);
        } else if (map.containsKey("mpnews")) {
            mpnews((String) map.get("mpnews"));
        } else if (map.containsKey("video")) {
            video(map);
        } else if (map.containsKey("music")) {
            music(map);
        } else if (map.containsKey("kf")) {
            kf(map);
        }
    }

    private void kf(Map<String, Object> map) {
        message.put("type", "kf");
        message.put("kf", map.get("kf"));
    }

    public void text(String text){
        message.put("type", "text");
        message.put("text", text);
    }

    public void image(String image){
        message.put("type", "image");
        message.put("image", image);
    }

    public void voice(String voice){
        message.put("type", "voice");
        message.put("voice", voice);
    }

    public void card(String card){
        message.put("type", "card");
        message.put("card", card);
    }

    public void mpnews(String mpnews){
        message.put("type", "mpnews");
        message.put("mpnews", mpnews);
    }

    public void video(Map<String, Object> map){
        message.put("type", "video");
        Video video = new Video();
        video.setMediaId((String) map.get("video"));
        video.setDescription((String) map.get("desc"));
        video.setTitle((String) map.get("title"));
        video.setThumbMediaId((String) map.get("thumb"));

        message.put("video", video);
    }

    public void music(Map<String, Object> map){
        message.put("type", "music");
        Music music = new Music();
        music.setTitle((String) map.get("title"));
        music.setThumbMediaId((String) map.get("thumb"));
        music.setDescription((String) map.get("desc"));
        music.setMusicUrl((String) map.get("musicUrl"));
        music.setHqMusicUrl((String) map.get("hqMusicUrl"));
        message.put("music", music);
    }

    public void news(Map<String, Object> map) {
        message.put("type", "news");

        List<Map<String, Object>> list;
        if (map.containsKey("news")) {
            Object obj = map.get("news");
            if (obj instanceof List) {
                list = (List<Map<String, Object>>) map.get("news");
            } else if (obj instanceof Map) {
                list = new ArrayList<>();
                list.add((Map) obj);
            } else {
                list = new ArrayList<>();
            }
        } else {
            list = new ArrayList<>();
            list.add(map);
        }

        News news = new News();
        for (Map<String, Object> o : list) {
            Article article = new Article();
            article.title((String) o.get("title")).description((String) o.get("desc")).picUrl((String) o.get("picUrl")).url((String) o.get("url"));
            news.article(article);
        }

        message.put("news", news);
    }

    public String getType(){
            return (String)message.get("type");
    }

    public String getText() {
        return (String)message.get("text");
    }

    public String getImage() {
        return (String)message.get("image");
    }

    public String getVoice() {
        return (String)message.get("voice");
    }

    public String getCard() {
        return (String)message.get("card");
    }

    public News getNews(){
        return (News)message.get("news");
    }
    public String getMpnews() {
        return (String)message.get("mpnews");
    }

    public Music getMusic() {
        return (Music)message.get("music");
    }

    public Video getVideo() {
        return (Video)message.get("video");
    }

    public String getKf() {
        return message.get("kf").toString();
    }
}
