package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
public interface MarketDataService<Req, Res> { // req = request; res = response
    HttpResponse<String> getHttpResponse(Req requestParams);

    Res parseResponse(String rawResponse) throws JsonProcessingException;
}
