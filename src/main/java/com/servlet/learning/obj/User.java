package com.servlet.learning.obj;

public class User {

  private String mName;
  private int nLevel;
  private int nTerritory;

  public User(String name, int level, int territory){
    this.mName = name;
    this.nLevel = level;
    this.nTerritory = territory;
  }

  public String getName() {
    return mName;
  }

  public int getLevel() {
    return nLevel;
  }

  public int getTerritory() {
    return nTerritory;
  }

}