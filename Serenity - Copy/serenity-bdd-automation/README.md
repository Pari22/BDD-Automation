# Getting started with Serenity and Cucumber 4


Serenity BDD is a library that makes it easier to write high quality automated acceptance tests, with powerful reporting and living documentation features. It has strong support for both web testing with Selenium, and API testing using RestAssured. 

Serenity strongly encourages good test automation design, and supports several design patterns, including classic Page Objects, the newer Lean Page Objects/ Action Classes approach, and the more sophisticated and flexible Screenplay pattern.
There are two versions to choose from. The master branch uses a more classic approach, using action classes and lightweight page objects, whereas the **[screenplay](https://github.com/serenity-bdd/serenity-cucumber4-starter/tree/screenplay)** branch shows the same sample test implemented using Screenplay.


## The Cucumber 4 sample scenario

```Gherkin
Feature: Search by keyword

  Scenario: Searching for a term
    Given Sergey is on the DuckDuckGo home page
    When he searches for "cucumber"
    Then all the result titles should contain the word "cucumber"
```

This scenario lets us explore a few of the new Cucumber 4 expressions. Cucumber 4 supports both the classic regular expressions, and the new _Cucumber Expressions_, which are more readable albeit not as powerful in some cases. 

The glue code for this scenario uses both regular expressions and cucumber expressions. The glue code looks this this:

```java
    @Given("^(?:.*) is on the DuckDuckGo home page")	 
    public void i_am_on_the_DuckDuckGo_home_page() {
        navigateTo.theDuckDuckGoHomePage();
    }

    @When("(s)he searches for {string}")				
    public void i_search_for(String term) {
        searchFor.term(term);
    }

    @Then("all the result titles should contain the word {string}")
    public void all_the_result_titles_should_contain_the_word(String term) {
        assertThat(searchResult.titles())
                .matches(results -> results.size() > 0)
                .allMatch(title ->  
                         textOf(title).containsIgnoringCase(term));
    }
```

The `@Given` step uses a regular expression; the action class approach we use here is action-centric, not actor-centric, so we ignore the name of the actor. 

The `@When` and `@Then` steps uses Cucumber expressions, and highlights two useful features. Rather than using a regular expression to match the search term, we use the more readable Cucumber expression _{string}_. This matches a single or double-quoted string (the quotes themselves are dropped). Cucumber 4 also supports other typed expressions, such as _{int}_, _{word}_, and _ {float}_. 

Parentheses can be used to indicate optional text, so _“(s)he”_ will match both “he” and “she”. We could also write this using a slash: _“she/he”_.

### Lean Page Objects and Action Classes
The glue code shown above uses Serenity step libraries as _action classes_ to make the tests easier to read, and to improve maintainability.

These classes are declared using the Serenity `@Steps` annotation, shown below:
```java
    @Steps
    NavigateTo navigateTo;

    @Steps
    SearchFor searchFor;

    @Steps
    SearchResult searchResult;
```

The `@Steps`annotation tells Serenity to create a new instance of the class, and inject any other steps or page objects that this instance might need. 

Each action class models a particular facet of user behaviour: navigating to a particular page, performing a search, or retrieving the results of a search. These classes are designed to be small and self-contained, which makes them more stable and easier to maintain.

The `NavigateTo` class is an example of a very simple action class. In a larger application, it might have some other methods related to high level navigation, but in our sample project, it just needs to open the DuckDuckGo home page:
```java
public class NavigateTo {

    DuckDuckGoHomePage duckDuckGoHomePage;

    @Step("Open the DuckDuckGo home page")
    public void theDuckDuckGoHomePage() {
        duckDuckGoHomePage.open();
    }
}
```

It does this using a standard Serenity Page Object. Page Objects are often very minimal, storing just the URL of the page itself:
```java
@DefaultUrl("https://duckduckgo.com")
class DuckDuckGoHomePage extends PageObject {}
```

The second class, `SearchFor`, is an interaction class. It needs to interact with the web page, and to enable this, we make the class extend the Serenity `UIInteractionSteps`. This gives the class full access to the powerful Serenity WebDriver API, including the `$()` method used below, which locates a web element using a `By` locator or an XPath or CSS expression:
```java
public class SearchFor extends UIInteractionSteps {

    @Step("Search for term {0}")
    public void term(String term) {
        $(SearchForm.SEARCH_FIELD).clear();
        $(SearchForm.SEARCH_FIELD).type(term);
        $(SearchForm.SEARCH_BUTTON).click();
    }
} 
```

The `SearchForm` class is typical of a light-weight Page Object: it is responsible uniquely for locating elements on the page, and it does this by defining locators or occasionally by resolving web elements dynamically. 
```java
class SearchForm {
    static By SEARCH_FIELD = By.cssSelector(".js-search-input");
    static By SEARCH_BUTTON = By.cssSelector(".js-search-button");
}
```

The last step library class used in the step definition code is the `SearchResult` class. The job of this class is to query the web page, and retrieve a list of search results that we can use in the AssertJ assertion at the end of the test. This class also extends `UIInteractionSteps` and 
```java
public class SearchResult extends UIInteractionSteps {
    public List<String> titles() {
        return findAll(SearchResultList.RESULT_TITLES)
                .stream()
                .map(WebElementFacade::getTextContent)
                .collect(Collectors.toList());
    }
}
```

The `SearchResultList` class is a lean Page Object that locates the search result titles on the results page:
```java
class SearchResultList {
    static By RESULT_TITLES = By.cssSelector(".result__title");
}
```


## Executing the tests
To run the sample project, you can either just run the `CucumberTestSuite` test runner class, or run either `mvn verify` 

By default, the tests will run using Chrome. You can run them in Firefox by overriding the `driver` system property, e.g.
```json
$ mvn clean verify -Ddriver=firefox
```
Or 
```json
$ gradle clean test -Pdriver=firefox
```

The test results will be recorded in the `target/site/serenity` directory.

## Simplified WebDriver configuration and other Serenity extras
The sample projects both use some Serenity features which make configuring the tests easier. In particular, Serenity uses the `serenity.conf` file in the `src/test/resources` directory to configure test execution options.  
### Webdriver configuration
The WebDriver configuration is managed entirely from this file, as illustrated below:
```java
webdriver {
    driver = chrome
}
headless.mode = true

chrome.switches="""--start-maximized;--test-type;--no-sandbox;--ignore-certificate-errors;
                   --disable-popup-blocking;--disable-default-apps;--disable-extensions-file-access-check;
                   --incognito;--disable-infobars,--disable-gpu"""

```

The project also bundles some of the WebDriver binaries that you need to run Selenium tests in the `src/test/resources/webdriver` directories. These binaries are configured in the `drivers` section of the `serenity.conf` config file:
```json
drivers {
  windows {
    webdriver.chrome.driver = "src/test/resources/webdriver/windows/chromedriver.exe"
    webdriver.gecko.driver = "src/test/resources/webdriver/windows/geckodriver.exe"
  }
  mac {
    webdriver.chrome.driver = "src/test/resources/webdriver/mac/chromedriver"
    webdriver.gecko.driver = "src/test/resources/webdriver/mac/geckodriver"
  }
  linux {
    webdriver.chrome.driver = "src/test/resources/webdriver/linux/chromedriver"
    webdriver.gecko.driver = "src/test/resources/webdriver/linux/geckodriver"
  }
}
```
This configuration means that development machines and build servers do not need to have a particular version of the WebDriver drivers installed for the tests to run correctly.

### Environment-specific configurations
We can also configure environment-specific properties and options, so that the tests can be run in different environments. Here, we configure three environments, __dev__, _staging_ and _prod_, with different starting URLs for each:
```json
environments {
  default {
    webdriver.base.url = "https://duckduckgo.com"
  }
  dev {
    webdriver.base.url = "https://duckduckgo.com/dev"
  }
  staging {
    webdriver.base.url = "https://duckduckgo.com/staging"
  }
  prod {
    webdriver.base.url = "https://duckduckgo.com/prod"
  }
}
```
  
You use the `environment` system property to determine which environment to run against. For example to run the tests in the staging environment, you could run:
```json
$ mvn clean verify -Denvironment=staging
```

See [**this article**](https://johnfergusonsmart.com/environment-specific-configuration-in-serenity-bdd/) for more details about this feature.

## Want to learn more?
For more information about Serenity BDD, you can read the [**Serenity BDD Book**](https://serenity-bdd.github.io/theserenitybook/latest/index.html), the official online Serenity documentation source. Other sources include:
* **[Byte-sized Serenity BDD](https://www.youtube.com/channel/UCav6-dPEUiLbnu-rgpy7_bw/featured)** - tips and tricks about Serenity BDD
* [**Serenity BDD Blog**](https://johnfergusonsmart.com/category/serenity-bdd/) - regular articles about Serenity BDD
* [**The Serenity BDD Dojo**](https://serenitydojo.teachable.com) - Online training on Serenity BDD and on test automation and BDD in general. 
