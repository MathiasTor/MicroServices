package com.example.playpalgroupservice.repository;

import com.example.playpalgroupservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Long> {
    List<Group> findAllByUserIdsContains(Long userId);
}
