package com.servlet.learning.obj;

public class Territory {

  private String mLocation;
  private int nId;
  private String mWitel;

  public Territory(String l, int i, String w) {
    setData(l, i, w);
  }

  public String getLocation() {
    return this.mLocation;
  }

  public int getId() {
    return this.nId;
  }

  public String getWitel() {
    return this.mWitel;
  }

  public void setData(String l, int i, String w) {
    this.mLocation = l;
    this.nId = i;
    this.mWitel = w;
  }
}