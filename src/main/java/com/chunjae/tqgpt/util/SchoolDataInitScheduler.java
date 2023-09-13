package com.chunjae.tqgpt.util;

import com.chunjae.tqgpt.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchoolDataInitScheduler {

    private final SchoolService schoolService;

    @Scheduled(cron = "50 10 13 9 * *") // 매년 1월 1일에 실행됨.
    public void scheduleTaskUsingCronExpression() {
        log.info("API 공식 데이터 초기화 스케줄러 실행됨.");
        long startTime = System.currentTimeMillis(); // Start time
        schoolService.upsertSchoolData("user1");
        long endTime = System.currentTimeMillis(); // End time

        long elapsedTime = endTime - startTime;
        log.info("API 공식 데이터 초기화 스케줄러 적용 됨 - 소요시간: " + elapsedTime);
    }
}
