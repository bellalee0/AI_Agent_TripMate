package com.example.tripmate.domain.group.controller;

import static com.example.tripmate.common.enums.SuccessMessage.GROUP_CREATE_SUCCESS;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.dto.CommonResponse;
import com.example.tripmate.domain.group.dto.request.GroupCreateRequest;
import com.example.tripmate.domain.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
@Tag(name = "Group")
public class GroupController {

    private final GroupService groupService;

    /**
     * 그룹 생성
     */
    @Operation(
        summary = "그룹 생성",
        description = """
                    새로운 그룹을 생성합니다.
                    """
    )
    @PostMapping
    public ResponseEntity<CommonResponse<Void>> createGroup(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody GroupCreateRequest request
    ) {

        groupService.createGroup(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(GROUP_CREATE_SUCCESS));
    }
}
