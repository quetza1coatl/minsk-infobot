package com.quetzalcoatl.telebot.handlers.main;


import com.quetzalcoatl.telebot.handlers.AbstractHandlerTest;
import org.junit.BeforeClass;
import static com.quetzalcoatl.telebot.HandlersTestData.*;

public class ExchangeRateHandlerImplTest extends AbstractHandlerTest {

    @BeforeClass
    public static void  init(){
        handler = new ExchangeRateHandlerImpl();
        key = TEST_MESSAGE_EXCHANGE_RATE_KEY;
    }
}