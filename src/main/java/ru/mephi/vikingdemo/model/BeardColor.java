package ru.mephi.vikingdemo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Цвет бороды викинга")
public enum BeardColor {
    Blond,
    Red,
    Brown,
    Black,
    Gray
}
