package com.servlet.learning.obj;

public class Trend {

  private String mMonth;
  private Double nAch;

  
  public String getMonth() {
    return mMonth;
  }

  public Double getAch() {
    return nAch;
  }

  public Trend(String m, Double a) {
    this.mMonth = m;
    this.nAch = a;
  }
}