package com.jtconnors.multisocketserverfx;

import javafx.scene.image.Image;

public class Weapon {

    String weaponName;
    String weaponType;
    int range;
    int damage;
    int ammo;
    int maxAmmo;

    public Weapon(String weaponName, String weaponType, int range, int damage, int maxAmmo){
        this.weaponName = weaponName;
        this.weaponType = weaponType;
        this.range = range;
        this.damage = damage;
        this.ammo = maxAmmo;
        this.maxAmmo = maxAmmo;
    }
}
