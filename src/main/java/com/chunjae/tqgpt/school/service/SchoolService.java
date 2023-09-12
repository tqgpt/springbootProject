package com.chunjae.tqgpt.school.service;

import com.chunjae.tqgpt.school.entity.School;
import com.chunjae.tqgpt.school.repository.SchoolRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
@AllArgsConstructor
public class SchoolService implements SchoolServiceImpl {

    private final SchoolRepository schoolRepository;

    @Override
    public List<School> getTop10Schools() {
        Pageable pageable = PageRequest.of(0, 10);
        return schoolRepository.findAll(pageable).getContent();
    }
}
