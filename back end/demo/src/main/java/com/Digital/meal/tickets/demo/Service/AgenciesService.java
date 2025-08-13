package com.Digital.meal.tickets.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.Digital.meal.tickets.demo.Entity.Agencies;
import com.Digital.meal.tickets.demo.Repository.AgenciesRepository;

@Service
public class AgenciesService {
    private final AgenciesRepository agenciesRepository;

    // 构造器注入（Spring 推荐）
    public AgenciesService(AgenciesRepository agenciesRepository) {
        this.agenciesRepository = agenciesRepository;
    }

    // 新增/修改
    public Agencies save(Agencies agencies) {
        return agenciesRepository.save(agencies);
    }

    // 删除
    public void delete(String id) {
        agenciesRepository.deleteById(id);
    }

    // 按 ID 查询
    public Optional<Agencies> findById(String id) {
        return agenciesRepository.findById(id);
    }

    // 查询全部
    public List<Agencies> findAll() {
        return agenciesRepository.findAll();
    }

    // 分页查询
    public Page<Agencies> findByPage(int page, int size) {
        return agenciesRepository.findAll(PageRequest.of(page, size));
    }
}