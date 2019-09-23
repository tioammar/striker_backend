package com.servlet.learning;

import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TTRController {

  @RequestMapping(value = "ttrtpt", method = RequestMethod.GET)
  public ResultWrapper getTTRtpt(@RequestParam String cls, @RequestParam int bln){
    return new TTRService(cls, bln).getTTRtpt();
  } 

  @RequestMapping(value = "ttrubis", method = RequestMethod.GET)
  public ResultWrapper getTTRubis(@RequestParam int bln){
    return new TTRService(DBHelper.GET_ALL_STO, bln).getTTRubis();
  } 
}