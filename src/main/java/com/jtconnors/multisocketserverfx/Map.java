package com.jtconnors.multisocketserverfx;

import javafx.scene.control.Button;

public class Map {
    boolean isWall = false;
    int x;
    int y;
    int Orignum;
    int newNum;

    public Map(){}

    
    public Map(int x, int y, int num, boolean isWall){
        this.x = x;
        this.y = y;
        this.Orignum = num;
        this.newNum = Orignum;
        this.isWall = isWall;
    }

    public void changeNum(int num){
        this.newNum = num;
    }
}
