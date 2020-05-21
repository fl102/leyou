package com.leyou.controller;


import com.leyou.Service.SpecService;
import com.leyou.domain.Brand;
import com.leyou.domain.SpecGroup;
import com.leyou.domain.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;


    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> findGroupsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = this.specService.findGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> findParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ) {
        List<SpecParam> params = this.specService.findParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    @PostMapping("param")
    public ResponseEntity<Void> save(@RequestBody SpecParam specParam){
        this.specService.save(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("param/{pid}")
    public ResponseEntity<Void> save(@PathVariable("pid") Long pid){
        this.specService.delete(pid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> findGroupAndParamsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = this.specService.findGroupAndParamsByCid(cid);
        if (CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}
