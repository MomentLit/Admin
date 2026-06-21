package com.example.admin.service;

import com.example.admin.dto.request.AdminSpaceRequest;
import com.example.admin.dto.response.AdminSpaceResponse;
import com.example.admin.entity.ApprovalStatus;
import com.example.admin.entity.Role;
import com.example.admin.entity.Space;
import com.example.admin.global.exception.ForbiddenException;
import com.example.admin.global.exception.PreconditionFailedException;
import com.example.admin.repository.SpaceRepository;
import com.example.admin.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public AdminSpaceResponse adminSpaceApprove(
            Long spaceId, AdminSpaceRequest request
    ){
        if (!request.role().equals(Role.ADMIN)){
            throw new ForbiddenException("관리자만 가능합니다");
        }
        Space space = spaceRepository.findSpaceById(spaceId);
        if (!space.getAdminStatus().equals(ApprovalStatus.PENDING)){
            throw new PreconditionFailedException("이미 승인됐거나 거절되었습니다");
        }
        space.update(ApprovalStatus.APPROVED);
        return AdminSpaceResponse.from(spaceId);
    }

    @Transactional
    public AdminSpaceResponse adminSpaceReject(
            Long spaceId, AdminSpaceRequest request
    ){
        if (!request.role().equals(Role.ADMIN)){
            throw new ForbiddenException("관리자만 가능합니다");
        }
        Space space = spaceRepository.findSpaceById(spaceId);
        if (!space.getAdminStatus().equals(ApprovalStatus.PENDING)){
            throw new PreconditionFailedException("이미 승인됐거나 거절되었습니다");
        }
        space.update(ApprovalStatus.REJECTED);
        return AdminSpaceResponse.from(spaceId);
    }
}
