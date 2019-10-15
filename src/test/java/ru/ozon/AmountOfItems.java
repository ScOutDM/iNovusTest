package ru.ozon;

import ru.ozon.domain.Item;

import java.util.List;

/**
 * Класс суммирует общую стоимость вещей в списке
 */
public class AmountOfItems {
    private int itemsAmount;

    /**
     * Метод для получения общей стоимости всех вещей в списке
     * @param items список вещей
     * @return  общая сумма стоимости всех вещей
     */
    public int getItemAmount(List<Item> items){
        for (Item i : items) {
            String price = i.getPrice().replaceAll("[^0-9]", "");
            itemsAmount += Integer.parseInt(price);
        }
        return itemsAmount;
    }
}
