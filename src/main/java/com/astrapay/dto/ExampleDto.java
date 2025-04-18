package com.astrapay.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ExampleDto {
    @NotNull
    private Integer exampleId;
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    private Integer topicId;
    private Boolean rowStatus;
    private Integer moveToTopic;
    private Date entryDate;



    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Boolean getRowStatus(){return rowStatus;}
    public Integer getExampleId(){ return exampleId;}
    public Integer getTopicId(){ return topicId;}
    public Date getEntryDate(){ return entryDate;}
    public Integer getMoveToTopic(){ return moveToTopic;}

    public void setExampleId( Integer exampleId){
        this.exampleId = exampleId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTopicId(Integer topicId){
        this.topicId = topicId;
    }
    public void setRowStatus(Boolean rowStatus) {
        this.rowStatus = rowStatus;
    }

    public void setEntryDate(Date entryDate){
        if (entryDate == null)
            this.entryDate = new Date();
        else
            this.entryDate = entryDate;

    }
    public void setName(String name) {
        this.name = name;
    }
}