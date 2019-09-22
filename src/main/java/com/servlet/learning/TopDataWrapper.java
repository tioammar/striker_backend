package com.servlet.learning;

import java.util.List;

import com.servlet.learning.obj.TopResult;

public class TopDataWrapper {

  private String mStatus;
  private List<TopResult> nClassA;
  private List<TopResult> nClassB;
  private List<TopResult> nClassC;
  private List<TopResult> nUbis;

  public TopDataWrapper(String status, List<TopResult> a, List<TopResult> b, List<TopResult> c, List<TopResult> ubis){
    this.mStatus = status;
    this.nClassA = a;
    this.nClassB = b;
    this.nClassC = c;
    this.nUbis = ubis;
  }

  public List<TopResult> getClassA(){
    return this.nClassA;
  }
  
  public List<TopResult> getClassB(){
    return this.nClassB;
  }

  public List<TopResult> getClassC(){
    return this.nClassC;
  }

  public List<TopResult> getUbis(){
    return this.nUbis;
  }

  public String getStatus(){
    return this.mStatus;
  }
}