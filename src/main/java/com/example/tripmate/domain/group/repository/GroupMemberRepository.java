package com.example.tripmate.domain.group.repository;

import com.example.tripmate.common.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

}
