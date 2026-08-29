package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.yahooApiException.*;
import com.github.leo51645.assetflow.marketdata.exception.yahooRequestException.YahooSearchInvalidParameterException;
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

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MarketDataUtil marketDataUtil;

    @Override
    public HttpResponse<String> getHttpResponse(String requestParam) {
        if (requestParam == null || requestParam.isBlank()) {
            throw new YahooSearchInvalidParameterException(requestParam);
        }

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

            if (response.statusCode() == 404) {
                if (marketDataUtil.isIsin(requestParam)) {
                    throw new YahooIsinNotFoundException(requestParam);
                } else {
                    throw new YahooAssetNameNotFoundException(requestParam);
                }
            } else if (response.statusCode() == 429) {
                throw new YahooRateLimitException();
            } else if (response.statusCode() >= 500) {
                throw new YahooServiceException(response.statusCode());
            } else if (response.statusCode() != 200) {
                throw new YahooApiException(response.statusCode());
            }

            return response;
        } catch (IOException e) {
            throw new YahooConnectionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new YahooConnectionException(e);
        }
    }

    @Override
    public List<MarketDataYahooSearchResponseDto> parseResponse(String rawResponse, String requestParam) {
        List<MarketDataYahooSearchResponseDto> parsedAssets = new ArrayList<>();

        JsonNode root;
        JsonNode assetsNode;

        if (requestParam == null || requestParam.isBlank()) {
            throw new YahooSearchInvalidParameterException(requestParam);
        }

        try {
            root = objectMapper.readTree(rawResponse);

            if (rawResponse.isEmpty() | rawResponse.isBlank()) {
                throw new YahooInvalidResponseException(requestParam);
            }

            assetsNode = root.get("quotes");
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new YahooInvalidResponseException(requestParam);
        }


        if (marketDataUtil.isIsin(requestParam)) {
            if (assetsNode.isEmpty()) {
                throw new YahooIsinNotFoundException(requestParam);
            }

            JsonNode singleAsset = assetsNode.get(0);

            parsedAssets.add(marketDataUtil.getMarketDataFromJsonNode(singleAsset));
        } else {
            if (assetsNode.isEmpty()) {
                throw new YahooAssetNameNotFoundException(requestParam);
            }

            for (JsonNode asset : assetsNode) {
                parsedAssets.add(marketDataUtil.getMarketDataFromJsonNode(asset));
            }
        }

        return parsedAssets;
    }

}
