package ru.ozon.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Класс сущность с полями <b>title</b> и <b>price</b>
 * @author Renat Khalimov
 */
@Data
@AllArgsConstructor
public class Item {
    private String title;
    private String price;
}
