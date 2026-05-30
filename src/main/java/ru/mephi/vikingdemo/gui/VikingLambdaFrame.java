package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Форма для практикума на лямбды. Использует только {@link VikingLambdaService}.
 *
 * Делится на три секции (по пунктам задания):
 *   1) Подсчёт объёма выборки по различным условиям;
 *   2) Вывод на экран (рандом / список / сортировка);
 *   3) Операции с массивом ID.
 *
 * Результаты пишутся в общую текстовую область внизу — туда же выводятся
 * сами лямбда-выражения и подсчитанные значения, чтобы было наглядно при сдаче.
 */
public class VikingLambdaFrame extends JFrame {

    private final VikingLambdaService lambdaService;
    private final JTextArea output = new JTextArea();

    public VikingLambdaFrame(VikingLambdaService lambdaService) {
        this.lambdaService = lambdaService;

        setTitle("Лямбда-сервис викингов");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(new Dimension(950, 750));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel header = new JLabel("Лямбда-сервис", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JPanel sections = new JPanel();
        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        sections.add(buildCountSection());
        sections.add(buildInfoSection());
        sections.add(buildIdsSection());

        add(new JScrollPane(sections), BorderLayout.CENTER);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setText("Здесь будут результаты...\n");
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setPreferredSize(new Dimension(950, 260));
        outputScroll.setBorder(BorderFactory.createTitledBorder("Результат"));
        add(outputScroll, BorderLayout.SOUTH);
    }

    // =========================================================================
    // Секция 1. Подсчёт объёма выборки
    // =========================================================================

    private JPanel buildCountSection() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                "1. Объём выборки (count через лямбду-Predicate)"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // --- Возраст ---
        JSpinner ageGt = spinner(30, 0, 200);
        JSpinner ageLt = spinner(30, 0, 200);
        JSpinner rangeFrom = spinner(20, 0, 200);
        JSpinner rangeTo = spinner(40, 0, 200);

        JButton btnGt = new JButton("Возраст > N");
        btnGt.addActionListener(e -> runCount(
                "ageGreaterThan(" + ageGt.getValue() + ")",
                VikingLambdaService.ageGreaterThan((int) ageGt.getValue())));

        JButton btnLt = new JButton("Возраст < N");
        btnLt.addActionListener(e -> runCount(
                "ageLessThan(" + ageLt.getValue() + ")",
                VikingLambdaService.ageLessThan((int) ageLt.getValue())));

        JButton btnIn = new JButton("В диапазоне [from..to]");
        btnIn.addActionListener(e -> runCount(
                "ageBetween(" + rangeFrom.getValue() + "," + rangeTo.getValue() + ")",
                VikingLambdaService.ageBetween((int) rangeFrom.getValue(), (int) rangeTo.getValue())));

        JButton btnOut = new JButton("Вне диапазона [from..to]");
        btnOut.addActionListener(e -> runCount(
                "ageOutsideRange(" + rangeFrom.getValue() + "," + rangeTo.getValue() + ")",
                VikingLambdaService.ageOutsideRange((int) rangeFrom.getValue(), (int) rangeTo.getValue())));

        JPanel ageRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ageRow1.add(new JLabel("N:"));
        ageRow1.add(ageGt);
        ageRow1.add(btnGt);
        ageRow1.add(Box.createHorizontalStrut(15));
        ageRow1.add(new JLabel("N:"));
        ageRow1.add(ageLt);
        ageRow1.add(btnLt);

        JPanel ageRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ageRow2.add(new JLabel("from:"));
        ageRow2.add(rangeFrom);
        ageRow2.add(new JLabel("to:"));
        ageRow2.add(rangeTo);
        ageRow2.add(btnIn);
        ageRow2.add(btnOut);

        panel.add(ageRow1);
        panel.add(ageRow2);

        // --- Борода + волосы ---
        JComboBox<BeardStyle> beardBox = new JComboBox<>(BeardStyle.values());
        JComboBox<HairColor> hairBox = new JComboBox<>(HairColor.values());

        JButton btnBh = new JButton("Подсчёт по бороде И цвету волос");
        btnBh.addActionListener(e -> {
            BeardStyle beard = (BeardStyle) beardBox.getSelectedItem();
            HairColor hair = (HairColor) hairBox.getSelectedItem();
            runCount("beardAndHair(" + beard + "," + hair + ")",
                    VikingLambdaService.beardAndHair(beard, hair));
        });

        JPanel bhRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bhRow.add(new JLabel("Борода:"));
        bhRow.add(beardBox);
        bhRow.add(new JLabel("Волосы:"));
        bhRow.add(hairBox);
        bhRow.add(btnBh);
        panel.add(bhRow);

        // --- Топоры ---
        JButton btn1 = new JButton("Ровно 1 топор");
        btn1.addActionListener(e -> runCount("hasAxes(1)", VikingLambdaService.hasAxes(1)));

        JButton btn2 = new JButton("Ровно 2 топора");
        btn2.addActionListener(e -> runCount("hasAxes(2)", VikingLambdaService.hasAxes(2)));

        JButton btn12 = new JButton("1 или 2 топора");
        btn12.addActionListener(e -> runCount(
                "hasAxes(1).or(hasAxes(2))", VikingLambdaService.hasOneOrTwoAxes()));

        JPanel axeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        axeRow.add(btn1);
        axeRow.add(btn2);
        axeRow.add(btn12);
        panel.add(axeRow);

        return panel;
    }

