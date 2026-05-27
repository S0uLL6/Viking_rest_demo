package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;
import ru.mephi.vikingdemo.repository.VikingRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Практикум на лямбда-функции.
 *
 * Сервис, в котором ВЕСЬ функционал реализуется через лямбды и Stream API:
 *  1) подсчёт объёма выборки по разным условиям;
 *  2) выдача данных для отображения (рандом, фильтрация, сортировка);
 *  3) операции с массивом ID (max, чётные).
 *
 * Predicate-фабрики возвращают функциональные объекты — их можно
 * комбинировать через .and() / .or() / .negate(), как требует условие
 * («в диапазоне» / «вне диапазона» = одно и то же через .negate()).
 */
@Service
public class VikingLambdaService {

    private final VikingService vikingService;
    private final VikingRepository vikingRepository;
    private final Random random = new Random();

    public VikingLambdaService(VikingService vikingService, VikingRepository vikingRepository) {
        this.vikingService = vikingService;
        this.vikingRepository = vikingRepository;
    }

    // =========================================================================
    // 1) Predicate-фабрики (условия выборки)
    // =========================================================================

    public static Predicate<Viking> ageGreaterThan(int age) {
        return v -> v.age() > age;
    }

    public static Predicate<Viking> ageLessThan(int age) {
        return v -> v.age() < age;
    }

    public static Predicate<Viking> ageBetween(int from, int to) {
        return v -> v.age() >= from && v.age() <= to;
    }

    public static Predicate<Viking> ageOutsideRange(int from, int to) {
        // вне диапазона = отрицание «в диапазоне» — наглядно показываем работу с лямбдами
        return ageBetween(from, to).negate();
    }

    public static Predicate<Viking> beardAndHair(BeardStyle beardStyle, HairColor hairColor) {
        // комбинированное условие: форма бороды И цвет волос одновременно
        Predicate<Viking> byBeard = v -> v.beardStyle() == beardStyle;
        Predicate<Viking> byHair = v -> v.hairColor() == hairColor;
        return byBeard.and(byHair);
    }

    /**
     * Викинги, у которых ровно n топоров (n = 1 или 2).
     */
    public static Predicate<Viking> hasAxes(int axeCount) {
        return v -> v.equipment().stream()
                .filter(item -> "Axe".equals(item.name()))
                .count() == axeCount;
    }

    public static Predicate<Viking> hasOneOrTwoAxes() {
        return hasAxes(1).or(hasAxes(2));
    }

    public static Predicate<Viking> hasLegendaryEquipment() {
        return v -> v.equipment().stream()
                .anyMatch(item -> "Legendary".equals(item.quality()));
    }

    // =========================================================================
    // 2) Подсчёт объёма выборки
    // =========================================================================

    public long count(Predicate<Viking> predicate) {
        return vikingService.findAll().stream()
                .filter(predicate)
                .count();
    }

    // =========================================================================
    // 3) Выдача информации для вывода на экран
    // =========================================================================

    /**
     * Случайный викинг ростом выше указанного значения.
     * Если таких нет — возвращаем Optional.empty().
     */
    public Optional<Viking> randomVikingTallerThan(int heightCm) {
        List<Viking> candidates = vikingService.findAll().stream()
                .filter(v -> v.heightCm() > heightCm)
                .toList();

        return candidates.isEmpty()
                ? Optional.empty()
                : Optional.of(candidates.get(random.nextInt(candidates.size())));
    }

    public List<Viking> withLegendaryEquipment() {
        return vikingService.findAll().stream()
                .filter(hasLegendaryEquipment())
                .toList();
    }

    /**
     * «Рыжебородые» — в модели нет цвета бороды, поэтому интерпретируем
     * как викингов с рыжими волосами (HairColor.Red), отсортированных по возрасту.
     */
    public List<Viking> redHairSortedByAge() {
        return vikingService.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red)
                .sorted(Comparator.comparingInt(Viking::age))
                .toList();
    }

    // =========================================================================
    // 4) Операции с массивом ID (берём id всех викингов из БД)
    // =========================================================================

    public List<Integer> allIds() {
        return vikingRepository.findAll().stream()
                .map(VikingEntity::id)
                .toList();
    }

    public Optional<Integer> maxId() {
        return allIds().stream()
                .max(Comparator.naturalOrder());
    }

    public List<Integer> evenIds() {
        return allIds().stream()
                .filter(id -> id % 2 == 0)
                .toList();
    }
}
