package com.servlet.learning.obj;

public class STO {

  private String mName;
  private String mCodeName;
  private String mWitel;
  private Double nTarget;

  public STO(){}

  public STO(String name, String codeName, String witel, Double target){
    this.setData(name, codeName, witel, target);
  }

  public void setData(String name, String codeName, String witel, Double target){
    this.mWitel = witel;
    this.mName = name;
    this.mCodeName = codeName;
    this.nTarget = target;
  }

  public String getName(){
    return this.mName;
  }

  public String getCodeName(){
    return this.mCodeName;
  }

  public String getWitel(){
    return this.mWitel;
  }

  public Double getTarget(){
    return this.nTarget;
  }
}