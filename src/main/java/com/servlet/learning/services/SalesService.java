package com.servlet.learning.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.servlet.learning.obj.STO;
import com.servlet.learning.obj.Ubis;
import com.servlet.learning.ResultWrapper;
import com.servlet.learning.obj.Result;
import com.servlet.learning.util.DBConnectNetezza;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

public class SalesService {

  private int nBulan;
  private String mClass;

  private static final Logger LOGGER = Logger.getLogger(SalesService.class.getName());

  Statement mStatement = null;
  ResultSet mResultSet = null;

  public SalesService(int bln, String cls){
    this.nBulan = bln;
    this.mClass = cls;
  }

  public ResultWrapper getSalesTPTArray() {
    ResultWrapper data = new ResultWrapper();
    
    List<Result> sales = getSTOSales();
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

  private List<Result> getSTOSales(){
    List<Result> data = new ArrayList<>();
    // setting up range
    String currentmonth = currentMonth(nBulan);
    String lastmonth = lastMonth(nBulan);

    List<Result> sales = new ArrayList<>();
    List<STO> sto = getSTObyClass();
    String stoList = buildString(sto);
    // Connection conn = new DBConnectNetezza().getConnection();
    Connection conn = new DBConnectSQL().getConnection();

    String querySQL = "SELECT sto, sum(currentmonth) as currentmonth, sum(lastmonth) as lastmonth"
    + " FROM ("
    + " select sto, sum(case when DATE_FORMAT(tgl,'%Y%m') = '2019"+currentmonth+"' then 1 else 0 end) as currentmonth, sum(case when DATE_FORMAT(tgl,'%Y%m') = '2019"+lastmonth+"' then 1 else 0 end) as lastmonth from demand_internet_new"
    + " where KAWASAN = 'DIVRE 7'"
    + " and sto in ("+stoList+")"
    + " and ETAT = '5'"
    + " and STATUS = 'IHNETIZEN'"
    + " and STATUS_DEMAND IN ('SALES NETIZEN','MIGRASI 2P TO 2P NETIZEN','MIGRASI NETIZEN')"
    + " and DATE_FORMAT(tgl,'%Y%m') >= '2019"+lastmonth+"' and DATE_FORMAT(tgl,'%Y%m') <= '2019"+currentmonth+"'"
    + " group by sto"
    + " UNION ALL"
    + " select sto, sum(case when DATE_FORMAT(tgl,'%Y%m') = '2019"+currentmonth+"' then 1 else 0 end) as currentmonth, sum(case when DATE_FORMAT(tgl,'%Y%m') = '2019"+lastmonth+"' then 1 else 0 end) as lastmonth FROM DEMAND_INDIHOME_NEW"
    + " WHERE DATE_FORMAT(tgl,'%Y%m') >= '2019"+lastmonth+"' and DATE_FORMAT(tgl,'%Y%m') <= '2019"+currentmonth+"'"
    + " and sto in ("+stoList+")"
    + " AND ETAT = '5'"
    + " AND STATUS_INDIHOME IN ('SALES_3P_BUNDLED','SALES_3P_UNBUNDLED','MIGRASI_1P_3P_UNBUNDLED','MIGRASI_2P_3P_UNBUNDLED','MIGRASI_1P_3P_BUNDLED','MIGRASI_2P_3P_BUNDLED')"
    + " AND KAWASAN = 'DIVRE 7'"
    + " group by sto)x"
    + " group by sto"
    + " order by sto";

    String query = "SELECT sto, sum(currentmonth) as currentmonth, sum(lastmonth) as lastmonth"
    + " FROM ("
    + " select sto, sum(case when TO_CHAR(tgl,'YYYYMM') = '2019"+currentmonth+"' then 1 else 0 end) as currentmonth, sum(case when TO_CHAR(tgl,'YYYYMM') = '2019"+lastmonth+"' then 1 else 0 end) as lastmonth from telkomCBD..demand_internet_new"
    + " where KAWASAN = 'DIVRE 7'"
    + " and sto in ("+stoList+")"
    + " and ETAT = '5'"
    + " and STATUS = 'IHNETIZEN'"
    + " and STATUS_DEMAND IN ('SALES NETIZEN','MIGRASI 2P TO 2P NETIZEN','MIGRASI NETIZEN')"
    + " and TO_CHAR(tgl,'YYYYMM') >= '2019"+lastmonth+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentmonth+"'"
    + " group by sto"
    + " UNION ALL"
    + " select sto, sum(case when TO_CHAR(tgl,'YYYYMM') = '2019"+currentmonth+"' then 1 else 0 end) as currentmonth, sum(case when TO_CHAR(tgl,'YYYYMM') = '2019"+lastmonth+"' then 1 else 0 end) as lastmonth FROM telkomCBD..DEMAND_INDIHOME_NEW"
    + " WHERE TO_CHAR(tgl,'YYYYMM') >= '2019"+lastmonth+"' and TO_CHAR(tgl,'YYYYMM') <= '2019"+currentmonth+"'"
    + " and sto in ("+stoList+")"
    + " AND ETAT = '5'"
    + " AND STATUS_INDIHOME IN ('SALES_3P_BUNDLED','SALES_3P_UNBUNDLED','MIGRASI_1P_3P_UNBUNDLED','MIGRASI_2P_3P_UNBUNDLED','MIGRASI_1P_3P_BUNDLED','MIGRASI_2P_3P_BUNDLED')"
    + " AND KAWASAN = 'DIVRE 7'"
    + " group by sto)x"
    + " group by sto"
    + " order by sto";
    
    try {
      // LOGGER.info(query);
      LOGGER.info("getting data...");
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(querySQL);
      while(mResultSet.next()){
        String name = mResultSet.getString("sto");
        Double current = mResultSet.getDouble("currentmonth");
        Double last = mResultSet.getDouble("lastmonth");
        sales.add(new Result(name, "", current, 0.0, last));
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
      for (Result m : sales) {
        if(m.getLocation().equals(n.getCodeName())){
          last = m.getLastMonth();
          current = m.getCurrentMonth();
        }
      }
      data.add(new Result(n.getName(), n.getWitel(), current, n.getTarget(), last));
    }

    LOGGER.info("all process done...");
    return data;
  }

  private String buildString(List<STO> stoList) {
    int size = stoList.size();
    int index = 0;
    String list = "";
    for (STO sto : stoList){
      if(index == (size-1)) list += "'"+sto.getCodeName()+"'";
      else list += "'"+sto.getCodeName()+"',";
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
        +" as target from sto_profile a left join tar_sales b on a.sto_str=b.location where datel != 'N'" : 
    "select distinct a.sto_str, a.sto, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a left join tar_sales b on a.sto_str=b.location where a.kelas = '"+this.mClass+"'";
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

  public ResultWrapper getSalesUbisArray(){
    Double current = 0.0;
    Double last = 0.0;

    ResultWrapper data = new ResultWrapper();
    Connection conn = new DBConnectSQL().getConnection();

    List<Result> salesData = new ArrayList<>();
    List<Ubis> ubis = getUbis(conn);
    List<Result> sales = getSTOSales();

    LOGGER.info("mapping to Ubis/Datel");
    for (Ubis u : ubis) {
      String name = u.getLocation();
      String witel = u.getWitel();
      Double target = u.getTarget();
      List<String> sto = getSTO(conn, u.getLocation());
      for (String s : sto) {
        for (Result sls : sales) {
          if(sls.getLocation().equals(s)){
            current += sls.getCurrentMonth();
            last += sls.getLastMonth();
          }
        }
      }
      salesData.add(new Result(name, witel, current, target, last));
      current = 0.0;
      last = 0.0;
    }
    LOGGER.info("sorting by achievement...");
    Collections.sort(salesData, new SortByAch());

    LOGGER.info("Ubis/Datel process done...");
    data.setData(DBHelper.GET_DATA_SUCESS, salesData);
    return data;
  }
  
  private List<Ubis> getUbis(Connection conn){
    List<Ubis> ubis = new ArrayList<>();
    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery("select distinct a.datel, a.witel, b.tar_"+currentMonth(nBulan)
        +" as target from sto_profile a left join tar_sales b on a.datel=b.location where datel != 'N'");
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
class SortByAch implements Comparator<Result> { 
    public int compare(Result a, Result b) { 
        return b.getAchievement().intValue() - a.getAchievement().intValue(); 
    } 
} 