package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.Exceptions.NotFoundException;
import refrigerator.model.MeasurementUnit;
import refrigerator.repositories.MeasurementUnitRepository;
import refrigerator.service.MeasurementUnitService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeasurementUnitTest {

    @Mock
    MeasurementUnitRepository measurementUnitRepository;

    @InjectMocks
    MeasurementUnitService measurementUnitService;

    @Test
    void createMeasurementUnit_ShouldReturnSavedMeasurementUnit() {
        //Arrange
        MeasurementUnit measurementUnit = new MeasurementUnit();
        when(measurementUnitRepository.save(measurementUnit)).thenReturn(measurementUnit);

        //Act
        MeasurementUnit result = measurementUnitService.createMeasurementUnit(measurementUnit);

        //Assert
        assertNotNull(result);
        assertEquals(measurementUnit, result);
        verify(measurementUnitRepository, times(1)).save(measurementUnit);
    }

    @Test
    void findMeasurementUnitById_WhenExists_ShouldReturnMeasurementUnit() {
        //Arrange
        Long id = 1L;
        MeasurementUnit measurementUnit = new MeasurementUnit();
        measurementUnit.setId(id);
        when(measurementUnitRepository.findById(id)).thenReturn(Optional.of(measurementUnit));

        //Act
        MeasurementUnit result = measurementUnitService.findMeasurementUnitById(id);

        //Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(measurementUnitRepository, times(1)).findById(id);
    }

    @Test
    void findMeasurementUnitById_WhenNotExists_ShouldThrowException() {
        //Arrange
        Long id = 999L;
        when(measurementUnitRepository.findById(id)).thenReturn(Optional.empty());

        //Act && Assert
        assertThrows(NotFoundException.class, () -> measurementUnitService.findMeasurementUnitById(id));
        verify(measurementUnitRepository, times(1)).findById(id);
    }

    @Test
    void findMeasurementUnitByName_WhenExists_ShouldReturnMeasurementUnit() {
        //Arrange
        String name = "Test name";
        MeasurementUnit measurementUnit = new MeasurementUnit();
        measurementUnit.setName(name);
        when(measurementUnitRepository.findByName(name)).thenReturn(measurementUnit);

        //Act
        MeasurementUnit result = measurementUnitService.findMeasurementUnitByName(name);

        //Assert
        assertNotNull(result);
        assertEquals("Test name", result.getName());
        verify(measurementUnitRepository, times(1)).findByName(name);
    }

    @Test
    void editMeasurementUnit_WhenExists_ShouldReturnUpdatedIngredient() {
        //Arrange
        MeasurementUnit measurementUnit = new MeasurementUnit();
        measurementUnit.setId(1L);
        measurementUnit.setName("test name");
        when(measurementUnitRepository.findById(measurementUnit.getId())).thenReturn(Optional.of(measurementUnit));
        when(measurementUnitRepository.save(measurementUnit)).thenReturn(measurementUnit);


        //Act
        MeasurementUnit result = measurementUnitService.editMeasurementUnit(measurementUnit);

        //Assert
        assertNotNull(result);
        assertEquals("test name", result.getName());
        verify(measurementUnitRepository, times(1)).save(measurementUnit);
        verify(measurementUnitRepository, times(1)).findById(result.getId());
    }

    @Test
    void editMeasurementUnit_WhenNotExists_ShouldThrowException() {
        //Arrange
        MeasurementUnit measurementUnit = new MeasurementUnit();
        measurementUnit.setId(999L);
        when(measurementUnitRepository.findById(measurementUnit.getId())).thenReturn(Optional.empty());

        //Act && Assert
        assertThrows(RuntimeException.class, () -> measurementUnitService.editMeasurementUnit(measurementUnit));
        verify(measurementUnitRepository, times(1)).findById(measurementUnit.getId());
        verify(measurementUnitRepository, never()).save(any());
    }

    @Test
    void deleteMeasurementUnitById() {
        //Arrange
        Long id = 1L;
        doNothing().when(measurementUnitRepository).deleteById(id);

        //Act
        measurementUnitService.deleteMeasurementUnitById(id);

        //Assert
        verify(measurementUnitRepository, times(1)).deleteById(id);

    }




}
