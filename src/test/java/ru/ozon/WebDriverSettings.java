package ru.ozon;

import jdk.jfr.Description;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * Класс подгатавливает WebDriver и устанавливает предварительные настрйки
 * перед выполнением автотестов
 */
public class WebDriverSettings {
    FirefoxDriver driver;
    WebDriverWait wait;

    /**
     *  Setup driver parameters
     */
    @BeforeTest
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/drivers/geckodriver.exe");
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 10);
    }

    /**
     * Driver quit
     */
    @Description("21. Закрыть браузер")
    @AfterTest
    public void afterTest() {
        driver.quit();
    }
}
