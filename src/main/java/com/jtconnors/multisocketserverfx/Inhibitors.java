package com.jtconnors.multisocketserverfx;

public class Inhibitors {

    int health;
    boolean isDestroyed;
    int x;
    int y;
    double startTime;

    public Inhibitors(String name){
        health = 500;
        isDestroyed = false;
        if (name.equals("topInhibitor")){
            x = 10;
            y = 20;
        } else if (name.equals("botInhibitor")){
            x = 10;
            y = 30;
        }
        startTime = System.nanoTime();
    }

    public void spawnMinions(Map[][] map){

    }


}
