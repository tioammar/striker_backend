package com.servlet.learning.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.servlet.learning.DetailWrapper;
import com.servlet.learning.obj.Position;
import com.servlet.learning.obj.Result;
import com.servlet.learning.obj.Trend;
import com.servlet.learning.util.DBConnectNetezza;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

public class DetailService {

  private int nId;
  private int nBulan;

  ResultSet mResultSet;
  Statement mStatement;

  private static final Logger LOGGER = Logger.getLogger(SalesService.class.getName());

  public DetailService(int id, int bln){
    this.nId = id;
    this.nBulan = bln;
  }

  public DetailWrapper getTPTDetail(){
    DetailWrapper data = new DetailWrapper();
    String tpt = getSTOName();
    data.setData(DBHelper.GET_DATA_SUCESS, getSalesTPT(), 
      getTrend("ttr", tpt), 
      getTrend("gaul", tpt), 
      getTrend("c3mr", tpt), 
      tptPosition(tpt));
    return data;
  }

  public DetailWrapper getUbisDetail(){
    DetailWrapper data = new DetailWrapper();
    String ubis = getUbisName();
    data.setData(DBHelper.GET_DATA_SUCESS, 
      getSalesUbis(ubis), 
      getTrend("ttr", ubis), 
      getTrend("gaul", ubis), 
      getTrend("c3mr", ubis), 
      ubisPosition(ubis));
    return data;
  }

  private String currentMonth(int bln){
    return bln < 10 ? "0"+bln : ""+bln;
  }

  private String startMonth(int bln){
    int lbln = bln < 3 ? 1 : bln - 2;
    return lbln < 10 ? "0"+lbln : ""+lbln;
  }

  private List<Trend> getSalesUbis(String ubis){
    List<Trend> data = new ArrayList<>();
    Connection conn = new DBConnectNetezza().getConnection();
    String stoList = buildString(getSTO(new DBConnectSQL().getConnection(), ubis));
    String query = "SELECT nmonth, sum(ach) as ach"
    + " FROM ("
    + " select TO_CHAR(tgl,'MM') as nmonth, count(ncli) as ach from telkomCBD..demand_internet_new"
    + " where KAWASAN = 'DIVRE 7'"
    + " and ETAT = '5'"
    + " and STATUS = 'IHNETIZEN'"
    + " and STATUS_DEMAND IN ('SALES NETIZEN','MIGRASI 2P TO 2P NETIZEN','MIGRASI NETIZEN')"
    + " and TO_CHAR(tgl,'YYYYMM') >= '2019"+startMonth(nBulan)+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentMonth(nBulan)+"'"
    + " and sto in  ("+stoList+")"
    + " group by TO_CHAR(tgl,'MM')"
    + " UNION ALL"
    + " select TO_CHAR(tgl,'MM') as nmonth, count(ncli) as ach FROM telkomCBD..DEMAND_INDIHOME_NEW"  
    + " WHERE TO_CHAR(tgl,'YYYYMM') >= '2019"+startMonth(nBulan)+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentMonth(nBulan)+"'"
    + " AND ETAT = '5'"      
    + " AND STATUS_INDIHOME IN ('SALES_3P_BUNDLED','SALES_3P_UNBUNDLED','MIGRASI_1P_3P_UNBUNDLED','MIGRASI_2P_3P_UNBUNDLED','MIGRASI_1P_3P_BUNDLED','MIGRASI_2P_3P_BUNDLED')"
    + " AND sto in  ("+stoList+")"
    + " group by TO_CHAR(tgl,'MM'))x"
    + " group by nmonth"
    + " order by nmonth";
    LOGGER.info(query);

    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String month = mResultSet.getString("nmonth");
        Double ach = mResultSet.getDouble("ach");
        data.add(new Trend(normalizeMonth(month), ach));
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
    LOGGER.info("getting data done..");
    return data;
  }

