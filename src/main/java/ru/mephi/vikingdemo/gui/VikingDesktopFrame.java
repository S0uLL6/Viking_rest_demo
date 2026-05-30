package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;


public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingLambdaService lambdaService;
    private final VikingTableModel tableModel = new VikingTableModel();

    /** Создаётся лениво при первом нажатии кнопки. */
    private VikingLambdaFrame lambdaFrame;

    public VikingDesktopFrame(VikingService vikingService, VikingLambdaService lambdaService) {
        this.vikingService = vikingService;
        this.lambdaService = lambdaService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton create50Button = new JButton("Create 50 vikings");
        create50Button.addActionListener(event -> onCreate50Vikings());

        JButton lambdaButton = new JButton("Открыть лямбда-сервис");
        lambdaButton.addActionListener(event -> openLambdaFrame());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(create50Button);
        bottomPanel.add(lambdaButton);
        add(bottomPanel, BorderLayout.SOUTH);

        onInit();
    }

    private void onCreate50Vikings() {
        for (int i = 0; i < 50; i++) {
            Viking viking = vikingService.createRandomViking();
            tableModel.addViking(viking);
        }
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    private void openLambdaFrame() {
        if (lambdaFrame == null) {
            lambdaFrame = new VikingLambdaFrame(lambdaService);
        }
        lambdaFrame.setVisible(true);
        lambdaFrame.toFront();
    }

    private void onInit() {
        List<Viking> all = vikingService.findAll();
        if (!all.isEmpty()) {
            for (Viking viking : all) {
                tableModel.addViking(viking);
            }
        }
    }
}
