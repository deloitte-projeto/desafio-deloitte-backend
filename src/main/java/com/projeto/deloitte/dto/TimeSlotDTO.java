package com.projeto.deloitte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 