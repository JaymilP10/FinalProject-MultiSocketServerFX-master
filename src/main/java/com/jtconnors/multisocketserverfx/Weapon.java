package com.jtconnors.multisocketserverfx;

import javafx.scene.control.Button;
import javafx.scene.image.Image;

public class Weapon {

    String weaponName;
    String weaponType;
    int range;
    int damage;
    int ammo;
    int maxAmmo;
    double speed;
    double startTime = System.nanoTime();
    int squaresTravelled;

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
        startTime = System.nanoTime();
    }


}

class Bullets extends Weapon{

    int x;
    int y;
    double startTime;

    public Bullets(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void fire(int targetX, int targetY, Button[][] buttons, Map[][] map){
        System.out.println("called fire");
        System.out.println(squaresTravelled);
        System.out.println("ty:" + targetY + " tx:" + targetX + " y:" + y + " x:" + x);
        int[] slope = reduceFraction(targetY - y, x - targetX);
        squaresTravelled++;
        x += slope[1];
        y += slope[0];
//        x += findGCD(y - targetY, targetX - x);
//        y -= findGCD(y - targetY, targetX - x);

        if (x < 100 && y < 50){
            buttons[x][y].setStyle("-fx-background-color: brown");
            map[x][y].newNum = 7;
        }
    }

    public static int[] reduceFraction(int numerator, int denominator) {
        System.out.println("nume: " + numerator + " deno:" + denominator);
        int gcd = findGCD(numerator, denominator);
        if (gcd == 1){
            return new int[]{1, 1};
        } else if (gcd == -1){
            return new int[]{-1, -1};
        }
        int[] result = {numerator/gcd, denominator/gcd};
        return result;
    }

//    public static int findGCD(int a, int b) {
//        if (b == 0) {
//            return a;
//        } else {
//            return findGCD(b, a % b);
//        }
//    }

    public static int findGCD(int x, int y)
    {
        int r = 0, a, b;
        a = Math.max(x, y); // a is greater number
        b = Math.min(x, y); // b is smaller number
        r = b;
//        if (a == b)
//            r = 1;

        while(a % b != 0) {
            r = a % b;
            a = b;
            b = r;
        }
        if (r == 0){
            return 1;
        }
        System.out.println("r:" + r);
        return r;
    }
}

