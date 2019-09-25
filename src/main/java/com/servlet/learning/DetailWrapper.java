package com.servlet.learning;

import java.util.List;

import com.servlet.learning.obj.Position;
import com.servlet.learning.obj.Trend;

public class DetailWrapper {

  private String mStatus;
  private String mLocation;
  private String mWitel;
  private List<Trend> salesTrend;
  private List<Trend> ttrTrend;
  private List<Trend> gaulTrend;
  private List<Trend> c3mrTrend;
  private List<Position> currentPosition;
  
  public String getStatus() {
    return mStatus;
  }

  public String getLocation(){
    return mLocation;
  }

  public String getWitel(){
    return mWitel;
  }

  public List<Trend> getSalesTrend() {
    return salesTrend;
  }

  public List<Trend> getTtrTrend() {
    return ttrTrend;
  }

  public List<Trend> getGaulTrend() {
    return gaulTrend;
  }

  public List<Trend> getC3mrTrend() {
    return c3mrTrend;
  }

  public List<Position> getCurrentPosition() {
    return currentPosition;
  }

  public void setData(String status, String location, String witel, List<Trend> salesTrend, List<Trend> ttrTrend, List<Trend> gaulTrend,
      List<Trend> c3mrTrend, List<Position> currentPosition) {
    this.mStatus = status;
    this.salesTrend = salesTrend;
    this.ttrTrend = ttrTrend;
    this.gaulTrend = gaulTrend;
    this.c3mrTrend = c3mrTrend;
    this.currentPosition = currentPosition;
    this.mLocation = location;
    this.mWitel = witel;
  }
}