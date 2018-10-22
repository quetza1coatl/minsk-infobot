package com.quetzalcoatl.telebot.response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StartResponseImplTest {
    private static Response response;
    @BeforeClass
    public static void init(){
        response = new StartResponseImpl();
    }

    @Test
    public void isSuitableTest(){
        Assert.assertTrue(response.isSuitable("/start"));
        Assert.assertTrue(response.isSuitable("Старт"));
        Assert.assertTrue(response.isSuitable("старт"));
        Assert.assertTrue(response.isSuitable("СтарТ"));
        Assert.assertFalse(response.isSuitable("погода"));

    }
    //public boolean isSuitable(String text)
}
