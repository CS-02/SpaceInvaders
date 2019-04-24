package com.example.hectormediero.spaceinvadersdas.Resources;


import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="./src/test/java/com/example/hectormediero/spaceinvadersdas/Resources/features",monochrome = true, glue="")
public class testCucumber {


}
