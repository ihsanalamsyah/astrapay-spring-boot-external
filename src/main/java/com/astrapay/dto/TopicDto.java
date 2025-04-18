package com.astrapay.dto;

import javax.validation.constraints.NotEmpty;

public class TopicDto {

    @NotEmpty
    private Integer topicId;
    @NotEmpty
    private String topicName;
    private Boolean rowStatus;
    private Integer moveTo;

    public Integer getMoveTo(){
        return this.moveTo;
    }
    public Integer getTopicId(){
        return this.topicId;
    }
    public void setTopicId(Integer topicId){
        this.topicId = topicId;
    }
    public String getTopicName(){
        return this.topicName;
    }
    public void setTopicName(String topicName){
        this.topicName = topicName;
    }

    public Boolean getRowStatus(){
        return this.rowStatus;
    }
    public void setRowStatus(Boolean rowStatus){
        this.rowStatus = rowStatus;
    }
}
