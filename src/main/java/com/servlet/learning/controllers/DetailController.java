package com.servlet.learning.controllers;

import com.servlet.learning.DetailWrapper;
import com.servlet.learning.services.DetailService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetailController {

  @RequestMapping(value = "tptdetail", method = RequestMethod.GET)
  public DetailWrapper getTPTDetail(@RequestParam int id, int bln){
    return new DetailService(id, bln).getTPTDetail();
  }

  @RequestMapping(value = "ubisdetail", method = RequestMethod.GET)
  public DetailWrapper getUbisDetail(@RequestParam int id, int bln){
    return new DetailService(id, bln).getUbisDetail();
  }
}