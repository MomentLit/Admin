package com.example.admin.service;

import com.example.admin.dto.response.AdminSpaceResponse;
import com.example.admin.entity.ApprovalStatus;
import com.example.admin.entity.Role;
import com.example.admin.entity.Space;
import com.example.admin.global.exception.ForbiddenException;
import com.example.admin.global.exception.PreconditionFailedException;
import com.example.admin.global.exception.SpaceNotFoundException;
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
    public synchronized AdminSpaceResponse adminSpaceApprove(
            Long spaceId, String authenticatedRole
    ){
        if (!Role.ADMIN.name().equals(authenticatedRole)){
            throw new ForbiddenException("관리자만 가능합니다");
        }
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(()->new SpaceNotFoundException("공간을 찾을 수 없습니다"));
        if (!space.getAdminStatus().equals(ApprovalStatus.PENDING)){
            throw new PreconditionFailedException("이미 승인됐거나 거절되었습니다");
        }
        space.update(ApprovalStatus.APPROVED);
        return AdminSpaceResponse.from(spaceId);
    }

    @Transactional
    public synchronized AdminSpaceResponse adminSpaceReject(
            Long spaceId, String authenticatedRole
    ){
        if (!authenticatedRole.equals(Role.ADMIN.name())){
            throw new ForbiddenException("관리자만 가능합니다");
        }
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(()->new SpaceNotFoundException("공간을 찾을 수 없습니다"));
        if (!space.getAdminStatus().equals(ApprovalStatus.PENDING)){
            throw new PreconditionFailedException("이미 승인됐거나 거절되었습니다");
        }
        space.update(ApprovalStatus.REJECTED);
        return AdminSpaceResponse.from(spaceId);
    }
}
