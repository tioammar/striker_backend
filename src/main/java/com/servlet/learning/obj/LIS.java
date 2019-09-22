package com.servlet.learning.obj;

/**
 * LISdetail
 */
public class LIS {

  private String mNcli;
  private String mWitel;
  private String mDatel;
  private String mSTO;

  public LIS(String ncli, String witel, String datel, String sto){
    mNcli = ncli;
    mWitel = witel;
    mDatel = datel;
    mSTO = sto;
  }

  public String getNcli(){
    return mNcli;
  }

  public String getWitel(){
    return mWitel;
  }

  public String getDatel(){
    return mDatel;
  }

  public String getSto(){
    return mSTO;
  }
}