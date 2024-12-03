package com.example.playpalgroupservice.repository;

import com.example.playpalgroupservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group,Long> {
}
