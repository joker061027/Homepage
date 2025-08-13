package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.Canteen;
import com.Digital.meal.tickets.demo.Service.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/canteen")
public class CanteenController {

    @Autowired
    private CanteenService canteenService;

    // 获取所有食堂
    @GetMapping
    public List<Canteen> getAllCanteens() {
        return canteenService.getAllCanteens();
    }

    // 根据ID获取食堂
    @GetMapping("/{id}")
    public Canteen getCanteen(@PathVariable String id) {
        return canteenService.getCanteenById(id);
    }

    // 创建食堂
    @PostMapping
    public Canteen createCanteen(@RequestBody Canteen canteen) {
        return canteenService.createCanteen(canteen);
    }

    // 全量更新食堂
    @PutMapping
    public Canteen updateCanteen(@RequestBody Canteen canteen) {
        return canteenService.updateCanteen(canteen);
    }

    // 局部更新食堂
    @PatchMapping("/{id}")
    public Canteen partialUpdateCanteen(@PathVariable String id, @RequestBody Canteen canteen) {
        return canteenService.partialUpdateCanteen(id, canteen);
    }

    // 删除食堂
    @DeleteMapping("/{id}")
    public void deleteCanteen(@PathVariable String id) {
        canteenService.deleteCanteen(id);
    }

    // 根据餐票类型ID查询关联的食堂
    @GetMapping("/ticket-type/{typeId}")
    public ResponseEntity<List<Canteen>> getCanteensByTicketTypeId(@PathVariable String typeId) {
        List<Canteen> canteens = canteenService.getCanteensByTicketTypeId(typeId);
        return ResponseEntity.ok(canteens);
    }
}
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

