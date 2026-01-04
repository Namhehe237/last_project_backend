package com.example.demo.examOnline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassActiveStatusRequest {
    private Boolean isActive;
}