    private void runCount(String label, Predicate<Viking> predicate) {
        long n = lambdaService.count(predicate);
        log("count[" + label + "] = " + n);
    }

    // =========================================================================
    // Секция 2. Вывод информации
    // =========================================================================

    private JPanel buildInfoSection() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                "2. Вывод данных (filter / sorted / random на лямбдах)"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton btnRandom = new JButton("Случайный викинг ростом > 180");
        btnRandom.addActionListener(e -> {
            Optional<Viking> v = lambdaService.randomVikingTallerThan(180);
            if (v.isPresent()) {
                log("Случайный (рост > 180): " + format(v.get()));
            } else {
                log("Викингов ростом > 180 нет");
            }
        });

        JButton btnLegendary = new JButton("Все викинги с легендарным снаряжением");
        btnLegendary.addActionListener(e -> {
            List<Viking> list = lambdaService.withLegendaryEquipment();
            log("С легендарным снаряжением (" + list.size() + "):");
            list.forEach(v -> log("  - " + format(v)));
        });

        JButton btnRed = new JButton("Рыжебородые, отсортированные по возрасту");
        btnRed.addActionListener(e -> {
            List<Viking> list = lambdaService.redBeardSortedByAge();
            log("Рыжебородые по возрасту (" + list.size() + "):");
            list.forEach(v -> log("  - " + format(v)));
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(btnRandom);
        row.add(btnLegendary);
        row.add(btnRed);
        panel.add(row);
        return panel;
    }

    // =========================================================================
    // Секция 3. Операции с массивом ID
    // =========================================================================

    private JPanel buildIdsSection() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                "3. Операции с массивом ID (Stream<Integer>)"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton btnAll = new JButton("Показать все ID");
        btnAll.addActionListener(e -> {
            List<Integer> ids = lambdaService.allIds();
            log("Все ID (" + ids.size() + "): " + ids);
        });

        JButton btnMax = new JButton("Max ID (последняя запись)");
        btnMax.addActionListener(e -> {
            Optional<Integer> max = lambdaService.maxId();
            log("Max ID = " + max.map(String::valueOf).orElse("(база пуста)"));
        });

        JButton btnEven = new JButton("Все чётные ID");
        btnEven.addActionListener(e -> {
            List<Integer> ids = lambdaService.evenIds();
            log("Чётные ID (" + ids.size() + "): " + ids);
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(btnAll);
        row.add(btnMax);
        row.add(btnEven);
        panel.add(row);
        return panel;
    }

    // =========================================================================
    // Утилиты
    // =========================================================================

    private static String format(Viking v) {
        String equipment = v.equipment().stream()
                .map(it -> it.name() + "[" + it.quality() + "]")
                .collect(Collectors.joining(", "));
        return v.name() + ", " + v.age() + " лет, рост " + v.heightCm()
                + " см, волосы " + v.hairColor()
                + ", борода " + v.beardStyle() + " (" + v.beardColor() + ")"
                + ", снаряжение: " + equipment;
    }

    private void log(String s) {
        output.append(s + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }

    private static JSpinner spinner(int initial, int min, int max) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(initial, min, max, 1));
        s.setPreferredSize(new Dimension(60, 26));
        return s;
    }
}
