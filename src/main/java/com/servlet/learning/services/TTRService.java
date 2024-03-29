package com.servlet.learning.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.servlet.learning.ResultWrapper;
import com.servlet.learning.obj.*;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

import com.servlet.learning.util.SortByAch;

public class TTRService {

  private String mClass;
  private int nBulan;

  Statement mStatement = null;
  ResultSet mResultSet = null;

  private static final Logger LOGGER = Logger.getLogger(TTRService.class.getName());

  public TTRService(String cls, int bln){
    this.mClass = cls;
    this.nBulan = bln;
  }

  public ResultWrapper getTTRtpt(){
    ResultWrapper data = new ResultWrapper();
    
    List<Result> sales = getSTOttr();
    String status = sales != null ? DBHelper.GET_DATA_SUCESS : DBHelper.GET_DATA_FAILED_SQL_ERROR;

    LOGGER.info("sort data by achievement...");
    Collections.sort(sales, new SortByAch());

    LOGGER.info("all process done...");
    data.setData(status, sales);
    return data;
  }

  private String currentMonth(int bln){
    return this.nBulan < 10 ? "0"+this.nBulan : ""+this.nBulan;
  }

  private String lastMonth(int bln){
    int lbln = this.nBulan - 1;
    return lbln < 10 ? "0"+lbln : ""+lbln;
  }

  private List<Result> getSTOttr(){
    List<Result> data = new ArrayList<>();
    // setting up range
    String currentmonth = currentMonth(nBulan);
    String lastmonth = lastMonth(nBulan);

    List<Result> ttr = new ArrayList<>();
    List<STO> sto = getSTObyClass();
    String stoList = buildString(sto);
    Connection conn = new DBConnectSQL().getConnection();
    String query = "Select location, real_"+currentmonth+" as currentmonth, real_"+lastmonth+" as lastmonth from real_ttr where location in ("+stoList+")";
    
    try {
      // LOGGER.info(query);
      LOGGER.info("getting data...");
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String name = mResultSet.getString("location");
        Double current = mResultSet.getDouble("currentmonth");
        Double last = mResultSet.getDouble("lastmonth");
        ttr.add(new Result(name, "", current, 0.0, last));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mResultSet.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }

    LOGGER.info("mapping to tpt...");
    // building data
    for (STO n : sto) {
      Double current = 0.0;
      Double last = 0.0;
      for (Result t : ttr) {
        if(t.getLocation().equals(n.getName())){
          last = t.getLastMonth();
          current = t.getCurrentMonth();
        }
      }
      data.add(new Result(n.getName(), n.getWitel(), current, n.getTarget(), last));
    }
    return data;
  }

  private String buildString(List<STO> stoList) {
    int size = stoList.size();
    int index = 0;
    String list = "";
    for (STO sto : stoList){
      if(index == (size-1)) list += "'"+sto.getName()+"'";
      else list += "'"+sto.getName()+"',";
      index++;
    }
    return list;
  }

