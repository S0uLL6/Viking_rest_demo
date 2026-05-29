package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.Viking;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VikingTableModel extends AbstractTableModel {

    private final String[] columns = {"Name", "Age", "Height (cm)", "Hair color", "Beard style", "Equipment"};
    private final List<Viking> data = new ArrayList<>();

    public void addViking(Viking viking) {
        int row = data.size();
        data.add(viking);
        fireTableRowsInserted(row, row);
    }

    public void clear() {
        int size = data.size();
        if (size > 0) {
            data.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }

    /** Удаляет строку с викингом, у которого данный id. */
    public void removeByVikingId(int id) {
        for (int i = 0; i < data.size(); i++) {
            Viking v = data.get(i);
            if (v.id() != null && v.id() == id) {
                data.remove(i);
                fireTableRowsDeleted(i, i);
                return;
            }
        }
    }

    /** Заменяет викинга в таблице (находит по id и подставляет новый объект). */
    public void replaceViking(Viking viking) {
        if (viking.id() == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            Viking v = data.get(i);
            if (v.id() != null && v.id().equals(viking.id())) {
                data.set(i, viking);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Viking viking = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> viking.name();
            case 1 -> viking.age();
            case 2 -> viking.heightCm();
            case 3 -> viking.hairColor();
            case 4 -> viking.beardStyle();
            case 5 -> formatEquipment(viking.equipment());
            default -> "";
        };
    }

    private String formatEquipment(List<EquipmentItem> equipment) {
        return equipment.stream()
                .map(item -> item.name() + " [" + item.quality() + "]")
                .collect(Collectors.joining(", "));
    }
}
