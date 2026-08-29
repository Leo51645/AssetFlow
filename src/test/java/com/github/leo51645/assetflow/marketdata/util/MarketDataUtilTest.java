package com.github.leo51645.assetflow.marketdata.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import com.github.leo51645.assetflow.investment_asset.domain.entity.Currency;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.yahooApiException.YahooMarketDataUnavailableException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataUtilTest {

    private final MarketDataUtil marketDataUtil = new MarketDataUtil();

    @Nested
    class GetAssetType {

        @Test
        void shouldGetCorrectAssetType() {
            AssetType actualEtfParam = marketDataUtil.getAssetType("ETF");
            AssetType actualEquityParam = marketDataUtil.getAssetType("EQUITY");
            AssetType actualFutureParam = marketDataUtil.getAssetType("FUTURE");
            AssetType actualCryptoParam = marketDataUtil.getAssetType("CRYPTOCURRENCY");

            assertEquals(AssetType.ETF, actualEtfParam);
            assertEquals(AssetType.STOCK, actualEquityParam);
            assertEquals(AssetType.COMMODITY, actualFutureParam);
            assertEquals(AssetType.CRYPTO, actualCryptoParam);
        }

        @Test
        void shouldReturnAssetTypeOtherDueToUnrecognizedType() {
            AssetType actual = marketDataUtil.getAssetType("Unrecognized AssetType");
            assertEquals(AssetType.OTHER, actual);
        }

        @Test
        void shouldReturnAssetTypeOtherDueToNullString() {
            AssetType actual = marketDataUtil.getAssetType(null);
            assertEquals(AssetType.OTHER, actual);
        }

        @Test
        void shouldReturnAssetTypeOtherDueToBlankString() {
            AssetType actual = marketDataUtil.getAssetType("  ");
            assertEquals(AssetType.OTHER, actual);
        }

    }

    @Nested
    class GetCurrency {
        @Test
        void shouldReturnCorrectCurrency() {
            Currency actualEuroParam = marketDataUtil.getCurrency("EUR");
            Currency actualDollarParam = marketDataUtil.getCurrency("USD");
            Currency actualPoundParam = marketDataUtil.getCurrency("GBP");
            Currency actualFrancParam = marketDataUtil.getCurrency("CHF");
            Currency actualYenParam = marketDataUtil.getCurrency("JPY");
            Currency actualYuanParam = marketDataUtil.getCurrency("CNY");

            assertEquals(Currency.EUR, actualEuroParam);
            assertEquals(Currency.USD, actualDollarParam);
            assertEquals(Currency.GBP, actualPoundParam);
            assertEquals(Currency.CHF, actualFrancParam);
            assertEquals(Currency.JPY, actualYenParam);
            assertEquals(Currency.CNY, actualYuanParam);
        }

        @Test
        void shouldReturnCurrencyUSDAsDefaultDueToUnrecognizedCurrency() {
            Currency actual = marketDataUtil.getCurrency("Unrecognized Currency");

            assertEquals(Currency.USD, actual);
        }

        @Test
        void shouldReturnCurrencyUSDAsDefaultDueToNullString() {
            Currency actual = marketDataUtil.getCurrency(null);

            assertEquals(Currency.USD, actual);
        }

        @Test
        void shouldReturnCurrencyUSDAsDefaultDueToBlankString() {
            Currency actual = marketDataUtil.getCurrency("  ");

            assertEquals(Currency.USD, actual);
        }
    }

    @Nested
    class CreateURI {
        @Test
        void shouldCreateSearchURI() {
            String expected = "https://query1.finance.yahoo.com/v1/finance/search?q=APPL";
            String actual = marketDataUtil.createYahooFinanceSearchRequestURI("APPL");

            assertEquals(expected, actual);
        }

        @Test
        void shouldCreateChartURI() {
            String expected = "https://query1.finance.yahoo.com/v8/finance/chart/APPL?range=1d&interval=1d";
            String actual = marketDataUtil.createYahooFinanceChartRequestURI("APPL");

            assertEquals(expected, actual);
        }
    }

    @Nested
    class isIsin {
        @Test
        void shouldReturnTrueDueToValidIsin() {
            assertTrue(marketDataUtil.isIsin("US0378331005"));
        }

        @Test
        void shouldReturnFalseDueToInvalidIsin() {
            assertFalse(marketDataUtil.isIsin("INVALID"));
        }

        // Impossible that null or empty values are parsed in therefore no need to test these cases
    }

    @Nested
    class GetDataFromNode {

        GetDataFromNode() throws JsonProcessingException {
        }

        private static String getExampleResponse() {
            return """
                    {
                        "explains": [],
                        "count": 9,
                        "quotes": [
                            {
                                "exchange": "NMS",
                                "shortname": "Apple Inc.",
                                "quoteType": "EQUITY",
                                "symbol": "AAPL",
                                "index": "quotes",
                                "score": 37115.0,
                                "typeDisp": "Equity",
                                "longname": "Apple Inc.",
                                "exchDisp": "NASDAQ",
                                "sector": "Technology",
                                "sectorDisp": "Technology",
                                "industry": "Consumer Electronics",
                                "industryDisp": "Consumer Electronics",
                                "isYahooFinance": true
                            }
                        ],
                        "news": [],
                        "nav": [],
                        "lists": [],
                        "researchReports": [],
                        "screenerFieldResults": [],
                        "totalTime": 36,
                        "timeTakenForQuotes": 413,
                        "timeTakenForNews": 600,
                        "timeTakenForAlgowatchlist": 400,
                        "timeTakenForPredefinedScreener": 400,
                        "timeTakenForCrunchbase": 0,
                        "timeTakenForNav": 400,
                        "timeTakenForResearchReports": 0,
                        "timeTakenForQuestions": 0,
                        "timeTakenForScreenerField": 0,
                        "timeTakenForCulturalAssets": 0,
                        "timeTakenForSearchLists": 0
                    }
                    """;
        }
        private static JsonNode getJsonNodeByExampleResponse(String exampleResponse) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readTree(exampleResponse).get("quotes").get(0);
        }
        private final String EXAMPLE_ISIN = "US0378331005";

        @Test
        void shouldReturnCorrectDataFromJsonNode() throws JsonProcessingException {
            JsonNode singleAsset = getJsonNodeByExampleResponse(getExampleResponse());

            MarketDataYahooSearchResponseDto expected = new MarketDataYahooSearchResponseDto("Apple Inc.", "AAPL", AssetType.STOCK);

            MarketDataYahooSearchResponseDto actual = marketDataUtil.getMarketDataFromJsonNode(singleAsset, EXAMPLE_ISIN);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowYahooMarketDataUnavailableExceptionDueToEmptyOrNullFieldsAtResponse() throws JsonProcessingException {
            String rawResponse = """
                    {
                        "explains": [],
                        "count": 9,
                        "quotes": [
                            {
                                "exchange": "NMS",
                                "shortname": "Apple Inc.",
                                "quoteType": null,
                                "symbol": "   ",
                                "index": "quotes",
                                "score": 37115.0,
                                "typeDisp": "Equity",
                                "longname": null,
                                "exchDisp": "NASDAQ",
                                "sector": "Technology",
                                "sectorDisp": "Technology",
                                "industry": "Consumer Electronics",
                                "industryDisp": "Consumer Electronics",
                                "isYahooFinance": true
                            }
                        ],
                        "news": [],
                        "nav": [],
                        "lists": [],
                        "researchReports": [],
                        "screenerFieldResults": [],
                        "totalTime": 36,
                        "timeTakenForQuotes": 413,
                        "timeTakenForNews": 600,
                        "timeTakenForAlgowatchlist": 400,
                        "timeTakenForPredefinedScreener": 400,
                        "timeTakenForCrunchbase": 0,
                        "timeTakenForNav": 400,
                        "timeTakenForResearchReports": 0,
                        "timeTakenForQuestions": 0,
                        "timeTakenForScreenerField": 0,
                        "timeTakenForCulturalAssets": 0,
                        "timeTakenForSearchLists": 0
                    }
                    """;

            JsonNode asset = getJsonNodeByExampleResponse(rawResponse);

            assertThrows(YahooMarketDataUnavailableException.class, () -> marketDataUtil.getMarketDataFromJsonNode(asset, EXAMPLE_ISIN));
        }
    }


}