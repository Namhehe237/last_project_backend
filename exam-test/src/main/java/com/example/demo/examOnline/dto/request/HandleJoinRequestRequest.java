package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandleJoinRequestRequest {
    private List<Integer> classRequestId;
    private String status; // "APPROVED" or "REJECTED"
} 