package com.quetzalcoatl.telebot.handlers;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StartHandlerImplTest {
    private static Handler handler;
    @BeforeClass
    public static void init(){
        handler = new StartHandlerImpl();
    }

    @Test
    public void isSuitableTest(){
        Assert.assertTrue(handler.isSuitable("/start"));
        Assert.assertTrue(handler.isSuitable("Старт"));
        Assert.assertTrue(handler.isSuitable("старт"));
        Assert.assertTrue(handler.isSuitable("СтарТ"));
        Assert.assertFalse(handler.isSuitable("погода"));

    }
    //public boolean isSuitable(String text)
}
