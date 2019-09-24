package com.servlet.learning.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.servlet.learning.TerritoryWrapper;
import com.servlet.learning.obj.Territory;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

public class TerritoryService {

  private String mClass;

  ResultSet mResultSet;
  Statement mStatement;

  public TerritoryService(String cls) {
    this.mClass = cls;
  }

  public TerritoryWrapper getTerritory(){
    TerritoryWrapper data = new TerritoryWrapper();
    List<Territory> territory = new ArrayList<>();
    Connection conn = new DBConnectSQL().getConnection();
    String query = this.mClass.equals(DBHelper.GET_ALL_STO) ? 
    "select distinct b.id, a.datel as location, a.witel from sto_profile a left join ubis b on a.datel = b.datel where a.datel != 'N'" : 
    "select id, sto_str as location , witel from sto_profile where kelas = '"+this.mClass+"'";

    try {
      mStatement = conn.createStatement();
      mResultSet = mStatement.executeQuery(query);
      while(mResultSet.next()){
        String location = mResultSet.getString("location");
        String witel = mResultSet.getString("witel");
        int id = mResultSet.getInt("id");
        territory.add(new Territory(location, id, witel));
      } 
    } catch (SQLException e){
      e.printStackTrace();
    } finally {
      try {
        if(mStatement != null) mStatement.close();
        if(mResultSet != null) mResultSet.close();
      } catch(SQLException e){
        e.printStackTrace();
      }
    }
    String status = territory != null ? DBHelper.GET_DATA_SUCESS : DBHelper.GET_DATA_FAILED_SQL_ERROR;
    data.setData(status, territory);
    return data;
  }
}