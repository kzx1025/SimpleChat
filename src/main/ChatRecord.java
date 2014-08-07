package main;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ChatRecord implements Serializable {
 
    private String user_name;
    private String record;
    private String time;
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    public String getRecord() {
        return record;
    }
    public void setRecord(String record) {
        this.record = record;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
