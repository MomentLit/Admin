package com.example.admin.controller;

import com.example.admin.dto.request.AdminSpaceRequest;
import com.example.admin.dto.response.AdminSpaceResponse;
import com.example.admin.global.dto.ApiResponse;
import com.example.admin.global.util.ResponseUtil;
import com.example.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/spaces")
public class AdminController {
    private final AdminService adminService;

    @PatchMapping("/{space-id}/approve")
    public ResponseEntity<ApiResponse<AdminSpaceResponse>> spaceApprove(
            @PathVariable("space-id") Long spaceId, AdminSpaceRequest request
    ) {
        AdminSpaceResponse response = adminService.adminSpaceApprove(spaceId,request);
        ApiResponse<AdminSpaceResponse> apiResponse =
                ResponseUtil.success("space approve",response);
        return ResponseEntity.status(202).body(apiResponse);

    }
    @PatchMapping("/{space-id}/reject")
    public ResponseEntity<ApiResponse<AdminSpaceResponse>> spaceReject(
            @PathVariable("space-id") Long spaceId, AdminSpaceRequest request
    ) {
        AdminSpaceResponse response = adminService.adminSpaceReject(spaceId,request);
        ApiResponse<AdminSpaceResponse> apiResponse =
                ResponseUtil.success("space approve",response);
        return ResponseEntity.status(202).body(apiResponse);

    }
}
