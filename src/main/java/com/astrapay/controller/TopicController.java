package com.astrapay.controller;

import com.astrapay.dto.ExampleDto;
import com.astrapay.dto.TopicDto;
import com.astrapay.exception.ExampleException;
import com.astrapay.service.ExampleService;
import com.astrapay.service.MemoryStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestController
@Api(value = "TopicController")
@Slf4j
public class TopicController {

    private static final Logger _log = LoggerFactory.getLogger(TopicController.class);

    private final MemoryStoreService<TopicDto> _memoryStore;
    private final AtomicInteger _idCounter;

    @Autowired
    public TopicController(MemoryStoreService<TopicDto> memoryStoreService) {
        this._memoryStore = memoryStoreService;
        this._idCounter = new AtomicInteger(1);
    }

    @PostMapping("/api/AddTopic")
    @ApiOperation(value = "Add Topic")
    public ResponseEntity<String> AddTopic(@RequestBody TopicDto topicDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try{
            Integer id = _idCounter.getAndIncrement();
            topicDto.setTopicId(id);
            topicDto.setRowStatus(true);
            _memoryStore.put(id, topicDto);
            return ResponseEntity.ok("Success Add Topic");
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/DeleteTopic")
    @ApiOperation(value = "Delete Topic")
    public ResponseEntity<String> DeleteTopic(@RequestBody Integer Id){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try {
            var memoryStore = _memoryStore.get(Id);
            memoryStore.setRowStatus(false);
            _memoryStore.put(Id, memoryStore);
            return ResponseEntity.ok("Deleted");
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/GetTopic")
    @ApiOperation(value = "Get Topic")
    public ResponseEntity<List<TopicDto>> GetTopic(@RequestBody TopicDto topicDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);
        List<TopicDto> topicDtos = List.of();
        try {
            topicDtos = _memoryStore.find(x -> x.getRowStatus() == true);

            return ResponseEntity.ok(topicDtos);
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(topicDtos);
        }
    }

    @PostMapping("/api/EditTopic")
    @ApiOperation(value = "Edit Topic")
    public ResponseEntity<String> EditTopic(@RequestBody TopicDto topicDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try {
            var topicDto1 = _memoryStore.get(topicDto.getTopicId());
            topicDto1.setTopicName(topicDto.getTopicName());
            topicDto1.setRowStatus(true);
            _memoryStore.put(topicDto.getTopicId(), topicDto1);
            return ResponseEntity.ok("Edited");
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/MoveTopic")
    @ApiOperation(value = "Move Topic")
    public ResponseEntity<String> MoveTopic(@RequestBody TopicDto topicDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try {
            var topicDto1 = _memoryStore.findFirst(x -> x.getTopicId() == topicDto.getTopicId());
            var existsTopic = _memoryStore.findFirst(x -> x.getTopicId() == topicDto.getMoveTo());

            if (existsTopic != null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("Move to %s Not valid ", topicDto.getMoveTo()));
            }
            topicDto1.setTopicId(topicDto.getMoveTo());
            _memoryStore.put(topicDto.getTopicId(), topicDto1);
            return ResponseEntity.ok("Edited");
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
