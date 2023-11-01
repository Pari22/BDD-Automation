package starter.search;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.NamedUrl;
import net.thucydides.core.annotations.NamedUrls;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ImageResults extends PageObject {
//    static{
//        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/windows/msedgedriver.exe");
//    }

    By imagesTab = By.xpath("//a[@data-zci-link='images']");
    By colourDD = By.xpath("//div[contains(@class,'color')]");
    By redColour = By.xpath("//a[text()='Red']");

    public void selectColour(String image, String colour) {
        $(imagesTab).click();
        $(colourDD).click();
        $(redColour).click();
    }

    public void scrollPage() {
        JavascriptExecutor je = (JavascriptExecutor) getDriver();
        je.executeScript("arguments[0].scrollIntoView(true);", $(imagesTab));
    }

    public void clickImagesButton() {
        $(imagesTab).click();
        new WebDriverWait(getDriver(), 10).until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(colourDD)));
    }

    public void enterImageData(String data) {
        getDriver().findElement(By.xpath(" //label[text()='" + data + "']")).click();
    }
}
