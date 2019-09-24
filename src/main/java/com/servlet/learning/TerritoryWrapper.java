package com.servlet.learning;

import java.util.List;

import com.servlet.learning.obj.Territory;

public class TerritoryWrapper {

  private String mStatus;
  private List<Territory> nData;

  public void setData(String s, List<Territory> n) {
    this.mStatus = s; 
    this.nData = n;
  }

  public String getStatus() {
    return this.mStatus;
  }

  public List<Territory> getData() {
    return this.nData;
  }
}