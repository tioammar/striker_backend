package com.servlet.learning.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.servlet.learning.services.TTRService;
import com.servlet.learning.SimpleTopDataWrapper;
import com.servlet.learning.TopDataWrapper;
import com.servlet.learning.obj.Result;
import com.servlet.learning.obj.STO;
import com.servlet.learning.obj.TopResult;
import com.servlet.learning.obj.Ubis;
import com.servlet.learning.services.CollectionsService;
import com.servlet.learning.services.GaulService;
import com.servlet.learning.services.SalesService;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

@RestController
public class TopTerritoryController {

  private ResultSet mResultSet;
  private Statement mStatement;

  Double salesBobot = 0.4;
  Double ttrBobot = 0.15;
  Double gaulBobot = 0.15;
  Double c3mrBobot = 0.3;

  @RequestMapping(value = "/topterritory", method = RequestMethod.GET)
  public TopDataWrapper getTopData(@RequestParam int bln){
    return getAllData(bln);
  }

  @RequestMapping(value = "/toptpt", method = RequestMethod.GET)
  public SimpleTopDataWrapper getTopDataTPT(@RequestParam int bln, @RequestParam String cls){
    return getAllDataIndicatorTPT(bln, cls);
  }

  private SimpleTopDataWrapper getAllDataIndicatorTPT(int bln, String cls) {
    List<Result> sales = new SalesService(bln, cls).getSalesTPTArray().getData();
    List<Result> gaul = new GaulService(cls, bln).getGaulTPT().getData();
    List<Result> c3mr = new CollectionsService(cls, bln).getCollectionsTPT().getData();
    List<Result> ttr = new TTRService(cls, bln).getTTRtpt().getData();

    List<STO> tpt = getSTOByClass(cls);
    List<TopResult> top = mapDataTPT(sales, ttr, gaul, c3mr, tpt);
    return new SimpleTopDataWrapper(DBHelper.GET_DATA_SUCESS, top);
  }

  @RequestMapping(value = "/topubis", method = RequestMethod.GET)
  public SimpleTopDataWrapper getTopDataUbis(@RequestParam int bln){
    return getAllDataIndicatorUbis(bln);
  }

  private SimpleTopDataWrapper getAllDataIndicatorUbis(int bln) {    
    List<Result> salesUbis = new SalesService(bln, DBHelper.GET_ALL_STO).getSalesUbisArray().getData();
    List<Result> gaulUbis = new GaulService(DBHelper.GET_ALL_STO, bln).getGaulUbis().getData();
    List<Result> c3mrUbis = new CollectionsService(DBHelper.GET_ALL_STO, bln).getCollectionsUbis().getData();
    List<Result> ttrUbis = new TTRService(DBHelper.GET_ALL_STO, bln).getTTRubis().getData();

    List<Ubis> ubis = getAllUbis();
    List<TopResult> top = mapDataUbis(salesUbis, ttrUbis, gaulUbis, c3mrUbis, ubis);
    return new SimpleTopDataWrapper(DBHelper.GET_DATA_SUCESS, top);
  }

  private TopDataWrapper getAllData(int bln) {
    // Get All Sales Data
    List<Result> salesA = new SalesService(bln, "A").getSalesTPTArray().getData();
    List<Result> salesB = new SalesService(bln, "B").getSalesTPTArray().getData();
    List<Result> salesC = new SalesService(bln, "C").getSalesTPTArray().getData();
    List<Result> salesUbis = new SalesService(bln, DBHelper.GET_ALL_STO).getSalesUbisArray().getData();
    // Get All Gaul Data
    List<Result> gaulA = new GaulService("A", bln).getGaulTPT().getData();
    List<Result> gaulB = new GaulService("B", bln).getGaulTPT().getData();
    List<Result> gaulC = new GaulService("C", bln).getGaulTPT().getData();
    List<Result> gaulUbis = new GaulService(DBHelper.GET_ALL_STO, bln).getGaulUbis().getData();
    // Get All Collection Data
    List<Result> c3mrA = new CollectionsService("A", bln).getCollectionsTPT().getData();
    List<Result> c3mrB = new CollectionsService("B", bln).getCollectionsTPT().getData();
    List<Result> c3mrC = new CollectionsService("C", bln).getCollectionsTPT().getData();
    List<Result> c3mrUbis = new CollectionsService(DBHelper.GET_ALL_STO, bln).getCollectionsUbis().getData();
    // Get All TTR Data
    List<Result> ttrA = new TTRService("A", bln).getTTRtpt().getData();
    List<Result> ttrB = new TTRService("B", bln).getTTRtpt().getData();
    List<Result> ttrC = new TTRService("C", bln).getTTRtpt().getData();
    List<Result> ttrUbis = new TTRService(DBHelper.GET_ALL_STO, bln).getTTRubis().getData();

    List<STO> tptA = getSTOByClass("A");
    List<STO> tptB = getSTOByClass("B");
    List<STO> tptC = getSTOByClass("C");
    List<Ubis> ubis = getAllUbis();

    List<TopResult> topA = getTop3(mapDataTPT(salesA, ttrA, gaulA, c3mrA, tptA));
    List<TopResult> topB = getTop3(mapDataTPT(salesB, ttrB, gaulB, c3mrB, tptB));
    List<TopResult> topC = getTop3(mapDataTPT(salesC, ttrC, gaulC, c3mrC, tptC));
    List<TopResult> topUbis = getTop3(mapDataUbis(salesUbis, ttrUbis, gaulUbis, c3mrUbis, ubis));
    return new TopDataWrapper(DBHelper.GET_DATA_SUCESS, topA, topB, topC, topUbis);
  }

