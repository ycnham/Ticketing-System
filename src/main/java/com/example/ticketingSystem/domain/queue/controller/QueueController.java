package com.example.ticketingSystem.domain.queue.controller;

import com.example.ticketingSystem.domain.queue.service.QueueService;
import com.example.ticketingSystem.domain.queue.dto.QueueStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    // 1. 대기열 진입
    @PostMapping("/events/{eventId}/users/{userId}")
    public ResponseEntity<String> enterQueue(@PathVariable String eventId, @PathVariable Long userId) {
        queueService.enterQueue(eventId, userId);
        return ResponseEntity.ok("대기열에 진입했습니다.");
    }

    // 2. 내 순서 확인 (프론트엔드에서 3초마다 폴링)
    @GetMapping("/events/{eventId}/users/{userId}")
    public ResponseEntity<QueueStatusDto> getQueueStatus(@PathVariable String eventId, @PathVariable Long userId) {
        QueueStatusDto status = queueService.getQueueStatus(eventId, userId);
        return ResponseEntity.ok(status);
    }
}