package com.servlet.learning.obj;

public class Ubis {

  private String mLocation;
  private String mWitel;
  private Double nTarget;

  public void setData(String location, String witel, Double target){
    this.mLocation = location;
    this.mWitel = witel;
    this.nTarget = target;
  }

  public Ubis(String location, String witel, Double target){
    this.setData(location, witel, target);
  }

  public String getLocation(){
    return this.mLocation;
  }

  public String getWitel(){
    return this.mWitel;
  }

  public Double getTarget(){
    return this.nTarget;
  }
}