package ru.ozon;

import jdk.jfr.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.ozon.domain.Item;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Класс тестировки вебинтерфейса
 */
public class OzonTest extends WebDriverSettings {
    /**
     * DOM элементы. Используется в поиске элементов в документе
     */
    private WebElement detail;

    /**
     * DOM элементы. Используется в поиске элементов в документе
     */
    private WebElement itemSearchResult;

    /**
     * DOM элементы. Используется в поиске элементов в документе
     */
    private List<WebElement> itemList;

    /**
     * Целочисленное поле для хранения количества товаров в списке
     */
    private int itemQuantity;

    /**
     * Целочисленное поле для хранения индекса товара в списке
     */
    private int itemIndex;

    /**
     * Поле типа Item. Сущность описывающая товар
     */
    private Item item;


    /**
     * Коллекция для хранения списка товаров
     */
    private List<Item> itemArrayList = new ArrayList<>();

    /**
     * Открывает страницу браузера с указанным URL
     */
    @Test(description = "1. Открыть в браузере сайт https://www.ozon.ru/")
    public void urlOpeningTest() {
        driver.get("https://www.ozon.ru/");

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.ozon.ru/");
    }

    /**
     * Осуществляет переход в категорию товаров
     */

    @Test(dependsOnMethods = {"urlOpeningTest"}, description = "2. В меню \"Каталог\" выбрать категорию \"Музыка\"")
    public void categoryOpeningTest() {
        driver.findElement(By.cssSelector("button[value=\"Каталог\"]")).click();
        driver.findElement(By.cssSelector("[href=\"/category/muzyka-13100/\"]")).click();

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.ozon.ru/category/muzyka-13100/");
    }

    /**
     * Осуществляет переход в подкатегорию товаров
     */
    @Test(dependsOnMethods = {"categoryOpeningTest"}, description = "3. С открывшейся страницы перейти на страницу \"Виниловые пластинки\"")
    public void categorySelectionTest() {
        driver.findElement(By.cssSelector("[href=\"/category/vinilovye-plastinki-31667/\"]")).click();
        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tiles")));

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.ozon.ru/category/vinilovye-plastinki-31667/");
    }

    /**
     * Получает DOM список товаров из документа
     */
    @Test(dependsOnMethods = {"categorySelectionTest"}, description = "4. Проверить, что открылся список товаров")
    public void checkNotEmptyItemListTest() {
        itemSearchResult = driver.findElement(By.cssSelector("div.tiles[data-v-4d7067c2]"));
        itemList = itemSearchResult.findElements(By.className("inner-link"));

        Assert.assertNotNull(itemList);
    }

    /**
     * Получает размер списка товаров
     */
    @Test(dependsOnMethods = {"checkNotEmptyItemListTest"}, description = "5. Получить количество товаров на странице")
    public void getTheQuantityOfItemsTest() {
        itemQuantity = itemList.size();

        Assert.assertEquals(itemQuantity, itemList.size());
    }

    /**
     * Использует класс RandomItem() для генерации случайного индекса товара
     */
    @Test(dependsOnMethods = {"getTheQuantityOfItemsTest"}, description = "6. Сгенерировать случайное число в диапазоне от 1 до количества товаров, полученного в п.5")
    public void getItemIndexTest() {
        itemIndex = new RandomItem().getRandomItem(itemQuantity);

        Assert.assertTrue((itemIndex >= 0) && (itemIndex < (itemList.size() - 1)));
    }

