package com.jtconnors.multisocketserverfx;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Entities {

    ArrayList<Image> images = new ArrayList<>();
    String type;
    int level;
    int health;
    int maxHealth;
    int healthIncrease;
    int speed;
    int xLoc;
    int yLoc;

    ProgressBar healthBar = new ProgressBar(1);

    Weapon primary;
    Weapon secondary;

    public Entities(int level, int range, int health, int healthIncrease, int speed, Weapon primary, Weapon secondary, int xLoc, int yLoc){
        this.level = level;
        this.health = health;
        this.maxHealth = health;
        this.healthIncrease = healthIncrease;
        this.speed = speed;
        this.primary = primary;
        this.secondary = secondary;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    public void changeHealth(int amount){
        health += amount;
        double progress = health/maxHealth;
        healthBar.setProgress(progress);
    }

    public void changeLoc(int x, int y){

    }

    public void changeWeapon(Weapon primary, Weapon secondary){
        this.primary = primary;
        this.secondary = secondary;
    }
}
