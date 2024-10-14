package com.example.brickbreaker;

public class Velocity {
    private float x,y;
    public Velocity(int x,int y)
    {
        this.x = x;
        this.y = y;
    }
    public float getX()
    {
        return x;
    }
    public void setX(float x)
    {
        this.x = x;
    }
    public float getY()
    {
        return y;
    }
    public void setY(float y)
    {
        this.y = y;
    }
}
