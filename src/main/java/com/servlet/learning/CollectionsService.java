package com.servlet.learning;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.servlet.learning.obj.*;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;;

public class CollectionsService {

  private String mClass;
  private int nBulan;

  Statement mStatement = null;
  ResultSet mResultSet = null;

  private static final Logger LOGGER = Logger.getLogger(Collections.class.getName());

  public CollectionsService(String cls, int bln){
    this.mClass = cls;
    this.nBulan = bln;
  }

  public ResultWrapper getCollectionsTPT(){
    ResultWrapper data = new ResultWrapper();
    
    List<Result> collections = getSTOcollections();
    String status = collections != null ? DBHelper.GET_DATA_SUCESS : DBHelper.GET_DATA_FAILED_SQL_ERROR;

    LOGGER.info("sort data by achievement...");
    Collections.sort(collections, new SortByAch());

    LOGGER.info("all process done...");
    data.setData(status, collections);
    return data;
  }

  private String currentMonth(int bln){
    return this.nBulan < 10 ? "0"+this.nBulan : ""+this.nBulan;
  }

  private String lastMonth(int bln){
    int lbln = this.nBulan - 1;
    return lbln < 10 ? "0"+lbln : ""+lbln;
  }

  private List<Result> getSTOcollections(){
    List<Result> data = new ArrayList<>();
    // setting up range
    String currentmonth = currentMonth(nBulan);
    String lastmonth = lastMonth(nBulan);

    List<Result> collections = new ArrayList<>();
    List<STO> sto = getSTObyClass();
    String stoList = buildString(sto);
    Connection conn = new DBConnectSQL().getConnection();
    String query = "Select location, real_"+currentmonth+" as currentmonth, real_"+lastmonth+" as lastmonth from real_c3mr where location in ("+stoList+")";
    
    try {
      // LOGGER.info(query);
      LOGGER.info("getting data...");
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String name = mResultSet.getString("location");
        Double current = mResultSet.getDouble("currentmonth");
        Double last = mResultSet.getDouble("lastmonth");
        collections.add(new Result(name, "", current, 0.0, last));
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
      for (Result c : collections) {
        if(c.getLocation().equals(n.getName())){
          last = c.getLastMonth();
          current = c.getCurrentMonth();
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
      list += "'"+sto.getName()+"',";
      if(index == (size-1)) list += "'"+sto.getName()+"'";
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
        +" as target from sto_profile a inner join tar_c3mr b on a.sto_str=b.location where datel != 'N'" : 
    "select distinct a.sto_str, a.sto, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a inner join tar_c3mr b on a.sto_str=b.location where a.kelas = '"+this.mClass+"'";
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
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mStatement.close();
      } catch (SQLException e){
        e.printStackTrace();
      }
    }
    return stoList;
  }
  
  public ResultWrapper getCollectionsUbis(){
    ResultWrapper data = new ResultWrapper();
    Connection conn = new DBConnectSQL().getConnection();

    List<Result> collectionsData = new ArrayList<>();
    List<Ubis> ubis = getUbis(conn);
    List<Result> collections = getSTOcollections();

    List<Double> sumCurrent = new ArrayList<>();
    List<Double> sumLast = new ArrayList<>();

    LOGGER.info("mapping to Ubis/Datel");
    for (Ubis u : ubis) {
      String name = u.getLocation();
      String witel = u.getWitel();
      Double target = u.getTarget();
      List<String> sto = getSTO(conn, u.getLocation());
      for (String s : sto) {
        for (Result c : collections){
          if(c.getLocation().equals(s)){
            sumCurrent.add(c.getCurrentMonth());
            sumLast.add(c.getLastMonth());
          }
        }
      }
      collectionsData.add(new Result(name, witel, average(sumCurrent), target, average(sumLast)));
      sumCurrent.clear();
      sumLast.clear();
    }
    LOGGER.info("sorting by achievement...");
    Collections.sort(collectionsData, new SortByAch());

    LOGGER.info("Ubis/Datel process done...");
    data.setData(DBHelper.GET_DATA_SUCESS, collectionsData);
    return data;
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
        +" as target from sto_profile a inner join tar_c3mr b on a.datel=b.location where datel != 'N'");
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

// sort descending
class SortByAch implements Comparator<Result> 
{ 
    public int compare(Result a, Result b) { 
        return b.getAchievement().intValue() - a.getAchievement().intValue(); 
    } 
} 