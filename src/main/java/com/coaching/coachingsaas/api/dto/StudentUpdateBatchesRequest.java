package com.coaching.coachingsaas.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class StudentUpdateBatchesRequest {
    @NotEmpty public List<Long> batchIds;
}