package com.scc.pub.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by AnthonyLenovo on 05/01/2019.
 */
public class Event<T> {

    private String type;
    protected String action;
    protected List<T> message;
    protected long timestamp;

    public Event() {

    }

    @JsonCreator
    public Event(@JsonProperty("type") String type, @JsonProperty("action") String action, @JsonProperty("message") List<T> message, @JsonProperty("timestamp") long timestamp) {
        this.type = type;
        this.action = action;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<T> getMessage() {
        return message;
    }

    public void setMessage(List<T> message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", message=" + message +
                ", timestamp=" + timestamp +
                '}';
    }
}
