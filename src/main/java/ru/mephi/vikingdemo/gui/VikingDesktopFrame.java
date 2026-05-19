package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;


public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService) {
        this.vikingService = vikingService;

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

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        onInit();
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }
    
    public void addNewViking(Viking viking){
        tableModel.addViking(viking);
    }

    /** Перечитать всех викингов из БД и перерисовать таблицу.
     *  Вызывается из VikingListener после операций POST/PUT/DELETE через REST. */
    public void refreshFromDb() {
        // Гарантируем что обновление JTable идёт в EDT (Swing-потоке),
        // даже если refresh вызван из не-Swing потока (Tomcat worker).
        SwingUtilities.invokeLater(() -> {
            tableModel.clear();
            for (Viking viking : vikingService.findAll()) {
                tableModel.addViking(viking);
            }
        });
    }

    private void onInit() {
        List<Viking> all = vikingService.findAll();
        if (!all.isEmpty()){
            for (Viking viking : all) {
                tableModel.addViking(viking);
            }
        }
    }
}
