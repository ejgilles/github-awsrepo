package com.learningdocker.demo.controller;

import com.learningdocker.demo.data.DataObject;
import com.learningdocker.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/s3bucketapidemo")
public class DockerDemoController {
    @Autowired
    DemoService demoService;

    @GetMapping("/testdocker")
    public String getControllerMessage() {
        return "Hello Docker World";
    }

    @PostMapping("/addobject")
    public void createObject(@RequestBody DataObject dataObject) throws Exception {
        this.demoService.uploadFile(dataObject);
    }

    @GetMapping("/fetchobject/{filename}")
    public void fetchObject(@PathVariable String filename) throws Exception {
        DataObject dataObject = new DataObject();
        dataObject.setName(filename);
        this.demoService.downloadFile(dataObject);
    }

    @GetMapping("/listobjects")
    public List<String> listObjects() throws Exception {
        return this.demoService.listObjects();
    }

    @PutMapping("/updateobject")
    public void updateObject(@RequestBody DataObject dataObject) throws Exception {
        this.demoService.uploadFile(dataObject);
    }

    @DeleteMapping("/deleteobject")
    public void deleteObject(@RequestBody DataObject dataObject) {
        this.demoService.deleteFile(dataObject);
    }

    @PostMapping("/addbucket")
    public DataObject createBucket(@RequestBody DataObject dataObject) {
        return this.demoService.addBucket(dataObject);
    }

    @GetMapping("/listbuckets")
    public List<String> listBuckets() {
        return this.demoService.listBuckets();
    }

    @DeleteMapping("/deletebucket")
    public void deleteBucket(@RequestBody DataObject dataObject) {
        this.demoService.deleteBucket(dataObject.getName());
    }
}