    /**
     * Выбирает товар по индексу и переходит на страницу товара
     */
    @Test(dependsOnMethods = {"getItemIndexTest"}, description = "7. Выбрать товар под номером, полученным в п.6. ( Перейти на страницу товара )")
    public void getItemPageTest() {
        WebElement itemByIndex = itemList.get(itemIndex);
        itemByIndex.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("detail")));

        Assert.assertNotNull(itemByIndex);
    }

    /**
     * Сохраняет информацию о товаре в ссылочной переменной
     */
    @Test(dependsOnMethods = {"getItemPageTest"}, description = "8. Запомнить стоимость и название данного товара")
    public void getItemDetailsTest() {
        detail = driver.findElement(By.className("detail"));

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));

        item = new Item(
                driver.findElement(By.cssSelector("h1._718dda")).getText(),
                driver.findElement(By.cssSelector("span.b3411b")).getText()
        );

        Assert.assertNotNull(item);
    }

    /**
     * Добавляет товар в корзину
     */
    @Test(dependsOnMethods = {"getItemDetailsTest"}, description = "9. Добавить товар в корзину")
    public void addToCart() {
        detail.findElement(By.cssSelector("button._652bc6")).click();

        itemArrayList.add(item);

        Assert.assertEquals(itemArrayList.size(), 1);
    }

    /**
     * Проверяет наличие товара в корзине
     */
    @Test(dependsOnMethods = {"addToCart"}, description = "10. Проверить то, что в корзине появился добавленный в п.9 товар")
    public void cartAfterAddingFirstProduct() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        detail.findElement(By.cssSelector("a._652bc6.ab8d80")).click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart-item")));

        WebElement cartItem = driver.findElement(By.cssSelector("div.cart-item"));
        Item itemInTheCart = new Item(
                cartItem.findElement(By.className("title")).findElement(By.tagName("span")).getText(),
                cartItem.findElement(By.className("price-block")).findElement(By.tagName("span")).getText()
        );

        Assert.assertTrue(itemArrayList.contains(itemInTheCart));
    }

    /**
     * Возвращает на страницу подкатегории
     */
    @Test(dependsOnMethods = {"cartAfterAddingFirstProduct"}, description = "11. Вернуться на страницу \"Виниловые пластинки\"")
    public void getBackTest() {
        driver.findElement(By.cssSelector("button[value=\"Каталог\"]")).click();
        driver.findElement(By.cssSelector("[href=\"/category/muzyka-13100/\"]")).click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[value=\"Каталог\"]")));
        driver.findElement(By.cssSelector("button[value=\"Каталог\"]")).click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href=\"/category/muzyka-13100/\"]")));
        driver.findElement(By.cssSelector("[href=\"/category/muzyka-13100/\"]")).click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[href=\"/category/vinilovye-plastinki-31667/\"]"))).click();
        driver.findElement(By.cssSelector("[href=\"/category/vinilovye-plastinki-31667/\"]"));

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.ozon.ru/category/vinilovye-plastinki-31667/");
    }

    /**
     * Использует класс RandomItem() для генерации случайного числа
     */
    @Description("12. Сгенерировать случайное число в диапазоне от 1 до количества товаров, полученного в п.5")
    @Test(dependsOnMethods = {"getBackTest"}, description = "12. Сгенерировать случайное число в диапазоне от 1 до количества товаров, полученного в п.5")
    public void getSecondItemIndexTest() {
        itemIndex = new RandomItem().getRandomItem(itemQuantity);

        Assert.assertTrue((itemIndex >= 0) && (itemIndex < itemList.size() - 1));
    }

    /**
     * Выбирает товар из списка по индексу и переходит на страницу товара
     */
    @Test(dependsOnMethods = {"getSecondItemIndexTest"}, description = "13. Выбрать товар под номером, полученным в п.12. ( Перейти на страницу товара )")
    public void getSecondItemPageTest() {
        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tiles")));

        itemSearchResult = driver.findElement(By.cssSelector("div.content[data-v-4d7067c2]"));
        itemList = itemSearchResult.findElements(By.className("inner-link"));

        WebElement itemByIndex = itemList.get(itemIndex);
        itemByIndex.click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("detail")));

        Assert.assertNotNull(itemByIndex);
    }

    /**
     * Сохраняет информацию со страницы товара
     */
    @Test(dependsOnMethods = {"getSecondItemPageTest"}, description = "14. Запомнить стоимость и название данного товара")
    public void getSecondItemDetailsTest() {
        detail = driver.findElement(By.className("detail"));

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));

        item = new Item(
                detail.findElement(By.cssSelector("h1._718dda")).getText(),
                driver.findElement(By.cssSelector("span.b3411b")).getText()
        );

        Assert.assertNotNull(item);
    }

    /**
     * Добавляет товар в корзину
     */
    @Test(dependsOnMethods = {"getSecondItemDetailsTest"}, description = "15. Добавить товар в корзину")
    public void addToCartSecondItemTest() {
        detail.findElement(By.cssSelector("div._7db0fb[data-v-c66bfbbc]")).click();

        itemArrayList.add(item);
    }

    /**
     * Провеяет количество товаров в корзине
     */
    @Test(dependsOnMethods = {"addToCartSecondItemTest"}, description = "16. Проверить то, что в корзине два товара. ( Проверка количества товаров в корзине. Может\n" +
            "быть произведена без открытия корзины, а проверяя значение в header сайта, где указано\n" +
            "количество товаров в корзине )")
    public void checkCartByHeader() {
        driver.navigate().refresh();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        WebElement headerElement = driver.findElement(By.tagName("header")).findElement(By.cssSelector("span.f-caption--bold.ef9580"));

        int cartItemCounter = Integer.parseInt(headerElement.getText());

        Assert.assertEquals(cartItemCounter, itemArrayList.size());
    }

    /**
     * Осуществляет переход в корзину
     */
    @Test(dependsOnMethods = {"checkCartByHeader"}, description = "17. Открыть корзину")
    public void goToCartTest() {
        driver.findElement(By.tagName("header")).findElement(By.cssSelector("[href=\"/cart\"]")).click();

        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart-item")));

        WebElement cartItem = driver.findElement(By.className("cart-item"));

        Assert.assertNotNull(cartItem);
    }

    /**
     * Проверяет на соответствие выбранные товары с добавленными в корзину
     * и верный расчет итоговой суммы
     */
    @Test(dependsOnMethods = {"goToCartTest"}, description = "18. Проверить то, что в корзине раннее выбранные товары и итоговая стоимость по двум товарам рассчитана верно")
    public void cartAfterAddingSecondProduct() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div._83a4a5.column__item_remove-margin")));

        WebElement cartElement = driver.findElementByCssSelector("div._83a4a5.column__item_remove-margin");
        List<WebElement> cartItems = cartElement.findElements(By.className("cart-item"));

        List<Item> itemsInTheCartList = new ArrayList<>();

        for (WebElement e : cartItems) {
            itemsInTheCartList.add(new Item(
                    e.findElement(By.className("title")).findElement(By.tagName("span")).getText(),
                    e.findElement(By.className("price-block")).findElement(By.tagName("span")).getText()
            ));
        }

        Assert.assertTrue(itemsInTheCartList.containsAll(itemArrayList));

        int totalItemListAmount = new AmountOfItems().getItemAmount(itemArrayList);

        WebElement cartPriceElement = driver.findElementByClassName("total-middle-footer");
        String cartPriceAmount = cartPriceElement.findElement(By.cssSelector("span.total-middle-footer-text")).getText();
        cartPriceAmount = cartPriceAmount.replaceAll("[^0-9]", "");

        int totalCartAmount = Integer.parseInt(cartPriceAmount);

        Assert.assertEquals(totalCartAmount, totalItemListAmount);
    }

    /**
     * Удаляет все товары из корзины
     */
    @Test(dependsOnMethods = {"cartAfterAddingSecondProduct"}, description = "19. Удалить из корзины все товары")
    public void removeItemFromCartTest() {
        WebElement removeElement = driver.findElementByCssSelector("div.e276f9.column__item_remove-margin");
        removeElement.findElement(By.cssSelector("span.dc5e23.a443c8")).click();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement webElement = driver.findElement(By.xpath("/html/body/div[5]"));
        webElement.findElement(By.cssSelector("div.modal-mask.light")).findElement(By.cssSelector("button[class=\"button.button.blue\"]")).click();
        itemArrayList.clear();
    }

    /**
     * Проверяет корзину на отсутствие товаров
     */
    @Test(dependsOnMethods = {"removeItemFromCartTest"}, description = "20. Проверить, что корзина пуста")
    public void EmptyCartTest() {
        wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.bd9af9.column__item_remove-margin")));

        WebElement emptyCart = driver.findElement(By.cssSelector("div.bd9af9.column__item_remove-margin"));
        Assert.assertNotNull(emptyCart);
    }
}
