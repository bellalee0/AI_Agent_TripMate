package com.example.tripmate.domain.group.repository;

import com.example.tripmate.common.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByCode(String code);
}
