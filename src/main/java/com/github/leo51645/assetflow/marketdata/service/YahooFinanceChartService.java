package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.yahooApiException.*;
import com.github.leo51645.assetflow.marketdata.exception.yahooRequestException.YahooChartInvalidSymbolParameterException;
import com.github.leo51645.assetflow.marketdata.util.MarketDataUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YahooFinanceChartService implements MarketDataService <String, MarketDataYahooChartResponseDto> {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final MarketDataUtil marketDataUtil;
    private final ObjectMapper objectMapper;

    @Override
    public HttpResponse<String> getHttpResponse(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new YahooChartInvalidSymbolParameterException(symbol);
        }

        URI uri = URI.create(marketDataUtil.createYahooFinanceChartRequestURI(symbol));

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
                throw new YahooSymbolNotFoundException(symbol);
            }
            if (response.statusCode() == 429) {
                throw new YahooRateLimitException();
            }
            if (response.statusCode() >= 500) {
                throw new YahooServiceException(response.statusCode());
            }
            if (response.statusCode() != 200) {
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
    public List<MarketDataYahooChartResponseDto> parseResponse(String rawResponse, String symbolRequest) throws JsonProcessingException {
        if (symbolRequest == null || symbolRequest.isBlank()) {
            throw new YahooChartInvalidSymbolParameterException(symbolRequest);
        }

        List<MarketDataYahooChartResponseDto> parsedAssets = new ArrayList<>();

        JsonNode root;
        JsonNode metadata;

        try {
            root = objectMapper.readTree(rawResponse);

            if (rawResponse.isEmpty() | rawResponse.isBlank()) {
                throw new YahooInvalidResponseException(symbolRequest);
            }

            metadata = root.path("chart").path("result").get(0).path("meta");
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new YahooInvalidResponseException(symbolRequest);
        }

        JsonNode symbolResponseNode = metadata.path("symbol");
        JsonNode currentPriceNode = metadata.path("regularMarketPrice");
        JsonNode previousClosePriceNode = metadata.path("chartPreviousClose");

        if (symbolResponseNode.isMissingNode() || symbolResponseNode.isNull()
                || currentPriceNode.isMissingNode() || currentPriceNode.isNull()
                || previousClosePriceNode.isMissingNode() || previousClosePriceNode.isNull()) {
            throw new YahooMarketDataUnavailableException(symbolRequest);
        }

        String symbolResponse = symbolResponseNode.asText();
        String currencyString = metadata.get("currency").asText();

        BigDecimal currentPrice = BigDecimal.valueOf(currentPriceNode.asDouble());
        LocalDateTime priceUpdatedAt = LocalDateTime.now().withNano(0);
        BigDecimal previousClosePrice = BigDecimal.valueOf(previousClosePriceNode.asDouble());
        Currency currency = marketDataUtil.getCurrency(currencyString);

        MarketDataYahooChartResponseDto parsedAsset = new MarketDataYahooChartResponseDto(
                symbolResponse, currentPrice, priceUpdatedAt, previousClosePrice, currency
        );

        parsedAssets.add(parsedAsset);
        return parsedAssets;
    }
}
