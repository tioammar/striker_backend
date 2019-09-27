package com.servlet.learning;

import java.util.List;

import com.servlet.learning.obj.TopResult;

public class SimpleTopDataWrapper {

  private String mStatus;
  private List<TopResult> nData;

  public SimpleTopDataWrapper(String status, List<TopResult> data){
    this.mStatus = status;
    this.nData = data;
  }

  public List<TopResult> getData(){
    return this.nData;
  }

  public String getStatus(){
    return this.mStatus;
  }
}