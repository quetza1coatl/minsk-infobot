package com.quetzalcoatl.minskinfobot.handlers;

import org.junit.BeforeClass;
import static com.quetzalcoatl.minskinfobot.HandlersTestData.*;

public class StartHandlerImplTest extends AbstractHandlerTest{

    @BeforeClass
    public static void init(){
        handler = new StartHandlerImpl();
        key = TEST_MESSAGE_START_KEY;
    }

}
