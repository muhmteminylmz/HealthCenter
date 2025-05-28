package com.example.health_center.payload.request;

import java.util.List;

public record AllergyIdsRequest(List<Long> allergyIds) { }