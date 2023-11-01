package starter.utilities;

import net.serenitybdd.core.pages.PageObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utilities extends PageObject {
    public static WebDriver _getDriver;

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    public String getCurrentTime() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return ZonedDateTime.now().format(FORMATTER);
    }

    public static String getCurrentDate() {
        return ZonedDateTime.now().format(dateFormatter);
    }

    public static String getCurrentDay() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        return ZonedDateTime.now().format(FORMATTER);
    }

    public static String getCurrentTimeString() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");
        return ZonedDateTime.now().format(FORMATTER);
    }

    public static boolean isSortedAlphabeticallyAscending(List<String> listOfStrings) {
        if (listOfStrings.isEmpty() || listOfStrings.size() == 1) {
            return true;
        }

        Iterator<String> iter = listOfStrings.iterator();
        String current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            if (previous.compareTo(current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSortedAlphabeticallyDescending(List<String> listOfStrings) {
        if (listOfStrings.isEmpty() || listOfStrings.size() == 1) {
            return true;
        }

        Iterator<String> iter = listOfStrings.iterator();
        String current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            if (previous.compareTo(current) < 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSortedDateAscending(List<String> listOfDates) {
        if (listOfDates.isEmpty() || listOfDates.size() == 1) {
            return true;
        }

        Iterator<String> iter = listOfDates.iterator();
        String current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            LocalDate previousDate = LocalDate.parse(previous, dateFormatter);
            LocalDate currentDate = LocalDate.parse(current, dateFormatter);
            if (previousDate.isAfter(currentDate)) {
                return false;
            }
            previous = current;
        }
        return true;
    }

    public static boolean isSortedDateDescending(List<String> listOfDates) {
        if (listOfDates.isEmpty() || listOfDates.size() == 1) {
            return true;
        }

        Iterator<String> iter = listOfDates.iterator();
        String current, previous = iter.next();
        while (iter.hasNext()) {
            current = iter.next();
            LocalDate previousDate = LocalDate.parse(previous, dateFormatter);
            LocalDate currentDate = LocalDate.parse(current, dateFormatter);
            if (previousDate.isBefore(currentDate)) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    private String getFileNameFromResources(String fileName) {
        String path = "src/test/resources/files";

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        String filePath = absolutePath + "/" + fileName;

        return filePath;
    }
    public static boolean doesListContainString(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static synchronized Map<String, String> TableDictionaryConverter(List<List<String>> data) {
        Map<String, String> mapTable = new HashMap<String, String>();
        for (List<String> rows : data) {
            mapTable.put(rows.get(0), rows.get(1));
        }
        return mapTable;

    }

    public static String getRandomString(int numberOfDigits) {
        return RandomStringUtils.randomAlphabetic(numberOfDigits);
    }


    public Map<String, String> makeFileCopy(Map<String,String> filesList,String currentTime) throws IOException {
        String path = "src/test/resources/files/";
        String tempPath = "src/test/tempDir/";
        Map<String, String> copiedFiles = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry: filesList.entrySet()){
            var source =  new File(path+entry.getKey()+"."+entry.getValue());
            String newFile = tempPath+entry.getKey()+currentTime+"."+entry.getValue();
            var dest = new File(newFile);
            FileUtils.copyFile(source, dest);

            List<String> splitString = Arrays.asList(newFile.split("/"));

            copiedFiles.put(entry.getKey(), splitString.get(3));

        }
        return copiedFiles;
    }

    static String fileToBeChanged = null;
    static String newFileName = null;
    static boolean isSaveButtonClicked = false;

    public static void setFileToBeChanged(String originalFileName) {
        fileToBeChanged = originalFileName;
    }

    public static void setNewFileName(String newDisplayName) {
        newFileName = newDisplayName;
    }

    public static void setIsSaveButtonClicked(boolean isSaveButtonClicked) {
        Utilities.isSaveButtonClicked = isSaveButtonClicked;
    }

    public static String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String trimChildElementInformation(By webSelector, int orderNumber) {
        String text = null;
        List<WebElement> children = new ArrayList<>();
        text = getDriver().findElements(webSelector).get(orderNumber).getText().trim();
        children = getDriver().findElements(webSelector).get(orderNumber).findElements(By.xpath("./*"));
        for (WebElement child : children) {
            text = text.replaceFirst(child.getText(), "").trim();
        }
        return text;
    }

    public static String getFilePathFromResources(String fileName) {
        String path = "src/test/resources/files";
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();
        return absolutePath + "/" + fileName;
    }

    public static LocalDateTime getDateTimeFromString(String date, String time) {
        int minute = Integer.parseInt(time.substring(3));
        int hour = Integer.parseInt(time.substring(0,2));
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int year = Integer.parseInt(date.substring(6));
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    public static boolean isSortedDateTime(List<LocalDateTime> localDateTimeList, String ascendingOrDescending) {
        if (localDateTimeList.isEmpty() || localDateTimeList.size() == 1) {
            return true;
        }

        Iterator<LocalDateTime> iter = localDateTimeList.iterator();
        LocalDateTime current, previous = iter.next();

        if (ascendingOrDescending.equalsIgnoreCase("Descending")) {
            while (iter.hasNext()) {
                current = iter.next();
                if (previous.isBefore(current)) {
                    return false;
                }
                previous = current;
            }
            return true;

        } else if (ascendingOrDescending.equalsIgnoreCase("Ascending")) {
            while (iter.hasNext()) {
                current = iter.next();
                if (previous.isAfter(current)) {
                    return false;
                }
                previous = current;
            }
            return true;
        } else {
            System.out.println("Please specify either ascending or descending");
            return false;
        }
    }
}
