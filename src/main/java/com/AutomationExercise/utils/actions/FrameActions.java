package com.AutomationExercise.utils.actions;

import com.AutomationExercise.utils.wait.WaitManager;
import com.AutomationExercise.utils.logs.LogsManager;
import org.openqa.selenium.WebDriver;

public class FrameActions {
    private final WebDriver driver;
    private final WaitManager waitManager;
    public FrameActions(WebDriver driver) {
        this.driver = driver;
        this.waitManager = new WaitManager(driver);
    }
    /**
     * Switches to a frame by index
     *
     * @param index the index of a frame
     */
    public void switchToFrame(int index){
        waitManager.fluentWait().until(d->{
            try {
                driver.switchTo().frame(index);
                LogsManager.info("Switch to frame indexed " + index);
                return true;
            }catch (Exception e){
                LogsManager.error("Switch To Frame Failed", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Switches to a frame by Frame Name
     *
     * @param frameName the name of a frame
     */
    public void switchToFrameByName(String frameName){
        waitManager.fluentWait().until(d->{
            try {
                driver.switchTo().frame(frameName);
                LogsManager.info("Switch to frame Named " + frameName);
                return true;
            }catch (Exception e){
                LogsManager.error("Frame switching error: ", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Switches to a frame by Frame Locator
     *
     * @param frameLocator the locator of a frame
     */
    public void switchToFrameByLocator(String frameLocator){
        waitManager.fluentWait().until(d->{
            try {
                driver.switchTo().frame(frameLocator);
                LogsManager.info("Switch to frame Locator " + frameLocator);
                return true;
            }catch (Exception e){
                LogsManager.error("Frame switching error: ", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Switches to the Default Content
     */
    public void switchToDefaultContent(){
        waitManager.fluentWait().until(d->{
            try {
                driver.switchTo().defaultContent();
                LogsManager.info("Switch to Default Content");
                return true;
            }catch (Exception e){
                LogsManager.error("Switch To Default Content Failed", e.getMessage());
                return false;
            }
        });
    }


}
