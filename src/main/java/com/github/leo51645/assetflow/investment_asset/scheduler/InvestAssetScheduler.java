package com.github.leo51645.assetflow.investment_asset.scheduler;

import com.github.leo51645.assetflow.investment_asset.domain.entity.InvestAssetEntity;
import com.github.leo51645.assetflow.investment_asset.service.InvestAssetService;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.service.YahooFinanceChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvestAssetScheduler {

    private final InvestAssetService investAssetService;
    private final YahooFinanceChartService yahooFinanceChartService;

    @Scheduled(fixedRate = 60_000)
    public void updateMarketData() {
        LocalDateTime maxUpdatedBefore = LocalDateTime.now().minusMinutes(10);

        List<InvestAssetEntity> outdatedAssets = investAssetService.getAllOutdatedInvestAssets(maxUpdatedBefore);

        if (outdatedAssets.isEmpty()) {
            return;
        }

        for (InvestAssetEntity investAsset : outdatedAssets) {
            String symbol = investAsset.getSymbol();

            HttpResponse<String> httpResponse = yahooFinanceChartService.getHttpResponse(symbol);
            List<MarketDataYahooChartResponseDto> responseDtoList = yahooFinanceChartService.parseResponse(httpResponse.body(), symbol);

            investAsset.setCurrentPrice(responseDtoList.getFirst().getCurrentPrice());
            investAsset.setPriceUpdatedAt(responseDtoList.getFirst().getPriceUpdatedAt());
            investAssetService.updateInvestAsset(investAsset);
        }
    }
}
