package com.trafigura.equityapi.service;

import com.trafigura.equityapi.model.Transaction;

import java.util.Map;

public interface PositionService {
    Map<String, Integer> getPositions();
}
