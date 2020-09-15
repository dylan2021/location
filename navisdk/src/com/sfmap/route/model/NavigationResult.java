package com.sfmap.route.model;

import java.io.Serializable;

public class NavigationResult implements Serializable {

    public NavigationPath[] paths;
    public int pathNum;
    public int dataLength;
    public int startX;
    public int startY;
    public int endX;
    public int endY;
}