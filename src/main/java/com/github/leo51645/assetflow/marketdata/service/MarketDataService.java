package com.github.leo51645.assetflow.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.List;

@Service
public interface MarketDataService<Req, Res> { // req = request; res = response

    HttpResponse<String> getHttpResponse(Req requestParam);

    List<Res> parseResponse(String rawResponse, Req requestParam) throws JsonProcessingException;
}
