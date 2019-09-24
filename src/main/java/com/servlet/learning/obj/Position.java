package com.servlet.learning.obj;

import com.servlet.learning.util.DBHelper;

public class Position {

  private String mIndikator;
  private Double nTarget;
  private Double nReal;
  private Double nScore;
  private int nRank;
  private int nType;

  public Position(String i, Double t, Double r, int n, int type) {
    this.mIndikator = i;
    this.nTarget = t;
    this.nReal = r;
    this.nRank = n;
    this.nType = type;
  }

  public String getIndikator() {
    return mIndikator;
  }

  public Double getTarget() {
    return nTarget;
  }

  public Double getReal() {
    return nReal;
  }

  public int getRank() {
    return nRank;
  }

  public Double getScore(){
    if(this.nType == DBHelper.REAL_COMMON) return (this.nReal/this.nTarget)*100;
    else return (this.nTarget/this.nReal)*100;
  } 
}