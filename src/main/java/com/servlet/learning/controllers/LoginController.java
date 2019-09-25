package com.servlet.learning.controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.servlet.learning.LoginWrapper;
import com.servlet.learning.obj.User;
import com.servlet.learning.util.DBConnectSQL;
import com.servlet.learning.util.DBHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  @RequestMapping(value = "/login" , method = RequestMethod.GET)
  public LoginWrapper login(@RequestParam String email, @RequestParam String password){
    return doLogin(email, password);
  }

  private LoginWrapper doLogin(String email, String password){
    LoginWrapper data = new LoginWrapper();
    String query = "select * from users where email = '"+email+"'";
    Connection conn = new DBConnectSQL().getConnection();
    try {
      Statement statement = conn.createStatement();
      ResultSet resultSet = statement.executeQuery(query);
      while(resultSet.next()){
        if(resultSet.getString("password").equals(password)) {
          String name = resultSet.getString("name");
          int level = resultSet.getInt("level");
          int territory = resultSet.getInt("territory");
          data.setData(DBHelper.GET_DATA_SUCESS, new User(name, level, territory));
        } else data.setData(DBHelper.GET_DATA_FAILED_NOT_EXIST, null);
      }  
    } catch (SQLException e){
      e.printStackTrace();
    }
    return data; 
  }
}