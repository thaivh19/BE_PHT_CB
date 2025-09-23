package com.pht.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationRequest {

    private List<String> ids;
    private String operation; // DELETE, UPDATE_STATUS
    private String newStatus; // For UPDATE_STATUS operation
}
