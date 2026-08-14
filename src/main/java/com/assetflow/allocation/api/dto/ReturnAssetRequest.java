package com.assetflow.allocation.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ReturnAssetRequest {

    @NotBlank(message = "Return condition state is required")
    private String returnConditionState;

    public ReturnAssetRequest() {
    }

    public ReturnAssetRequest(String returnConditionState) {
        this.returnConditionState = returnConditionState;
    }

    public String getReturnConditionState() {
        return returnConditionState;
    }

    public void setReturnConditionState(String returnConditionState) {
        this.returnConditionState = returnConditionState;
    }
}
