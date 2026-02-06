package com.example.ticketingSystem.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueueStatusDto {
    private Long rank;       // 내 앞의 대기 인원 수
    private boolean entered; // 입장 가능 여부
}