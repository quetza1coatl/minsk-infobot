package com.quetzalcoatl.minskinfobot.handlers.main;


import com.quetzalcoatl.minskinfobot.handlers.AbstractHandlerTest;
import org.junit.BeforeClass;
import static com.quetzalcoatl.minskinfobot.HandlersTestData.*;

public class ExchangeRateHandlerImplTest extends AbstractHandlerTest {

    @BeforeClass
    public static void  init(){
        handler = new ExchangeRateHandlerImpl();
        key = TEST_MESSAGE_EXCHANGE_RATE_KEY;
    }

}