  private List<STO> getSTObyClass() {
    // String stoList = "";
    List<STO> stoList = new ArrayList<>();
    DBConnectSQL db = new DBConnectSQL();
    Connection conn = db.getConnection();
    String query = this.mClass.equals(DBHelper.GET_ALL_STO) ? 
    "select distinct a.sto_str, a.sto, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a inner join tar_ttr b on a.sto_str=b.location where datel != 'N'" : 
    "select distinct a.sto_str, a.sto, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a inner join tar_ttr b on a.sto_str=b.location where a.kelas = '"+this.mClass+"'";
    // LOGGER.info(query);
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String name = mResultSet.getString("sto_str");
        String code = mResultSet.getString("sto");
        String witel = mResultSet.getString("witel");
        Double target = mResultSet.getDouble("target");
        stoList.add(new STO(name, code, witel, target));
      }
      LOGGER.info("get STO done...");
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      try {
        // if(conn != null) conn.close();
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mStatement.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }
    return stoList;
  }

  private String buildStringUbis(List<Ubis> list) {
    int size = list.size();
    int index = 0;
    String data = "";
    for (Ubis sto : list){
      data += "'"+sto.getLocation()+"',";
      if(index == (size-1)) data += "'"+sto.getLocation()+"'";
      index++;
    }
    return data;
  }

  public ResultWrapper getTTRubis(){
    // ResultWrapper data = new ResultWrapper();
    // Connection conn = new DBConnectSQL().getConnection();

    // List<Result> ttrData = new ArrayList<>();
    // List<Ubis> ubis = getUbis(conn);
    // List<Result> ttr = getSTOttr();

    // List<Double> sumCurrent = new ArrayList<>();
    // List<Double> sumLast = new ArrayList<>();

    // LOGGER.info("mapping to Ubis/Datel");
    // for (Ubis u : ubis) {
    //   String name = u.getLocation();
    //   String witel = u.getWitel();
    //   Double target = u.getTarget();
    //   List<String> sto = getSTO(conn, u.getLocation());
    //   for (String s : sto) {
    //     for (Result t : ttr) {
    //       if(t.getLocation().equals(s)){
    //         sumCurrent.add(t.getCurrentMonth());
    //         sumLast.add(t.getLastMonth());
    //       }
    //     }
    //   }
    //   ttrData.add(new Result(name, witel, average(sumCurrent), target, average(sumLast)));
    //   sumCurrent.clear();
    //   sumLast.clear();
    // }
    // LOGGER.info("sorting by achievement...");
    // Collections.sort(ttrData, new SortByAch());

    // LOGGER.info("Ubis/Datel process done...");
    // data.setData(DBHelper.GET_DATA_SUCESS, ttrData);
    // return data;
    String currentmonth = currentMonth(nBulan);
    String lastmonth = lastMonth(nBulan);
    ResultWrapper wrapper = new ResultWrapper();
    Connection conn = new DBConnectSQL().getConnection();

    List<Result> collectionsData = new ArrayList<>();
    List<Result> data = new ArrayList<>();
    List<Ubis> ubis = getUbis(conn);
    String ubisList = buildStringUbis(ubis);
    String query = "Select location, real_"+currentmonth+" as currentmonth, real_"+lastmonth+" as lastmonth from real_ttr where location in ("+ubisList+")";
    
    try {
      // LOGGER.info(query);
      LOGGER.info("getting data...");
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String name = mResultSet.getString("location");
        Double current = mResultSet.getDouble("currentmonth");
        Double last = mResultSet.getDouble("lastmonth");
        collectionsData.add(new Result(name, "", current, 0.0, last));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mResultSet.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }

    LOGGER.info("mapping to tpt...");
    // building data
    for (Ubis n : ubis) {
      Double current = 0.0;
      Double last = 0.0;
      for (Result c : collectionsData) {
        if(c.getLocation().equals(n.getLocation())){
          last = c.getLastMonth();
          current = c.getCurrentMonth();
        }
      }
      data.add(new Result(n.getLocation(), n.getWitel(), current, n.getTarget(), last));
    }
    wrapper.setData(DBHelper.GET_DATA_SUCESS, data);
    return wrapper;
  }

  private Double average(List<Double> data){
    Double sum = 0.0;
    if(!data.isEmpty()) {
      for (Double d : data) {
          sum += d;
      }
      return sum.doubleValue() / data.size();
    }
    return sum;
  }
  
  private List<Ubis> getUbis(Connection conn){
    List<Ubis> ubis = new ArrayList<>();
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery("select distinct a.datel, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a left join tar_ttr b on a.datel=b.location where datel != 'N'");
      while(mResultSet.next()){
        String location = mResultSet.getString("datel");
        String witel = mResultSet.getString("witel");
        Double target = mResultSet.getDouble("target");
        ubis.add(new Ubis(location, witel, target));
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

  private List<String> getSTO(Connection conn, String ubis){
    List<String> sto = new ArrayList<>();
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery("select sto_str from sto_profile where datel = '"+ubis+"'");
      while(mResultSet.next()){
        sto.add(mResultSet.getString("sto_str"));
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mResultSet.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }
    return sto;
  }
}