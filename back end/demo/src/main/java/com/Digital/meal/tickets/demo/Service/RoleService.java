package com.Digital.meal.tickets.demo.Service;

import com.Digital.meal.tickets.demo.Entity.Role;
import com.Digital.meal.tickets.demo.Repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public void delete(String id) {
        roleRepository.deleteById(id);
    }

    public Optional<Role> findById(String id) {
        return roleRepository.findById(id);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}