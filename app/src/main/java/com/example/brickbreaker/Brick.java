package com.example.brickbreaker;

public class Brick {
    private boolean is_Visible;
    public int width,height,row,column,BrickLife;
    public Brick(int row,int column,int width,int height,int BrickLife)
    {
        is_Visible = true;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
        this.BrickLife = BrickLife;
    }
    public void setInvisible()
    {
        is_Visible = false;
    }
    public boolean getVisibility()
    {
        return is_Visible;
    }
}
