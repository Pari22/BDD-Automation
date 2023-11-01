package starter.navigation;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

@DefaultUrl("https://duckduckgo.com")
class DuckDuckGoHomePage extends PageObject {

    static{
        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/windows/msedgedriver.exe");
    }
}
