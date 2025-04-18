package com.astrapay.controller;

import com.astrapay.dto.ExampleDto;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@Api(value = "ExampleController")
@Slf4j
public class ExampleController {

    private static final Logger _log = LoggerFactory.getLogger(ExampleController.class);
    private final ExampleService _exampleService;
    private final MemoryStoreService<ExampleDto> _memoryStore;
    private final AtomicInteger _idCounter;
    @Autowired
    public ExampleController(ExampleService exampleService, MemoryStoreService<ExampleDto> memoryStoreService) {
        this._exampleService = exampleService;
        this._memoryStore = memoryStoreService;
        this._idCounter = new AtomicInteger(1);
    }

    @GetMapping("/api/hello")
    @ApiOperation(value = "Say Hello")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "OK", response = ExampleDto.class)
            }
    )
    public ResponseEntity<String> sayHello(@RequestParam String name, @RequestParam String description) {

        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Incoming hello Request from {}", name);
        try {

            ExampleDto exampleDto = new ExampleDto();
            exampleDto.setName(name);
            exampleDto.setDescription(description);

            return ResponseEntity.ok(_exampleService.sayHello(exampleDto));
        } catch (ExampleException e) {
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/AddExample")
    @ApiOperation(value = "Add Example")
    public ResponseEntity<String> EditExample(@RequestBody ExampleDto exampleDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try{
            if(exampleDto.getTopicId() > 5){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Topic Id can't more than 5");
            }
            Integer id = _idCounter.getAndIncrement();
            exampleDto.setExampleId(id);
            exampleDto.setRowStatus(true);
            exampleDto.setEntryDate(null);
            _memoryStore.put(id, exampleDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success Add");
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/GetExampleById")
    @ApiOperation(value = "Get Example By Id")
    public ResponseEntity<ExampleDto> GetExampleById(@RequestBody Integer Id){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);
        ExampleDto exampleDto = new ExampleDto();
        try{
            exampleDto = _memoryStore.get(Id);
            return ResponseEntity.status(HttpStatus.OK).body(exampleDto);
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exampleDto);
        }
    }

    @PostMapping("/api/GetExample")
    @ApiOperation(value = "Get Example")
    public ResponseEntity<List<ExampleDto>> GetExample(){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);
        List<ExampleDto> exampleDtos = List.of();
        try {
            exampleDtos = _memoryStore.find(exampleDto -> exampleDto.getRowStatus() == true);

            return ResponseEntity.ok(exampleDtos);
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exampleDtos);
        }
    }


    @PostMapping("/api/ViewAllExample")
    @ApiOperation(value = "View All Example")
    public ResponseEntity<List<ExampleDto>> ViewAllExample(){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);
        List<ExampleDto> exampleDtos = List.of();
        try {
            exampleDtos = _memoryStore.getAll();

            return ResponseEntity.status(HttpStatus.OK).body(exampleDtos);
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exampleDtos);
        }
    }

    @PostMapping("/api/DeleteExample")
    @ApiOperation(value = "Delete Example")
    public ResponseEntity<String> DeleteExample(@RequestBody Integer Id){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try {
            var memoryStore = _memoryStore.get(Id);
            memoryStore.setRowStatus(false);
            _memoryStore.put(Id, memoryStore);
            return ResponseEntity.ok(String.format("Deleted %s", Id));
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/MoveExample")
    @ApiOperation(value = "Move Example")
    public ResponseEntity<String> DeleteExample(@RequestBody ExampleDto exampleDto){
        String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
        _log.info("Function {} begin ", functionName);

        try {
            if(exampleDto.getMoveToTopic() > 5){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error Move To can't more than 5");
            }
            var findExampleDto = _memoryStore.findFirst(x -> x.getExampleId() == exampleDto.getExampleId());
            if (findExampleDto == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format("Error Invalid ExampleId %s", exampleDto.getExampleId()));
            }
            findExampleDto.setTopicId(exampleDto.getMoveToTopic());

            _memoryStore.put(exampleDto.getExampleId(), findExampleDto);
            return ResponseEntity.ok(String.format("Moved to %s", exampleDto.getMoveToTopic()));
        }catch (ExampleException e){
            _log.error("Error function: {}, {}", functionName, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}