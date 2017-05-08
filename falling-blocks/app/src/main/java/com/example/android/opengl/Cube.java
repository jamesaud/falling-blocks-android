package com.example.android.opengl;

/**
 * Created by jamesaudretsch on 5/3/17.
 */

public class Cube implements Comparable<Cube>  {
    public float x;
    public float y;
    public float z;
    public float speed;

    public Cube(float x, float y, float z, float speed){
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
    }

    public int compareTo(Cube cube)
    {
        if (this.y - cube.y < 0){
            return 1;
        }

        else if (this.y - cube.y > 0){
            return -1;
        }
        return 0;
    }


}