  private List<Trend> getSalesTPT(){
    List<Trend> data = new ArrayList<>();
    Connection conn = new DBConnectNetezza().getConnection();
    String stoCode = getSTOcode();
    String query = "SELECT nmonth, sum(ach) as ach"
    + " FROM ("
    + " select TO_CHAR(tgl,'MM') as nmonth, count(ncli) as ach from telkomCBD..demand_internet_new"
    + " where KAWASAN = 'DIVRE 7'"
    + " and ETAT = '5'"
    + " and STATUS = 'IHNETIZEN'"
    + " and STATUS_DEMAND IN ('SALES NETIZEN','MIGRASI 2P TO 2P NETIZEN','MIGRASI NETIZEN')"
    + " and TO_CHAR(tgl,'YYYYMM') >= '2019"+startMonth(nBulan)+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentMonth(nBulan)+"'"
    + " and sto = '"+stoCode+"'"
    + " group by TO_CHAR(tgl,'MM')"
    + " UNION ALL"
    + " select TO_CHAR(tgl,'MM') as nmonth, count(ncli) as ach FROM telkomCBD..DEMAND_INDIHOME_NEW"  
    + " WHERE TO_CHAR(tgl,'YYYYMM') >= '2019"+startMonth(nBulan)+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentMonth(nBulan)+"'"
    + " AND ETAT = '5'"      
    + " AND STATUS_INDIHOME IN ('SALES_3P_BUNDLED','SALES_3P_UNBUNDLED','MIGRASI_1P_3P_UNBUNDLED','MIGRASI_2P_3P_UNBUNDLED','MIGRASI_1P_3P_BUNDLED','MIGRASI_2P_3P_BUNDLED')"
    + " AND sto = '"+stoCode+"'"
    + " group by TO_CHAR(tgl,'MM'))x"
    + " group by nmonth"
    + " order by nmonth";
    LOGGER.info(query);

    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String month = mResultSet.getString("nmonth");
        Double ach = mResultSet.getDouble("ach");
        data.add(new Trend(normalizeMonth(month), ach));
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
    LOGGER.info("getting data done..");
    return data;
  }

