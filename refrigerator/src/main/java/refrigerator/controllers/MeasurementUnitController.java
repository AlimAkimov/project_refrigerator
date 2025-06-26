package refrigerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refrigerator.model.MeasurementUnit;
import refrigerator.model.dto.MeasurementUnitDto;
import refrigerator.service.MeasurementUnitService;

@RestController
@RequestMapping(path = "/measurement-unit")
public class MeasurementUnitController {

    @Autowired
    private MeasurementUnitService measurementUnitService;

    @PostMapping(path = "/create")
    public ResponseEntity<MeasurementUnit> createMeasurementUnit(@RequestBody MeasurementUnit measurementUnit) {
        return ResponseEntity.ok(measurementUnitService.createMeasurementUnit(measurementUnit));
    }

    @GetMapping(path = "/findByid/{id}")
    public ResponseEntity<MeasurementUnitDto> findMeasurementUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(measurementUnitService.findMeasurementUnitById(id));
    }

    @GetMapping(path = "/findByName/{name}")
    public ResponseEntity<MeasurementUnitDto> findMeasurementUnitByName(@PathVariable String name) {
        return ResponseEntity.ok(measurementUnitService.findMeasurementUnitByName(name));
    }

    @PutMapping(path = "/edit/{id}")
    public ResponseEntity<MeasurementUnitDto> editMeasurementUnit(
            @PathVariable Long id,
            @RequestBody MeasurementUnitDto measurementUnitDto) {
        return ResponseEntity.ok(measurementUnitService.editMeasurementUnit(id, measurementUnitDto));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<MeasurementUnit> deleteMeasurementUnit(@PathVariable Long id) {
        measurementUnitService.deleteMeasurementUnitById(id);
        return ResponseEntity.ok().build();
    }

}
