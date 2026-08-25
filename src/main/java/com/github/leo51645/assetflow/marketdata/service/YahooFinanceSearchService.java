package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.YahooAssetNameNotFoundException;
import com.github.leo51645.assetflow.marketdata.exception.YahooIsinNotFoundException;
import com.github.leo51645.assetflow.marketdata.exception.YahooFinanceApiException;
import com.github.leo51645.assetflow.marketdata.exception.YahooFinanceConnectionException;
import com.github.leo51645.assetflow.marketdata.util.MarketDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YahooFinanceSearchService implements MarketDataService <String, MarketDataYahooSearchResponseDto> {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final MarketDataUtil marketDataUtil;

    @Override
    public HttpResponse<String> getHttpResponse(String requestParam) {
        URI uri = URI.create(marketDataUtil.createYahooFinanceSearchRequestURI(requestParam));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new YahooFinanceApiException("Yahoo Finance API returned status code" + response.statusCode() + "for request parameter " + requestParam); // Todo: Global exception Handler
            }

            return response;
        } catch (IOException e) {
            throw new YahooFinanceConnectionException("Failed to communicate with Yahoo Finance API"); // Todo: Global exception Handler
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YahooFinanceApiException("Yahoo Finance Request was interrupted"); // Todo: Global exception Handler
        }
    }

    @Override
    public List<MarketDataYahooSearchResponseDto> parseResponse(String rawResponse, String requestParam) throws JsonProcessingException {
        List<MarketDataYahooSearchResponseDto> parsedAssets = new ArrayList<>();

        JsonNode root = objectMapper.readTree(rawResponse);

        JsonNode assetsNode = root.get("quotes");

        if (marketDataUtil.isIsin(requestParam)) {
            if (assetsNode.isEmpty()) {
                throw new YahooIsinNotFoundException("Isin '" + requestParam + "' not found");
            }

            JsonNode singleAsset = assetsNode.get(0);

            parsedAssets.add(marketDataUtil.getMarketDataFromJsonNode(singleAsset));
        } else {
            if (assetsNode.isEmpty()) {
                throw new YahooAssetNameNotFoundException("Asset name '" + requestParam + "' not found");
            }

            for (JsonNode asset : assetsNode) {
                parsedAssets.add(marketDataUtil.getMarketDataFromJsonNode(asset));
            }
        }

        return parsedAssets;
    }

}
