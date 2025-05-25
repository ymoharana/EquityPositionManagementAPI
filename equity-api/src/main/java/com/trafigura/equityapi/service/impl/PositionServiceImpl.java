package com.trafigura.equityapi.service.impl;

import com.trafigura.equityapi.cache.PositionStateCache;
import com.trafigura.equityapi.service.PositionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionServiceImpl implements PositionService {

    private final PositionStateCache positionStateCache;

    @Override
    @Transactional
    public Map<String, Integer> getPositions() {
        return positionStateCache.getAllPositions();
    }

}