  private List<Ubis> getAllUbis(){
    List<Ubis> ubis = new ArrayList<>();
    Connection conn = new DBConnectSQL().getConnection();
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery("select distinct datel, witel from sto_profile where datel != 'N'");
      while(mResultSet.next()){
        String location = mResultSet.getString("datel");
        String witel = mResultSet.getString("witel");
        ubis.add(new Ubis(location, witel, 0.0));
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mStatement != null) mResultSet.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }
    return ubis;
  }

  private List<STO> getSTOByClass(String cls){
    List<STO> stoList = new ArrayList<>();
    Connection conn = new DBConnectSQL().getConnection();
    String query = "select distinct sto_str, sto, witel from sto_profile where kelas = '"+cls+"'";
    // LOGGER.info(query);
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String name = mResultSet.getString("sto_str");
        String code = mResultSet.getString("sto");
        String witel = mResultSet.getString("witel");
        stoList.add(new STO(name, code, witel, 0.0));
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mStatement.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }
    return stoList;
  }

  private List<TopResult> mapDataTPT(List<Result> salesData, 
    List<Result> ttrData,
    List<Result> gaulData,
    List<Result> c3mrData, 
    List<STO> tpt){

    List<TopResult> data = new ArrayList<>();

    for(STO t : tpt){
      Double salesScore = 0.0;
      Double gaulScore = 0.0;
      Double ttrScore = 0.0;
      Double c3mrScore = 0.0;
      for(Result s : salesData){
        if(s.getLocation().equals(t.getName())) salesScore = s.getAchievement() * this.salesBobot;
      }
      for(Result ttr : ttrData){
        if(ttr.getLocation().equals(t.getName())) ttrScore = ttr.getAchievement() * this.ttrBobot;
      }
      for(Result gaul : gaulData){
        if(gaul.getLocation().equals(t.getName())) gaulScore = gaul.getAchievement() * this.gaulBobot;
      }
      for(Result c3mr : c3mrData){
        if(c3mr.getLocation().equals(t.getName())) c3mrScore = c3mr.getAchievement() * this.c3mrBobot;
      }
      Double totalScore = salesScore + gaulScore + ttrScore + c3mrScore;
      data.add(new TopResult(t.getName(), t.getWitel(), totalScore));
    }
    Collections.sort(data, new SortTopByAch());
    return data;
  }  
  
  private List<TopResult> mapDataUbis(List<Result> salesData, 
    List<Result> ttrData,
    List<Result> gaulData,
    List<Result> c3mrData, 
    List<Ubis> ubis){
  
    List<TopResult> data = new ArrayList<>();

    for(Ubis u : ubis){
      Double salesScore = 0.0;
      Double gaulScore = 0.0;
      Double ttrScore = 0.0;
      Double c3mrScore = 0.0;
      for(Result s : salesData){
        if(s.getLocation().equals(u.getLocation())) salesScore = s.getAchievement() * this.salesBobot;
      }
      for(Result ttr : ttrData){
        if(ttr.getLocation().equals(u.getLocation())) ttrScore = ttr.getAchievement() * this.ttrBobot;
      }
      for(Result gaul : gaulData){
        if(gaul.getLocation().equals(u.getLocation())) gaulScore = gaul.getAchievement() * this.gaulBobot;
      }
      for(Result c3mr : c3mrData){
        if(c3mr.getLocation().equals(u.getLocation())) c3mrScore = c3mr.getAchievement() * this.c3mrBobot;
      }
      Double totalScore = salesScore + gaulScore + ttrScore + c3mrScore;
      data.add(new TopResult(u.getLocation(), u.getWitel(), totalScore));
    }
    Collections.sort(data, new SortTopByAch());
    return data;
  }

  private List<TopResult> getTop3(List<TopResult> raw){
    List<TopResult> data = new ArrayList<>();
    int i = 0;
    while(i < 3){
      data.add(raw.get(i));
      i++;
    }
    return data;
  }
}

// sort descending
class SortTopByAch implements Comparator<TopResult> { 
  public int compare(TopResult a, TopResult b) { 
      return b.getScore().intValue() - a.getScore().intValue(); 
  } 
} 