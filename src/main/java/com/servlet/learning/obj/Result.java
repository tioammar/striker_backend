package com.servlet.learning.obj;

/**
 * Sales
 */
public class Result {

  private String mLocation;
  private String mWitel;
  private Double nCurrentMonth;
  private Double nCurrentTarget;
  private Double nLastMonth;
  private Double nAchievement;

  public Result(){
  }

  public void setData(String location, String witel, Double current, Double target, Double last){
    this.mLocation = location;
    this.mWitel = witel;
    this.nCurrentMonth = current;
    this.nCurrentTarget = target;
    this.nLastMonth = last;
    if(target == 0) this.nAchievement = 100.0;
    else this.nAchievement = (this.nCurrentMonth/this.nCurrentTarget) * 100;
  }

  public Result(String location, String witel, Double current, Double target, Double last) {
    this.setData(location, witel, current, target, last);
  }

  public String getLocation(){
    return this.mLocation;
  }

  public String getWitel(){
    return this.mWitel;
  }

  public Double getCurrentMonth() {
    return this.nCurrentMonth;
  }

  public Double getCurrentTarget() {
    return this.nCurrentTarget;
  }

  public Double getLastMonth() {
    return this.nLastMonth;
  }

  public Double getAchievement(){
    return this.nAchievement;
  }
}