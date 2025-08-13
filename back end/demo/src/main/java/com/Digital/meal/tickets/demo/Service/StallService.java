package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Entity.*;
import com.Digital.meal.tickets.demo.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StallService {

    @Autowired
    private StallRepository stallRepository;

    public List<Stall> getAllStalls() {
        return stallRepository.findAll();
    }

    public Stall getStallById(String id) {
        return stallRepository.findById(id).orElse(null);
    }

    public Stall createStall(Stall stall) {
        return stallRepository.save(stall);
    }

    public void deleteStall(String id) {
        stallRepository.deleteById(id);
    }

    public Stall updateStall(Stall stall) {
        return stallRepository.save(stall);
    }

    // 局部更新
    public Stall partialUpdateStall(String id, Stall stall) {
        // 查找现有的 Stall
        Stall existingStall = stallRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stall not found with id: " + id));

        // 根据提供的部分数据进行更新
        if (stall.getStallName() != null) {
            existingStall.setStallName(stall.getStallName());
        }
        if (stall.getCanteenId() != null) {
            existingStall.setCanteenId(stall.getCanteenId());
        }
        if (stall.getStatue() != 0) { // 根据业务逻辑，决定是否更新 statue
            existingStall.setStatue(stall.getStatue());
        }
        if (stall.getFkCreator() != null) {
            existingStall.setFkCreator(stall.getFkCreator());
        }
        if (stall.getCreateDatetime() != null) {
            existingStall.setCreateDatetime(stall.getCreateDatetime());
        }
        if (stall.getCreateIp() != null) {
            existingStall.setCreateIp(stall.getCreateIp());
        }
        if (stall.getStallLocation() != null) {
            existingStall.setStallLocation(stall.getStallLocation());
        }

        // 保存更新后的 Stall
        return stallRepository.save(existingStall);
    }

    
     public List<Stall> getStallsByCanteenId(String canteenId) {
        return stallRepository.findByCanteenId(canteenId);
    }

}
