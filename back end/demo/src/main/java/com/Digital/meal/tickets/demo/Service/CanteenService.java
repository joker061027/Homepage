package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Entity.*;
import com.Digital.meal.tickets.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CanteenService {

    @Autowired
    private CanteenRepository canteenRepository;

    public List<Canteen> getAllCanteens() {
        return canteenRepository.findAll();
    }

    public Canteen getCanteenById(String id) {
        return canteenRepository.findById(id).orElse(null);
    }

    public Canteen createCanteen(Canteen canteen) {
        return canteenRepository.save(canteen);
    }

    public void deleteCanteen(String id) {
        canteenRepository.deleteById(id);
    }

    public Canteen updateCanteen(Canteen canteen) {
        return canteenRepository.save(canteen);
    }

    // 局部更新
    public Canteen partialUpdateCanteen(String id, Canteen canteen) {
        Canteen existingCanteen = canteenRepository.findById(id).orElse(null);
        if (existingCanteen != null) {
            if (canteen.getCanteenName() != null) {
                existingCanteen.setCanteenName(canteen.getCanteenName());
            }
            if (canteen.getStatus() != 0) { // 假设 0 是一个无效状态
                existingCanteen.setStatus(canteen.getStatus());
            }
            if (canteen.getFkCreator() != null) {
                existingCanteen.setFkCreator(canteen.getFkCreator());
            }
            if (canteen.getCreateDatetime() != null) {
                existingCanteen.setCreateDatetime(canteen.getCreateDatetime());
            }
            if (canteen.getCreateIp() != null) {
                existingCanteen.setCreateIP(canteen.getCreateIp());
            }
            return canteenRepository.save(existingCanteen);
        } else {
            return null; // 或者抛出自定义异常
        }
    }

}