package com.example.tripmate.domain.group.service;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.entity.Group;
import com.example.tripmate.common.entity.GroupMember;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.enums.GroupMemberRole;
import com.example.tripmate.common.utils.GroupCodeGenerator;
import com.example.tripmate.domain.group.dto.request.GroupCreateRequest;
import com.example.tripmate.domain.group.repository.GroupMemberRepository;
import com.example.tripmate.domain.group.repository.GroupRepository;
import com.example.tripmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 그룹 생성
     */
    @Transactional
    public void createGroup(AuthUser authUser, GroupCreateRequest request) {

        User user = userRepository.findActiveUserById(authUser.getId());

        String code = GroupCodeGenerator.generate67BillionCode();

        while (groupRepository.existsByCode(code)) {
            code = GroupCodeGenerator.generate67BillionCode();
        }

        Group group = new Group(request.getName(), code, request.getPassword());
        groupRepository.saveAndFlush(group);

        GroupMember groupMember = new GroupMember(group, user, GroupMemberRole.OWNER);
        groupMemberRepository.saveAndFlush(groupMember);
    }
}
