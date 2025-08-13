package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.*;
import com.Digital.meal.tickets.demo.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/canteen")
public class CanteenController {

    @Autowired
    private CanteenService canteenService;

    @GetMapping
    public List<Canteen> getAllCanteens() {
        return canteenService.getAllCanteens();
    }

    @GetMapping("/{id}")
    public Canteen getCanteen(@PathVariable String id) {
        return canteenService.getCanteenById(id);
    }

    @PostMapping
    public Canteen createCanteen(@RequestBody Canteen canteen) {
        return canteenService.createCanteen(canteen);
    }

    @PutMapping
    public Canteen updateCanteen(@RequestBody Canteen canteen) {
        return canteenService.updateCanteen(canteen);
    }

    @PatchMapping("/{id}")
    public Canteen partialUpdateCanteen(@PathVariable String id, @RequestBody Canteen canteen) {
        return canteenService.partialUpdateCanteen(id, canteen);
    }

    @DeleteMapping("/{id}")
    public void deleteCanteen(@PathVariable String id) {
        canteenService.deleteCanteen(id);
    }
}

