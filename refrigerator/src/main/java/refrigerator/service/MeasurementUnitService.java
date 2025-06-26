package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.model.DishIngredient;
import refrigerator.model.MeasurementUnit;
import refrigerator.model.dto.MeasurementUnitDto;
import refrigerator.repositories.MeasurementUnitRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MeasurementUnitService {

    Logger logger = LoggerFactory.getLogger(MeasurementUnitService.class);

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Transactional
    public MeasurementUnit createMeasurementUnit(MeasurementUnit measurementUnit) {
        logger.debug("Creating measurementUnit with id {}", measurementUnit.getId());
        return measurementUnitRepository.save(measurementUnit);
    }

    @Transactional
    public MeasurementUnitDto findMeasurementUnitById(Long id) {
        logger.debug("Find MeasurementUnit with id " + id);
        Optional<MeasurementUnit> measurementUnitIdFromDB = measurementUnitRepository.findById(id);
        if (measurementUnitIdFromDB.isPresent()) {
            MeasurementUnit measurementUnit = measurementUnitIdFromDB.get();
            return convertToDto(measurementUnit);
        } else throw new ResourceNotFoundException("Не найдено");
    }

    @Transactional
    public MeasurementUnitDto findMeasurementUnitByName(String name) {
        logger.debug("Find measurementUnit by name");
        MeasurementUnit byName = measurementUnitRepository.findByName(name);
        if (byName != null) {
            return convertToDto(byName);
        } else throw new ResourceNotFoundException("MeasurementUnit не найдено");
    }

    @Transactional
    public MeasurementUnitDto editMeasurementUnit(Long id, MeasurementUnitDto measurementUnitDto) {
        logger.debug("Edit MeasurementUnit");
        Optional<MeasurementUnit> optionalMeasurementUnit = measurementUnitRepository.findById(id);
        if (optionalMeasurementUnit.isPresent()) {
            MeasurementUnit measurementUnitFromDB = optionalMeasurementUnit.get();
            measurementUnitFromDB.setName(measurementUnitDto.getName());
            measurementUnitFromDB.setAbbreviation(measurementUnitDto.getAbbreviation());
            measurementUnitRepository.save(measurementUnitFromDB);
            return measurementUnitDto;
        } else throw new RuntimeException("measurementUnit не найден");
    }

    @Transactional
    public void deleteMeasurementUnitById(Long id) {
        logger.debug("Delete measurementUnit");
        measurementUnitRepository.deleteById(id);
    }

    public MeasurementUnitDto convertToDto(MeasurementUnit measurementUnit) {
        MeasurementUnitDto measurementUnitDto = new MeasurementUnitDto();
        measurementUnitDto.setName(measurementUnit.getName());
        List<DishIngredient> dishIngredients = measurementUnit.getDishIngredients();
        for (DishIngredient dishIngredient : dishIngredients) {
            measurementUnitDto.setAmount(dishIngredient.getAmount());
        }
        return measurementUnitDto;
    }


}
