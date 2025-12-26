package com.elderlycare.management.service;

import com.elderlycare.management.dto.DataAnalysisResponse;

public interface DataAnalysisService {
    /**
     * 获取老人分析数据
     */
    DataAnalysisResponse.ElderAnalysisData getElderAnalysisData();

    /**
     * 获取营收分析数据
     */
    DataAnalysisResponse.RevenueAnalysisData getRevenueAnalysisData();

    /**
     * 获取运营分析数据
     */
    DataAnalysisResponse.OperationAnalysisData getOperationAnalysisData();

    /**
     * 获取完整的数据分析响应
     */
    DataAnalysisResponse getAllAnalysisData();
}
