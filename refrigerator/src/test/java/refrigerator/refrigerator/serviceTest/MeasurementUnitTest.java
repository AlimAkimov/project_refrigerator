package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.model.MeasurementUnit;
import refrigerator.model.dto.MeasurementUnitDto;
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
        MeasurementUnit measurementUnit = new MeasurementUnit();
        when(measurementUnitRepository.save(measurementUnit)).thenReturn(measurementUnit);

        MeasurementUnit result = measurementUnitService.createMeasurementUnit(measurementUnit);

        assertNotNull(result);
        assertEquals(measurementUnit, result);
        verify(measurementUnitRepository, times(1)).save(measurementUnit);
    }

    @Test
    void findMeasurementUnitById_WhenExists_ShouldReturnDto() {
        Long id = 1L;
        String unitName = "Kilogram";

        MeasurementUnit mockUnit = new MeasurementUnit();
        mockUnit.setId(id);
        mockUnit.setName(unitName);

        when(measurementUnitRepository.findById(id))
                .thenReturn(Optional.of(mockUnit));

        MeasurementUnitDto result = measurementUnitService.findMeasurementUnitById(id);

        assertNotNull(result);
        assertEquals(unitName, result.getName());
        verify(measurementUnitRepository, times(1)).findById(id);
    }

    @Test
    void findMeasurementUnitById_WhenNotExists_ShouldThrowException() {
        Long id = 999L;
        when(measurementUnitRepository.findById(id))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            measurementUnitService.findMeasurementUnitById(id);
        });
        verify(measurementUnitRepository, times(1)).findById(id);
    }

    @Test
    void findMeasurementUnitByName_WhenExists_ReturnsDto() {
        String name = "Liter";
        MeasurementUnit mockUnit = new MeasurementUnit();
        mockUnit.setName(name);
        mockUnit.setAbbreviation("L");

        when(measurementUnitRepository.findByName(name)).thenReturn(mockUnit);

        MeasurementUnitDto result = measurementUnitService.findMeasurementUnitByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(measurementUnitRepository).findByName(name);
    }

    @Test
    void editMeasurementUnit_WhenExists_ShouldUpdateAndReturnDto() {
        Long id = 1L;
        MeasurementUnitDto inputDto = new MeasurementUnitDto();
        inputDto.setName("Updated Kilogram");
        inputDto.setAbbreviation("kg-upd");

        MeasurementUnit existingUnit = new MeasurementUnit();
        existingUnit.setId(id);
        existingUnit.setName("Kilogram");
        existingUnit.setAbbreviation("kg");

        when(measurementUnitRepository.findById(id))
                .thenReturn(Optional.of(existingUnit));
        when(measurementUnitRepository.save(any(MeasurementUnit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MeasurementUnitDto result = measurementUnitService.editMeasurementUnit(id, inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getName(), result.getName());
        assertEquals(inputDto.getAbbreviation(), result.getAbbreviation());

        verify(measurementUnitRepository).findById(id);
        verify(measurementUnitRepository).save(existingUnit);

        assertEquals("Updated Kilogram", existingUnit.getName());
        assertEquals("kg-upd", existingUnit.getAbbreviation());
    }

    @Test
    void editMeasurementUnit_WhenNotExists_ShouldThrowException() {
        Long id = 999L;
        MeasurementUnitDto inputDto = new MeasurementUnitDto();

        when(measurementUnitRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            measurementUnitService.editMeasurementUnit(id, inputDto);
        });

        verify(measurementUnitRepository).findById(id);
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
