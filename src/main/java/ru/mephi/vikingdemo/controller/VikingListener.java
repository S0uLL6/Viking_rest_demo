/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.mephi.vikingdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.vikingdemo.gui.VikingDesktopFrame;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.SwingUtilities;

/**
 *
 * @author test2023
 */
@Component
public class VikingListener {
    private VikingService service;
    private VikingDesktopFrame gui;

    @Autowired
    public VikingListener(VikingService service) {
        this.service = service;
    }

    public void setGui(VikingDesktopFrame gui){
        this.gui = gui;
    }

    void testAdd() {
        gui.addNewViking(service.createRandomViking());
    }

    /** REST-операция POST успешна — добавляем новую строку в таблицу. */
    public void onCreated(Viking viking) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.addNewViking(viking));
        }
    }

    /** REST-операция DELETE успешна — удаляем конкретную строку по id. */
    public void onDeleted(int id) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.removeViking(id));
        }
    }

    /** REST-операция PUT успешна — заменяем строку с этим id новыми данными. */
    public void onUpdated(Viking viking) {
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.replaceViking(viking));
        }
    }
}
