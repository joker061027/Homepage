package com.Digital.meal.tickets.demo.Control;

import com.Digital.meal.tickets.demo.Entity.Agencies;
import com.Digital.meal.tickets.demo.Service.AgenciesService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agencies")
public class AgenciesController {
    private final AgenciesService agenciesService;

    public AgenciesController(AgenciesService agenciesService) {
        this.agenciesService = agenciesService;
    }

    // 新增/修改：POST
    @PostMapping
    public Agencies save(@RequestBody Agencies agencies) {
        return agenciesService.save(agencies);
    }

    // 删除：DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        agenciesService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 按 ID 查询：GET
    @GetMapping("/{id}")
    public ResponseEntity<Agencies> findById(@PathVariable String id) {
        Optional<Agencies> agencies = agenciesService.findById(id);
        return agencies.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 查询全部：GET
    @GetMapping
    public List<Agencies> findAll() {
        return agenciesService.findAll();
    }

    // 分页查询：GET
    @GetMapping("/page")
    public Page<Agencies> findByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return agenciesService.findByPage(page, size);
    }
}