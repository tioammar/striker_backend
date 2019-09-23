package com.servlet.learning;

import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GaulController {

  @RequestMapping(value = "gaultpt", method = RequestMethod.GET)
  public ResultWrapper getTTRtpt(@RequestParam String cls, @RequestParam int bln){
    return new GaulService(cls, bln).getGaulTPT();
  } 

  @RequestMapping(value = "gaulubis", method = RequestMethod.GET)
  public ResultWrapper getTTRubis(@RequestParam int bln){
    return new GaulService(DBHelper.GET_ALL_STO, bln).getGaulUbis();
  } 
}