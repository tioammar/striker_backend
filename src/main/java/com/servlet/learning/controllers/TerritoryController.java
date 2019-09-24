package com.servlet.learning.controllers;

import com.servlet.learning.TerritoryWrapper;
import com.servlet.learning.services.TerritoryService;
import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TerritoryController {

  @RequestMapping(value = "/tptlist",  method = RequestMethod.GET)
  public TerritoryWrapper getTPTbyClass(@RequestParam String cls){
    return new TerritoryService(cls).getTerritory();
  }

  @RequestMapping(value = "ubislist", method = RequestMethod.GET)
  public TerritoryWrapper getUbis(){
    return new TerritoryService(DBHelper.GET_ALL_STO).getTerritory();
  }
}