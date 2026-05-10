package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mephi.vikingdemo.repository.VikingStorage;

@Service
public class VikingService {

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;


    @Autowired
    public VikingService(
            VikingFactory vikingFactory,
            VikingStorage vikingStorage
    ) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
    }

    public List<Viking> findAll() {
        return vikingStorage.findAll();
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        return vikingStorage.save(viking);
    }

    /**
     * Сохраняет конкретного викинга, переданного клиентом.
     */
    public Viking create(Viking viking) {
        return vikingStorage.save(viking);
    }

    /**
     * Полностью перезаписывает параметры викинга по id.
     */
    public Viking update(int id, Viking viking) {
        return vikingStorage.update(id, viking);
    }

    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }
}
