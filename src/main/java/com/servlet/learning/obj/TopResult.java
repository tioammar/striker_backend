package com.servlet.learning.obj;

public class TopResult {

  private String mLocation;
  private String mWitel;
  private Double nScore;

  public TopResult(String loc, String witel, Double score){
    this.mLocation = loc;
    this.mWitel = witel;
    this.nScore = score;
  }

  public String getLocation(){
    return this.mLocation;
  }

  public String getWitel(){
    return this.mWitel;
  }

  public Double getScore(){
    return this.nScore;
  }
}