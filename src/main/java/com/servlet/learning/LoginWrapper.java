package com.servlet.learning;

import com.servlet.learning.obj.User;

public class LoginWrapper {

  private String mStatus;
  private User mUser;

  public String getStatus(){
    return mStatus;
  }

  public User getUser(){
    return mUser;
  }

  public void setData(String status, User user){
    this.mStatus = status;
    this.mUser = user;
  }
}