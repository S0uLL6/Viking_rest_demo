package ru.mephi.vikingdemo.repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.EquipmentItemEntity;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;


@Repository
public class VikingStorage {

    private final VikingRepository vikingRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final VikingMapper vikingMapper;

    public VikingStorage(
            VikingRepository vikingRepository,
            EquipmentItemRepository equipmentItemRepository,
            VikingMapper vikingMapper
    ) {
        this.vikingRepository = vikingRepository;
        this.equipmentItemRepository = equipmentItemRepository;
        this.vikingMapper = vikingMapper;
    }

    @Transactional
    public Viking save(Viking viking) {
        Integer vikingId = vikingRepository.save(
                vikingMapper.toVikingEntity(viking)
        );

        List<EquipmentItem> equipment = viking.equipment() != null
                ? viking.equipment()
                : List.of();

        for (EquipmentItem item : equipment) {
            equipmentItemRepository.save(
                    vikingMapper.toEquipmentItemEntity(vikingId, item)
            );
        }

        // возвращаем викинга с проставленным id, чтобы клиент знал, что вставилось
        return new Viking(
                vikingId,
                viking.name(),
                viking.age(),
                viking.heightCm(),
                viking.hairColor(),
                viking.beardStyle(),
                equipment
        );
    }

    /**
     * Полная перезапись викинга по id: обновляем поля, удаляем все его equipment_items
     * и вставляем заново. Возвращает обновлённого викинга.
     *
     * @throws NoSuchElementException если викинга с таким id нет
     */
    @Transactional
    public Viking update(int id, Viking viking) {
        int rowsAffected = vikingRepository.update(
                id,
                vikingMapper.toVikingEntity(viking)
        );

        if (rowsAffected == 0) {
            throw new NoSuchElementException("Викинг с id=" + id + " не найден");
        }

        equipmentItemRepository.deleteByVikingId(id);

        List<EquipmentItem> equipment = viking.equipment() != null
                ? viking.equipment()
                : List.of();

        for (EquipmentItem item : equipment) {
            equipmentItemRepository.save(
                    vikingMapper.toEquipmentItemEntity(id, item)
            );
        }

        return new Viking(
                id,
                viking.name(),
                viking.age(),
                viking.heightCm(),
                viking.hairColor(),
                viking.beardStyle(),
                equipment
        );
    }

    public List<Viking> findAll() {
        List<VikingEntity> vikingEntities = vikingRepository.findAll();
        List<EquipmentItemEntity> equipmentEntities = equipmentItemRepository.findAll();

        Map<Integer, List<EquipmentItemEntity>> equipmentByVikingId = equipmentEntities.stream()
                .collect(Collectors.groupingBy(EquipmentItemEntity::vikingId));

        return vikingEntities.stream()
                .map(vikingEntity -> vikingMapper.toViking(
                        vikingEntity,
                        equipmentByVikingId.getOrDefault(vikingEntity.id(), List.of())
                ))
                .toList();
    }

    @Transactional
    public void deleteById(int id) {
        // equipment_items удалятся каскадом (FK с on delete cascade)
        vikingRepository.deleteById(id);
    }
}
