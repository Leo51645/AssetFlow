package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.investment_asset.domain.entity.AssetType;
import com.github.leo51645.assetflow.marketdata.domain.dto.MarketDataYahooSearchResponseDto;
import com.github.leo51645.assetflow.marketdata.exception.yahooApiException.*;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YahooFinanceSearchServiceTest {

    @Mock
    private HttpClient httpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MarketDataUtil marketDataUtil;

    private YahooFinanceSearchService yahooFinanceSearchService;

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
    private static String rawResponseByIsin() {
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
                            "score": 31403.0,
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
                    "news": [
                        {
                            "uuid": "5cbf94c9-41e3-3158-beb5-cdd15d235bf7",
                            "title": "Apple implements price hikes for its TV streaming service and 'One' subscription bundle",
                            "publisher": "Fox Business",
                            "link": "https://finance.yahoo.com/m/5cbf94c9-41e3-3158-beb5-cdd15d235bf7/apple-implements-price-hikes.html",
                            "providerPublishTime": 1787969356,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/v6Yu0QqdLS8fQ8ZecltSOQ--~B/aD03MjA7dz0xMjgwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/fox_business_text_367/7f65b993f9e192aa6b849d1b3b4e9719.jpg",
                                        "width": 1280,
                                        "height": 720,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/P.4zOtvAeyF45Az6P3l36Q--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/fox_business_text_367/7f65b993f9e192aa6b849d1b3b4e9719.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "AAPL"
                            ]
                        },
                        {
                            "uuid": "9bd4a306-38f9-30a2-bdb4-e19fc11c0e4a",
                            "title": "Apple CEO Tim Cook Pays Tribute to Yayoi Kusama, the Legendary Artist Behind Iconic Pumpkin Sculptures and Polka-Dots",
                            "publisher": "Benzinga",
                            "link": "https://finance.yahoo.com/m/9bd4a306-38f9-30a2-bdb4-e19fc11c0e4a/apple-ceo-tim-cook-pays.html",
                            "providerPublishTime": 1787959865,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/U.F_oOPwkT2ols5NdYfGCg--~B/aD01NzY7dz0xMDI0O2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/benzinga_79/788352cb1a4222a50b64a24baacea994.jpg",
                                        "width": 1024,
                                        "height": 576,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/gwyLS6w4sct5YlmwXbiolA--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/benzinga_79/788352cb1a4222a50b64a24baacea994.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "AAPL"
                            ]
                        },
                        {
                            "uuid": "e76362fd-2a59-3205-9fdd-57656dfef143",
                            "title": "Apple Makes Costly Move Subscribers Won't Miss",
                            "publisher": "GuruFocus.com",
                            "link": "https://finance.yahoo.com/media-advertising/articles/apple-makes-costly-move-subscribers-222454691.html",
                            "providerPublishTime": 1787955894,
                            "type": "STORY",
                            "relatedTickers": [
                                "AAPL",
                                "NVDA"
                            ]
                        },
                        {
                            "uuid": "11d4481c-83e4-30ee-a739-187db6167960",
                            "title": "Has Microsoft Stock Run Ahead Of Its AI Payoff?",
                            "publisher": "Trefis",
                            "link": "https://finance.yahoo.com/m/11d4481c-83e4-30ee-a739-187db6167960/has-microsoft-stock-run-ahead.html",
                            "providerPublishTime": 1787955055,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/YTEGdlZwgv.G8QaHKhGvZw--~B/aD0xMjgyO3c9MTkyMDthcHBpZD15dGFjaHlvbg--/https://media.zenfs.com/en/trefis_142/5a244008120629ff6da292a0e3ab9a6e.jpg",
                                        "width": 1920,
                                        "height": 1282,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/k7Iq9djZpp_raUa8jI2LRw--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/trefis_142/5a244008120629ff6da292a0e3ab9a6e.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "MSFT",
                                "GOOG",
                                "AMZN",
                                "AAPL",
                                "ORCL",
                                "ORCL-PD",
                                "CRM"
                            ]
                        },
                        {
                            "uuid": "e92e7668-12c6-3875-b5c0-62e6a69ac319",
                            "title": "Weekly Wrap: Bitcoin Holds Onto Gains",
                            "publisher": "CryptoProwl",
                            "link": "https://finance.yahoo.com/m/e92e7668-12c6-3875-b5c0-62e6a69ac319/weekly-wrap%3A-bitcoin-holds.html",
                            "providerPublishTime": 1787954280,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/DJAsPntn97ZRzMUZBu5HkA--~B/aD02MDA7dz0xMDAwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/cryptoprowl_304/eb17757340fbd2e84fed7cb85547a2c3.jpg",
                                        "width": 1000,
                                        "height": 600,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/uPLdQkIeboUb6XvUBbWP_Q--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/cryptoprowl_304/eb17757340fbd2e84fed7cb85547a2c3.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "BTC-USD",
                                "ETH-USD",
                                "CRCL",
                                "GOOG",
                                "MSTR",
                                "STRC",
                                "STRD",
                                "STRF",
                                "STRK",
                                "COIN",
                                "HIVE",
                                "NVDA",
                                "BMNR",
                                "AAPL",
                                "GEMI"
                            ]
                        },
                        {
                            "uuid": "f71ad446-1f7b-3fd7-9a31-767c245e765e",
                            "title": "How Much Upside Can AAPL Stock's Growth Deliver?",
                            "publisher": "Trefis",
                            "link": "https://finance.yahoo.com/m/f71ad446-1f7b-3fd7-9a31-767c245e765e/how-much-upside-can-aapl.html",
                            "providerPublishTime": 1787954034,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/xc0hpT8NzwpzURO17wULxA--~B/aD04NTM7dz0xMjgwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/trefis_142/f5bbfd947ebcc09b4a540f522b416137.jpg",
                                        "width": 1280,
                                        "height": 853,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/hdyVVHEKv03eeOr3yu4lOg--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/trefis_142/f5bbfd947ebcc09b4a540f522b416137.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "AAPL",
                                "DELL",
                                "AMZN",
                                "HPQ",
                                "MSFT",
                                "GOOG"
                            ]
                        },
                        {
                            "uuid": "540cb6d4-b93c-3128-9b38-45a437cad462",
                            "title": "Dell Stock Widens Your Portfolio Instead Of Steadying It",
                            "publisher": "Trefis",
                            "link": "https://finance.yahoo.com/m/540cb6d4-b93c-3128-9b38-45a437cad462/dell-stock-widens-your.html",
                            "providerPublishTime": 1787951159,
                            "type": "STORY",
                            "thumbnail": {
                                "resolutions": [
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/5HEtZmorqSXOj6Yz07mD6Q--~B/aD04NTM7dz0xMjgwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/trefis_142/aec1911f7df19eee11f9c07efd02521d.jpg",
                                        "width": 1280,
                                        "height": 853,
                                        "tag": "original"
                                    },
                                    {
                                        "url": "https://s.yimg.com/uu/api/res/1.2/U6oPirsWi5.HMuPfWvApEw--~B/Zmk9ZmlsbDtoPTE0MDtweW9mZj0wO3c9MTQwO2FwcGlkPXl0YWNoeW9u/https://media.zenfs.com/en/trefis_142/aec1911f7df19eee11f9c07efd02521d.jpg",
                                        "width": 140,
                                        "height": 140,
                                        "tag": "140x140"
                                    }
                                ]
                            },
                            "relatedTickers": [
                                "DELL",
                                "^GSPC",
                                "HPQ",
                                "IBM",
                                "AAPL",
                                "CSCO",
                                "SPGI",
                                "HPE"
                            ]
                        },
                        {
                            "uuid": "4e12bf62-ee09-356c-b7e0-02c15d4e4d6f",
                            "title": "Apple Raises US Subscription Price for Apple TV",
                            "publisher": "MT Newswires",
                            "link": "https://finance.yahoo.com/media-advertising/articles/apple-raises-us-subscription-price-183013599.html",
                            "providerPublishTime": 1787941813,
                            "type": "STORY",
                            "relatedTickers": [
                                "AAPL"
                            ]
                        }
                    ],
                    "nav": [],
                    "lists": [],
                    "researchReports": [],
                    "screenerFieldResults": [],
                    "totalTime": 34,
                    "timeTakenForQuotes": 412,
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
    private static String rawResponseByName() {
        return """
        {
            "explains": [],
            "count": 15,
            "quotes": [
                {
                    "exchange": "NMS",
                    "shortname": "Apple Inc.",
                    "quoteType": "EQUITY",
                    "symbol": "AAPL",
                    "index": "quotes",
                    "score": 32031.0,
                    "typeDisp": "Equity",
                    "longname": "Apple Inc.",
                    "exchDisp": "NASDAQ",
                    "sector": "Technology",
                    "sectorDisp": "Technology",
                    "industry": "Consumer Electronics",
                    "industryDisp": "Consumer Electronics",
                    "dispSecIndFlag": true,
                    "isYahooFinance": true
                },
                {
                    "exchange": "NYQ",
                    "shortname": "Apple Hospitality REIT, Inc.",
                    "quoteType": "EQUITY",
                    "symbol": "APLE",
                    "index": "quotes",
                    "score": 20013.0,
                    "typeDisp": "Equity",
                    "longname": "Apple Hospitality REIT, Inc.",
                    "exchDisp": "NYSE",
                    "sector": "Real Estate",
                    "sectorDisp": "Real Estate",
                    "industry": "REIT—Hotel & Motel",
                    "industryDisp": "REIT—Hotel & Motel",
                    "isYahooFinance": true
                },
                {
                    "exchange": "GER",
                    "shortname": "Apple Inc.                    R",
                    "quoteType": "EQUITY",
                    "symbol": "APC.DE",
                    "index": "quotes",
                    "score": 20011.0,
                    "typeDisp": "Equity",
                    "longname": "Apple Inc.",
                    "exchDisp": "XETRA",
                    "sector": "Technology",
                    "sectorDisp": "Technology",
                    "industry": "Consumer Electronics",
                    "industryDisp": "Consumer Electronics",
                    "isYahooFinance": true
                },
                {
                    "exchange": "OQB",
                    "shortname": "Apple iSports Group Inc.",
                    "quoteType": "EQUITY",
                    "symbol": "AAPI",
                    "index": "quotes",
                    "score": 20004.0,
                    "typeDisp": "Equity",
                    "longname": "Apple iSports Group, Inc.",
                    "exchDisp": "OQB",
                    "sector": "Consumer Cyclical",
                    "sectorDisp": "Consumer Cyclical",
                    "industry": "Gambling",
                    "industryDisp": "Gambling",
                    "isYahooFinance": true,
                    "prevName": "Prevention Insurance.Com",
                    "nameChangeDate": "2026-08-28"
                },
                {
                    "exchange": "SET",
                    "shortname": "AAPL19_DR AAPL#YUANTA",
                    "quoteType": "EQUITY",
                    "symbol": "AAPL19.BK",
                    "index": "quotes",
                    "score": 20004.0,
                    "typeDisp": "Equity",
                    "longname": "Apple Inc.",
                    "exchDisp": "SET",
                    "sector": "Technology",
                    "sectorDisp": "Technology",
                    "industry": "Consumer Electronics",
                    "industryDisp": "Consumer Electronics",
                    "isYahooFinance": true
                },
                {
                    "exchange": "BUE",
                    "shortname": "APPLE INC CEDEAR(REPR 1/20 SHR)",
                    "quoteType": "EQUITY",
                    "symbol": "AAPLC.BA",
                    "index": "quotes",
                    "score": 20003.0,
                    "typeDisp": "Equity",
                    "exchDisp": "Buenos Aires",
                    "isYahooFinance": true
                },
                {
                    "exchange": "PNK",
                    "shortname": "Apple Rush Co., Inc.",
                    "quoteType": "EQUITY",
                    "symbol": "APRU",
                    "index": "quotes",
                    "score": 20003.0,
                    "typeDisp": "Equity",
                    "longname": "Apple Rush Company, Inc.",
                    "exchDisp": "OTC Markets",
                    "sector": "Consumer Defensive",
                    "sectorDisp": "Consumer Defensive",
                    "industry": "Beverages—Non-Alcoholic",
                    "industryDisp": "Beverages—Non—Alcoholic",
                    "isYahooFinance": true
                }
            ],
            "news": [
                {
                    "uuid": "5cbf94c9-41e3-3158-beb5-cdd15d235bf7",
                    "title": "Apple implements price hikes for its TV streaming service and 'One' subscription bundle",
                    "publisher": "Fox Business",
                    "link": "https://finance.yahoo.com/m/5cbf94c9-41e3-3158-beb5-cdd15d235bf7/apple-implements-price-hikes.html",
                    "providerPublishTime": 1787969356,
                    "type": "STORY",
                    "relatedTickers": ["AAPL"]
                },
                {
                    "uuid": "9bd4a306-38f9-30a2-bdb4-e19fc11c0e4a",
                    "title": "Apple CEO Tim Cook Pays Tribute to Yayoi Kusama, the Legendary Artist Behind Iconic Pumpkin Sculptures and Polka-Dots",
                    "publisher": "Benzinga",
                    "link": "https://finance.yahoo.com/m/9bd4a306-38f9-30a2-bdb4-e19fc11c0e4a/apple-ceo-tim-cook-pays.html",
                    "providerPublishTime": 1787959865,
                    "type": "STORY",
                    "relatedTickers": ["AAPL"]
                },
                {
                    "uuid": "e76362fd-2a59-3205-9fdd-57656dfef143",
                    "title": "Apple Makes Costly Move Subscribers Won't Miss",
                    "publisher": "GuruFocus.com",
                    "link": "https://finance.yahoo.com/media-advertising/articles/apple-makes-costly-move-subscribers-222454691.html",
                    "providerPublishTime": 1787955894,
                    "type": "STORY",
                    "relatedTickers": ["AAPL", "NVDA"]
                },
                {
                    "uuid": "11d4481c-83e4-30ee-a739-187db6167960",
                    "title": "Has Microsoft Stock Run Ahead Of Its AI Payoff?",
                    "publisher": "Trefis",
                    "link": "https://finance.yahoo.com/m/11d4481c-83e4-30ee-a739-187db6167960/has-microsoft-stock-run-ahead.html",
                    "providerPublishTime": 1787955055,
                    "type": "STORY",
                    "relatedTickers": ["MSFT", "GOOG", "AMZN", "AAPL", "ORCL", "ORCL-PD", "CRM"]
                },
                {
                    "uuid": "e92e7668-12c6-3875-b5c0-62e6a69ac319",
                    "title": "Weekly Wrap: Bitcoin Holds Onto Gains",
                    "publisher": "CryptoProwl",
                    "link": "https://finance.yahoo.com/m/e92e7668-12c6-3875-b5c0-62e6a69ac319/weekly-wrap%3A-bitcoin-holds.html",
                    "providerPublishTime": 1787954280,
                    "type": "STORY",
                    "relatedTickers": ["BTC-USD", "ETH-USD", "CRCL", "GOOG", "MSTR", "STRC", "STRD", "STRF", "STRK", "COIN", "HIVE", "NVDA", "BMNR", "AAPL", "GEMI"]
                },
                {
                    "uuid": "f71ad446-1f7b-3fd7-9a31-767c245e765e",
                    "title": "How Much Upside Can AAPL Stock's Growth Deliver?",
                    "publisher": "Trefis",
                    "link": "https://finance.yahoo.com/m/f71ad446-1f7b-3fd7-9a31-767c245e765e/how-much-upside-can-aapl.html",
                    "providerPublishTime": 1787954034,
                    "type": "STORY",
                    "relatedTickers": ["AAPL", "DELL", "AMZN", "HPQ", "MSFT", "GOOG"]
                },
                {
                    "uuid": "540cb6d4-b93c-3128-9b38-45a437cad462",
                    "title": "Dell Stock Widens Your Portfolio Instead Of Steadying It",
                    "publisher": "Trefis",
                    "link": "https://finance.yahoo.com/m/540cb6d4-b93c-3128-9b38-45a437cad462/dell-stock-widens-your.html",
                    "providerPublishTime": 1787951159,
                    "type": "STORY",
                    "relatedTickers": ["DELL", "^GSPC", "HPQ", "IBM", "AAPL", "CSCO", "SPGI", "HPE"]
                },
                {
                    "uuid": "4e12bf62-ee09-356c-b7e0-02c15d4e4d6f",
                    "title": "Apple Raises US Subscription Price for Apple TV",
                    "publisher": "MT Newswires",
                    "link": "https://finance.yahoo.com/media-advertising/articles/apple-raises-us-subscription-price-183013599.html",
                    "providerPublishTime": 1787941813,
                    "type": "STORY",
                    "relatedTickers": ["AAPL"]
                }
            ],
            "nav": [],
            "lists": [],
            "researchReports": [],
            "screenerFieldResults": [],
            "totalTime": 43,
            "timeTakenForQuotes": 422,
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
    private final String EXAMPLE_NAME = "Apple";
    private final String EXAMPLE_ISIN = "US0378331005";

    @BeforeEach
    void setUp() {
        yahooFinanceSearchService = new YahooFinanceSearchService(httpClient, objectMapper, marketDataUtil);
    }

    @Nested
    class GetHttpResponse {
        @Test
        void shouldReturnHttpResponse() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(200);
            ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);

            HttpResponse<String> actual = yahooFinanceSearchService.getHttpResponse("something");

            assertSame(expected, actual);

            verify(marketDataUtil).createYahooFinanceSearchRequestURI("something");
            verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

            assertEquals(URI.create("https://example.com"), requestCaptor.getValue().uri());
        }

        @Test
        void shouldThrowYahooIsinNotFoundExceptionDueTo404WithIsinParam() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(404);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");
            when(marketDataUtil.isIsin(anyString())).thenReturn(true);

            assertThrows(YahooIsinNotFoundException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(marketDataUtil).isIsin("something");
        }

        @Test
        void shouldThrowYahooAssetNameNotFoundExceptionDueTo404WithNameParam() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(404);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");
            when(marketDataUtil.isIsin(anyString())).thenReturn(false);

            assertThrows(YahooAssetNameNotFoundException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(marketDataUtil).isIsin("something");
        }

        @Test
        void shouldThrowYahooRateLimitExceptionDueTo429() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(429);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooRateLimitException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooServiceExceptionDueToStatusCodeGreater500() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(502);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooServiceException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooApiExceptionDueToStatusCodeNot200() throws IOException, InterruptedException {
            HttpResponse<String> expected = createFakeResponse(403);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expected);
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");

            assertThrows(YahooApiException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        void shouldThrowYahooConnectionExceptionDueToIOExceptionWhenSending() throws IOException, InterruptedException {
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException());

            assertThrows(YahooConnectionException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
        }

        @Test
        void shouldThrowYahooConnectionExceptionDueToInterruptedExceptionWhenSending() throws IOException, InterruptedException {
            when(marketDataUtil.createYahooFinanceSearchRequestURI(anyString())).thenReturn("https://example.com");
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new InterruptedException());

            assertThrows(YahooConnectionException.class, () -> yahooFinanceSearchService.getHttpResponse("something"));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToRequestParameterEqualToNull() {
            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.getHttpResponse(null));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToEmptyRequestParameter() {
            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.getHttpResponse(""));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToBlankRequestParameter() {
            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.getHttpResponse("  "));
        }
    }

    @Nested
    class ParseHttpResponse {

        @Test
        void shouldParseRawResponseWithIsinSearchParameter() {
            MarketDataYahooSearchResponseDto expectedDto = new MarketDataYahooSearchResponseDto("Apple Inc.", "APPL", AssetType.STOCK);
            List<MarketDataYahooSearchResponseDto> expectedAssets = List.of(expectedDto);

            when(marketDataUtil.isIsin(EXAMPLE_ISIN)).thenReturn(true);
            when(marketDataUtil.getMarketDataFromJsonNode(any(JsonNode.class))).thenReturn(expectedDto);

            List<MarketDataYahooSearchResponseDto> actual = yahooFinanceSearchService.parseResponse(rawResponseByIsin(), EXAMPLE_ISIN);

            assertEquals(expectedAssets.size(), actual.size());
            assertEquals(expectedAssets, actual);
            assertEquals(expectedAssets.getFirst(), actual.getFirst());
        }

        @Test
        void shouldParseRawResponseWithNameSearchParameter() {
            MarketDataYahooSearchResponseDto expectedFirstDto = new MarketDataYahooSearchResponseDto("Apple Inc.", "APPL", AssetType.STOCK);

            when(marketDataUtil.isIsin(EXAMPLE_NAME)).thenReturn(false);
            when(marketDataUtil.getMarketDataFromJsonNode(any(JsonNode.class))).thenReturn(expectedFirstDto);

            List<MarketDataYahooSearchResponseDto> actual = yahooFinanceSearchService.parseResponse(rawResponseByName(), EXAMPLE_NAME);

            assertEquals(7, actual.size());
            assertEquals(expectedFirstDto, actual.getFirst());
        }

        @Test
        void shouldThrowYahooIsinNotFoundExceptionDueToEmptyQuotesParameterInResponse() {
            String rawResponse =
                    """
                    {
                        "explains": [],
                        "count": 8,
                        "quotes": [],
                        "news": [],
                        "nav": [],
                        "lists": [],
                        "researchReports": [],
                        "screenerFieldResults": [],
                        "totalTime": 56,
                        "timeTakenForQuotes": 410,
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

            when(marketDataUtil.isIsin(EXAMPLE_ISIN)).thenReturn(true);

            assertThrows(YahooIsinNotFoundException.class, () -> yahooFinanceSearchService.parseResponse(rawResponse, EXAMPLE_ISIN));
        }

        @Test
        void shouldThrowYahooAssetNameNotFoundExceptionDueToEmptyQuotesParameterInResponse() {
            String rawResponse =
                    """
                    {
                        "explains": [],
                        "count": 8,
                        "quotes": [],
                        "news": [],
                        "nav": [],
                        "lists": [],
                        "researchReports": [],
                        "screenerFieldResults": [],
                        "totalTime": 56,
                        "timeTakenForQuotes": 410,
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

            when(marketDataUtil.isIsin(EXAMPLE_NAME)).thenReturn(false);

            assertThrows(YahooAssetNameNotFoundException.class, () -> yahooFinanceSearchService.parseResponse(rawResponse, EXAMPLE_NAME));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToRawResponseEqualToNull() {
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceSearchService.parseResponse(null, "Apple"));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToEmptyRawResponse() {
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceSearchService.parseResponse("", "Apple"));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToBlankRawResponse() {
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceSearchService.parseResponse("   ", "Apple"));
        }

        @Test
        void shouldThrowYahooInvalidResponseExceptionDueToInvalidJsonInRawResponse() {
            String invalidJson = """
                    {
                        "explains": [,
                        "count": 8,
                        "quotes": [],
                        "news": [],
                        "nav": [],
                        "lists": [],
                        "researchReports": [],
                        "screenerFieldResults": [],
                        "totalTime": 56,
                        "timeTakenForQuotes": 410,
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
                    """;
            assertThrows(YahooInvalidResponseException.class, () -> yahooFinanceSearchService.parseResponse(invalidJson, "Apple"));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToRequestParameterEqualToNull() {
            String rawResponse = rawResponseByIsin();

            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.parseResponse(rawResponse, null));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToEmptyRequestParameter() {
            String rawResponse = rawResponseByIsin();

            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.parseResponse(rawResponse, ""));
        }

        @Test
        void shouldThrowYahooSearchInvalidParameterExceptionDueToBlankRequestParameter() {
            String rawResponse = rawResponseByIsin();

            assertThrows(YahooSearchInvalidParameterException.class, () -> yahooFinanceSearchService.parseResponse(rawResponse, "  "));
        }
    }

}