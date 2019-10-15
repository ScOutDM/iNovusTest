package ru.ozon;

import java.util.Random;

/**
 * Класс генерации случайного числа из диапазона количества элементов списка вещей
 */
public class RandomItem {
    private Random random = new Random();
    private int randomItem;

    /**
     *  Метод для получения случайного числа {@link OzonTest#itemIndex}
     * @param itemQuantity количество элементов
     * @return возвращает случайное число
     */
    public int getRandomItem(int itemQuantity) {
        randomItem = random.nextInt(itemQuantity);
        return randomItem;
    }
}
