package com.yk.test;

public class M
{
    public M foo()
    {
        return this;
    }
}

class N extends M
{
    @Override
    public M foo()
    {
        return this;
    }
}

class O extends N
{
    @Override
    public O foo()
    {
        return null;
    }

}