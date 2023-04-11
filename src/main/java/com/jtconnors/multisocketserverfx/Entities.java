package com.jtconnors.multisocketserverfx;

import javafx.scene.image.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Entities {

    ArrayList<Image> images = new ArrayList<>();
    String type;
    int level;
    int range;
    int health;
    int healthIncrease;
    int speed;

    Weapon primary;
    Weapon secondary;

    public Entities(int level, int range, int health, int healthIncrease, int speed, Weapon primary, Weapon secondary){

    }
}
