package com.jtconnors.multisocketserverfx;

import javafx.scene.image.Image;

public class Weapon {

    String weaponName;
    String weaponType;
    int range;
    int damage;
    int ammo;
    int maxAmmo;
    double speed;

    public Weapon(){
    }

    public Weapon(String weaponName, String weaponType, int range, int damage, int maxAmmo, double speed){
        this.weaponName = weaponName;
        this.weaponType = weaponType;
        this.range = range;
        this.damage = damage;
        this.ammo = maxAmmo;
        this.maxAmmo = maxAmmo;
        this.speed = speed;
    }

    public void fire(int x, int y, int targetX, int targetY){

    }

}
