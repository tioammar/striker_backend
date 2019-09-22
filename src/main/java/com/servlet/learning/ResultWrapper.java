package com.servlet.learning;

import java.util.List;

import com.servlet.learning.obj.Result;

public class ResultWrapper {

  private String status;
  private List<Result> data;

  public void setData(String status, List<Result> data){
    this.status = status;
    this.data = data;
  }

  public List<Result> getData(){
    return this.data;
  }

  public String getStatus(){
    return this.status;
  }
}