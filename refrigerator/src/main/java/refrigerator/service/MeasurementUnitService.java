package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.model.MeasurementUnit;
import refrigerator.repositories.MeasurementUnitRepository;

import java.util.Optional;

@Service
public class MeasurementUnitService {

    Logger logger = LoggerFactory.getLogger(MeasurementUnitService.class);

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    public MeasurementUnit createMeasurementUnit(MeasurementUnit measurementUnit) {
        logger.debug("Creating measurementUnit with id {}", measurementUnit.getId());
        return measurementUnitRepository.save(measurementUnit);
    }

    public MeasurementUnit findMeasurementUnitById(Long id) {
        logger.debug("Find MeasurementUnit with id " + id);
        Optional<MeasurementUnit> measurementUnitIdFromDB = measurementUnitRepository.findById(id);
        if (measurementUnitIdFromDB.isPresent()) {
            return measurementUnitIdFromDB.get();
        } else throw new ResourceNotFoundException("Не найдено");
    }

    public MeasurementUnit findMeasurementUnitByName(String name) {
        logger.debug("Find measurementUnit by name");
        MeasurementUnit byName = measurementUnitRepository.findByName(name);
        if (byName != null) {
            return byName;
        } else throw new ResourceNotFoundException("MeasurementUnit не найдено");

    }

    public MeasurementUnit editMeasurementUnit(MeasurementUnit measurementUnit) {
        logger.debug("Edit MeasurementUnit");
        Optional<MeasurementUnit> measurementUnitFromDB = measurementUnitRepository.findById(measurementUnit.getId());
        if (measurementUnitFromDB.isPresent()) {
            return measurementUnitRepository.save(measurementUnit);
        } else throw new RuntimeException("measurementUnit не найден");
    }

    public void deleteMeasurementUnitById(Long id) {
        logger.debug("Delete measurementUnit");
        measurementUnitRepository.deleteById(id);
    }

}
