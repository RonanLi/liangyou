package com.liangyou.model.interfac;

import java.util.Date;

/**
 * Created by Young on 2016/11/21.
 */
public class MessageVO {
    private Date addTime;
    private String content;
    private String isRead;

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