  private List<Trend> getTrend(String table, String name){
    List<Trend> data = new ArrayList<>();
    Connection conn = new DBConnectSQL().getConnection();

    String fmonth = currentMonth(nBulan-2);
    String smonth = currentMonth(nBulan-1);
    String month = currentMonth(nBulan);

    String query = "select real_"+fmonth+" as ach from real_"+table+" where location = '"+name+"'"
    + " union all" 
    + " select real_"+smonth+" as ach from real_"+table+" where location = '"+name+"'"
    + " union all"
    + " select real_"+month+" as ach from real_"+table+" where location = '"+name+"'";
    LOGGER.info(query);

    try {
      int i = 2;
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        int nmonth = this.nBulan - i;
        Double ach = mResultSet.getDouble("ach");
        data.add(new Trend(normalizeMonth(currentMonth(nmonth)), ach));
        i--;
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
    LOGGER.info("getting data done..");
    return data;
  }

  private String normalizeMonth(String m){
    switch(m){
      case "01":
        return "Jan";
      case "02":
        return "Feb";
      case "03":
        return "Mar";
      case "04":
        return "Apr";
      case "05":
        return "Mei";
      case "06":
        return "Jun";
      case "07":
        return "Jul";
      case "08":
        return "Agu";
      case "09":
        return "Sep";
      case "10":
        return "Okt";
      case "11":
        return "Nov";
      case "12":
        return "Des";
      default:
        return "Jan";
    }
  }

  private String getSTOcode(){
    String sto = "";
    Connection conn = new DBConnectSQL().getConnection();
    String query = "select sto from sto_profile where id = "+nId;
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        sto = mResultSet.getString("sto");
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

  private String getUbisName(){
    String ubis = "";
    Connection conn = new DBConnectSQL().getConnection();
    String query = "select datel from ubis where id = "+nId;
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        ubis = mResultSet.getString("datel");
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
    return ubis;
  }

  private String getSTOName(){
    String sto = "";
    Connection conn = new DBConnectSQL().getConnection();
    String query = "select sto_str from sto_profile where id = "+nId;
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        sto = mResultSet.getString("sto_str");
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

  private String buildString(List<String> stoList) {
    int size = stoList.size();
    int index = 0;
    String list = "";
    for (String sto : stoList){
      if(index == (size-1)) list += "'"+sto+"'";
      else list += "'"+sto+"',";
      index++;
    }
    return list;
  }

  private List<String> getSTO(Connection conn, String ubis){
    List<String> sto = new ArrayList<>();
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery("select distinct sto from sto_profile where datel = '"+ubis+"'");
      while(mResultSet.next()){
        sto.add(mResultSet.getString("sto"));
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

  private List<Position> ubisPosition(String ubis){
    List<Result> salesList = new SalesService(this.nBulan, DBHelper.GET_ALL_STO).getSalesUbisArray().getData();
    List<Result> ttrList = new TTRService(DBHelper.GET_ALL_STO, this.nBulan).getTTRubis().getData();
    List<Result> gaulList = new GaulService(DBHelper.GET_ALL_STO, this.nBulan).getGaulUbis().getData();
    List<Result> c3mrList = new CollectionsService(DBHelper.GET_ALL_STO, this.nBulan).getCollectionsUbis().getData();

    Position sales = getPosition(salesList, "Sales", ubis, DBHelper.REAL_COMMON);
    Position ttr = getPosition(ttrList, "TTR 3 Jam", ubis, DBHelper.REAL_COMMON);
    Position gaul = getPosition(gaulList, "Gangguan Ulang", ubis, DBHelper.REAL_REVERSE);
    Position c3mr = getPosition(c3mrList, "C3MR", ubis, DBHelper.REAL_COMMON);

    List<Position> data = new ArrayList<>();
    data.add(ttr);
    data.add(gaul);
    data.add(sales);
    data.add(c3mr);
    return data;
  }

  private String getSTOClass(String tpt){
    String kelas = "";
    Connection conn = new DBConnectSQL().getConnection();
    String query = "select kelas from sto_profile where sto_str = '"+tpt+"'";
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        kelas = mResultSet.getString("kelas");
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
    return kelas;
  }

  private List<Position> tptPosition(String tpt){
    String kelas = getSTOClass(tpt);
    List<Result> salesList = new SalesService(this.nBulan, kelas).getSalesTPTArray().getData();
    List<Result> ttrList = new TTRService(kelas, this.nBulan).getTTRtpt().getData();
    List<Result> gaulList = new GaulService(kelas, this.nBulan).getGaulTPT().getData();
    List<Result> c3mrList = new CollectionsService(kelas, this.nBulan).getCollectionsTPT().getData();

    Position sales = getPosition(salesList, "Sales", tpt, DBHelper.REAL_COMMON);
    Position ttr = getPosition(ttrList, "TTR 3 Jam", tpt, DBHelper.REAL_COMMON);
    Position gaul = getPosition(gaulList, "Gangguan Ulang", tpt, DBHelper.REAL_REVERSE);
    Position c3mr = getPosition(c3mrList, "C3MR", tpt, DBHelper.REAL_COMMON);

    List<Position> data = new ArrayList<>();
    data.add(ttr);
    data.add(gaul);
    data.add(sales);
    data.add(c3mr);
    return data;
  }

  private Position getPosition(List<Result> list, String indicator, String location, int type){
    Double target = 0.0;
    Double real = 0.0;
    int rank = 1;
    int i = 1;
    for(Result r : list){
      if(r.getLocation().equals(location)){
        target = r.getCurrentTarget();
        real = r.getCurrentMonth();
        rank = i;
      }
      i++;
    }
    return new Position(indicator, target, real, rank, type);
  }
}