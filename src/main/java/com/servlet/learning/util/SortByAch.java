package com.servlet.learning.util;

import java.util.Comparator;

import com.servlet.learning.obj.Result;

public class SortByAch implements Comparator<Result> { 

    public int compare(Result a, Result b) { 
        return b.getAchievement().intValue() - a.getAchievement().intValue(); 
    } 
} 