package com.servlet.learning.controllers;

import com.servlet.learning.ResultWrapper;
import com.servlet.learning.services.CollectionsService;
import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectionsController {

  @RequestMapping(value = "c3mrtpt", method = RequestMethod.GET)
  public ResultWrapper getTTRtpt(@RequestParam String cls, @RequestParam int bln){
    return new CollectionsService(cls, bln).getCollectionsTPT();
  } 

  @RequestMapping(value = "c3mrubis", method = RequestMethod.GET)
  public ResultWrapper getTTRubis(@RequestParam int bln){
    return new CollectionsService(DBHelper.GET_ALL_STO, bln).getCollectionsUbis();
  } 
}