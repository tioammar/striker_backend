package com.servlet.learning.controllers;

import com.servlet.learning.ResultWrapper;
import com.servlet.learning.services.SalesService;
import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SalesController {

  @RequestMapping(value = "/salestpt", method = RequestMethod.GET)
  public ResultWrapper getSalesTPT(@RequestParam String cls, @RequestParam int bln){
    return new SalesService(bln, cls).getSalesTPTArray();
  }

  @RequestMapping(value = "/salesubis", method = RequestMethod.GET)
  public ResultWrapper getSalesUbis(@RequestParam int bln){
    return new SalesService(bln, DBHelper.GET_ALL_STO).getSalesUbisArray();
  }
}