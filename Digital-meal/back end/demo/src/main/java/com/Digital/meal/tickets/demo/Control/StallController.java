package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Common.Result;
import com.Digital.meal.tickets.demo.Entity.*;
import com.Digital.meal.tickets.demo.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stall")
public class StallController {

    @Autowired
    private StallService stallService;

    @GetMapping
    public List<Stall> getAllStalls() {
        return stallService.getAllStalls();
    }

    @GetMapping("/{id}")
    public Stall getStall(@PathVariable String id) {
        return stallService.getStallById(id);
    }

    @PostMapping
    public Stall createStall(@RequestBody Stall stall) {
        return stallService.createStall(stall);
    }

    @PutMapping
    public Stall updateStall(@RequestBody Stall stall) {
        return stallService.updateStall(stall);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Stall> updateStall(@PathVariable String id, @RequestBody Stall stall) {
        Stall updatedStall = stallService.partialUpdateStall(id, stall);
        return ResponseEntity.ok(updatedStall);
    }

    @DeleteMapping("/{id}")
    public void deleteStall(@PathVariable String id) {
        stallService.deleteStall(id);
    }

    @GetMapping("/canteen/{canteenId}")
    public ResponseEntity<List<Stall>> getStallsByCanteenId(@PathVariable String canteenId) {
        List<Stall> stalls = stallService.getStallsByCanteenId(canteenId);
        return ResponseEntity.ok(stalls);
    }

}
