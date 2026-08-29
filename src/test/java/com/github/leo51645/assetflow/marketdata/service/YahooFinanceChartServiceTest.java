package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooChartResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.yahooApiException.*;
import com.github.leo51645.assetflow.marketdata.exception.yahooRequestException.YahooChartInvalidSymbolParameterException;
import com.github.leo51645.assetflow.marketdata.exception.yahooRequestException.YahooSearchInvalidParameterException;
import com.github.leo51645.assetflow.marketdata.util.MarketDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YahooFinanceChartServiceTest {

    @Mock
    private HttpClient httpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MarketDataUtil marketDataUtil;

    private YahooFinanceChartService yahooFinanceChartService;

    private static HttpResponse<String> createFakeResponse(int statusCode) {
        return new HttpResponse<>() {

            @Override
            public int statusCode() {
                return statusCode;
            }

            @Override
            public HttpRequest request() {
                return null;
            }

            @Override
            public Optional<HttpResponse<String>> previousResponse() {
                return Optional.empty();
            }

            @Override
            public HttpHeaders headers() {
                return null;
            }

            @Override
            public String body() {
                return null;
            }

            @Override
            public Optional<SSLSession> sslSession() {
                return Optional.empty();
            }

            @Override
            public URI uri() {
                return URI.create("https://example.com");
            }

            @Override
            public HttpClient.Version version() {
                return null;
            }
        };
    }
    private static String rawResponse() {
        return """
                {
                    "chart": {
                        "result": [
                            {
                                "meta": {
                                    "currency": "USD",
                                    "symbol": "SONY",
                                    "exchangeName": "NYQ",
                                    "fullExchangeName": "NYSE",
                                    "instrumentType": "EQUITY",
                                    "firstTradeDate": 99153000,
                                    "regularMarketTime": 1787947202,
                                    "hasPrePostMarketData": true,
                                    "gmtoffset": -14400,
                                    "timezone": "EDT",
                                    "exchangeTimezoneName": "America/New_York",
                                    "regularMarketPrice": 24.85,
                                    "regularMarketChangePercent": 3.241,
                                    "fiftyTwoWeekHigh": 30.34,
                                    "fiftyTwoWeekLow": 19.32,
                                    "regularMarketDayHigh": 25.02,
                                    "regularMarketDayLow": 24.655,
                                    "regularMarketVolume": 4809501,
                                    "longName": "Sony Group Corporation",
                                    "shortName": "Sony Group Corporation",
                                    "chartPreviousClose": 24.26,
                                    "priceHint": 2,
                                    "currentTradingPeriod": {
                                        "pre": {
                                            "timezone": "EDT",
                                            "start": 1787904000,
                                            "end": 1787923800,
                                            "gmtoffset": -14400
                                        },
                                        "regular": {
                                            "timezone": "EDT",
                                            "start": 1787923800,
                                            "end": 1787947200,
                                            "gmtoffset": -14400
                                        },
                                        "post": {
                                            "timezone": "EDT",
                                            "start": 1787947200,
                                            "end": 1787961600,
                                            "gmtoffset": -14400
                                        }
                                    },
                                    "dataGranularity": "1d",
                                    "range": "4d",
                                    "validRanges": [
                                        "1d",
                                        "5d",
                                        "1mo",
                                        "3mo",
                                        "6mo",
                                        "1y",
                                        "2y",
                                        "5y",
                                        "10y",
                                        "ytd",
                                        "max"
                                    ]
                                },
                                "timestamp": [
                                    1787664600,
                                    1787751000,
                                    1787837400,
                                    1787923800
                                ],
                                "indicators": {
                                    "quote": [
                                        {
                                            "high": [
                                                24.110000610351562,
                                                24.329999923706055,
                                                24.299999237060547,
                                                25.020000457763672
                                            ],
                                            "close": [
                                                24.049999237060547,
                                                24.1200008392334,
                                                24.06999969482422,
                                                null
                                            ],
                                            "open": [
                                                23.969999313354492,
                                                24.260000228881836,
                                                24.219999313354492,
                                                24.670000076293945
                                            ],
                                            "volume": [
                                                3076900,
                                                3320400,
                                                4155600,
                                                4809501
                                            ],
                                            "low": [
                                                23.809999465942383,
                                                24.059999465942383,
                                                23.860000610351562,
                                                24.655000686645508
                                            ]
                                        }
                                    ],
                                    "adjclose": [
                                        {
                                            "adjclose": [
                                                24.049999237060547,
                                                24.1200008392334,
                                                24.06999969482422,
                                                null
                                            ]
                                        }
                                    ]
                                }
                            }
                        ],
                        "error": null
                    }
                }
                """;
    }
    private final String EXAMPLE_SYMBOL = "SONY";

    @BeforeEach
    void setUp() {
        yahooFinanceChartService = new YahooFinanceChartService(httpClient, marketDataUtil, objectMapper);
    }

    @Nested
    class GetHttpResponse {
        @Test
        void shouldReturnHttpResponse() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(200);
            ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);

            HttpResponse<String> actual = yahooFinanceChartService.getHttpResponse("something");

            assertSame(expected, actual);

            verify(marketDataUtil).createYahooFinanceChartRequestURI("something");
            verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

            assertEquals(URI.create("https://example.com"), requestCaptor.getValue().uri());
        }

        @Test
        void shouldThrowYahooSymbolNotFoundExceptionDueTo404() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(404);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooSymbolNotFoundException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooRateLimitExceptionDueTo429() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(429);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooRateLimitException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooServiceExceptionDueToStatusCodeGreater500() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(502);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooServiceException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooApiExceptionDueToStatusCodeNot200() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(403);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooApiException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooConnectionExceptionDueToIOExceptionWhenSending() throws IOException, InterruptedException {
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException());

            assertThrows(YahooConnectionException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
        }

        @Test
        void shouldThrowYahooConnectionExceptionDueToInterruptedExceptionWhenSending() throws IOException, InterruptedException {
            when(marketDataUtil.createYahooFinanceChartRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new InterruptedException());

            assertThrows(YahooConnectionException.class, () -> yahooFinanceChartService.getHttpResponse("something"));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToSymbolRequestParameterEqualToNull() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.getHttpResponse(null));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToEmptySymbolRequestParameter() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.getHttpResponse(""));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToBlankSymbolRequestParameter() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.getHttpResponse("   "));
        }
    }

    @Nested
    class ParseHttpResponse {
        @Test
        void shouldParseRawResponse() {
            MarketDataYahooChartResponseDto expected = MarketDataYahooChartResponseDto.builder()
                    .symbol(EXAMPLE_SYMBOL)
                    .currentPrice(BigDecimal.valueOf(24.85))
                    .previousClosePrice(BigDecimal.valueOf(24.26))
                    .currency(Currency.USD)
                    .build();

            when(marketDataUtil.getCurrency(anyString())).thenReturn(Currency.USD);

            List<MarketDataYahooChartResponseDto> actualList = yahooFinanceChartService.parseResponse(rawResponse(), EXAMPLE_SYMBOL);

            MarketDataYahooChartResponseDto actual = actualList.getFirst();

            assertEquals(1, actualList.size());
            assertEquals(expected.getSymbol(), actual.getSymbol());
            assertEquals(expected.getCurrentPrice(), actual.getCurrentPrice());
            assertEquals(expected.getPreviousClosePrice(), actual.getPreviousClosePrice());
            assertEquals(expected.getCurrency(), actual.getCurrency());
            assertTrue(actual.getPriceUpdatedAt().isBefore(LocalDateTime.now()));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToSymbolRequestParameterEqualToNull() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.parseResponse(rawResponse(), null));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToEmptySymbolRequestParameter() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.parseResponse(rawResponse(), ""));
        }

        @Test
        void shouldThrowYahooChartInvalidSymbolParameterExceptionDueToBlankSymbolRequestParameter() {
            assertThrows(YahooChartInvalidSymbolParameterException.class, () -> yahooFinanceChartService.parseResponse(rawResponse(), " "));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToResponseEqualToNull() {
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceChartService.parseResponse(null, EXAMPLE_SYMBOL));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToBlankResponse() {
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceChartService.parseResponse(" ", EXAMPLE_SYMBOL));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToInvalidJSONResponseFormat() {
            String invalidJson = """
                    {
                        "invalidJson": "true"
                    
                    """;

            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceChartService.parseResponse(invalidJson, EXAMPLE_SYMBOL));
        }

        @Test
        void shouldThrowYahooMarketDataUnavailableExceptionDueToNullAndBlankMarketData() {
            String rawResponse = """
                {
                  "chart": {
                    "result": [
                      {
                        "meta": {
                          "currency": "USD",
                          "symbol": "  ",
                          "exchangeName": "NYQ",
                          "fullExchangeName": "NYSE",
                          "instrumentType": "EQUITY",
                          "firstTradeDate": 99153000,
                          "regularMarketTime": 1787947202,
                          "hasPrePostMarketData": true,
                          "gmtoffset": -14400,
                          "timezone": "EDT",
                          "exchangeTimezoneName": "America/New_York",
                          "regularMarketPrice": null,
                          "regularMarketChangePercent": 3.241,
                          "fiftyTwoWeekHigh": 30.34,
                          "fiftyTwoWeekLow": 19.32,
                          "regularMarketDayHigh": 25.02,
                          "regularMarketDayLow": 24.655,
                          "regularMarketVolume": 4809501,
                          "longName": "Sony Group Corporation",
                          "shortName": "Sony Group Corporation",
                          "chartPreviousClose": null,
                          "priceHint": 2,
                          "dataGranularity": "1d",
                          "range": "4d"
                        }
                      }
                    ],
                    "error": null
                  }
                }
                """;

            assertThrows(YahooMarketDataUnavailableException.class, () -> yahooFinanceChartService.parseResponse(rawResponse, EXAMPLE_SYMBOL));
        }


    }

}