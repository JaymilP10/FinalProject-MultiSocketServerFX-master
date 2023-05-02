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
        int[] slope = reduceFraction(targetY - y, targetX - x);
        
    }

    public static int[] reduceFraction(int numerator, int denominator) {
        int gcd = findGCD(numerator, denominator);
        int[] result = {numerator/gcd, denominator/gcd};
        return result;
    }

    public static int findGCD(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return findGCD(b, a % b);
        }
    }
}
