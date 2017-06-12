package com.marfeel;

import com.marfeel.controller.MarfeelRestController;
import com.marfeel.multithreading.SiteChecker;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                MarfeelRestController.class,
                SiteChecker.class
        })
public class SpringRestTestClient {


}