package com.tk.wallet.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getExchangeRates(String baseCurrency) {
        String url = API_URL + baseCurrency.toUpperCase();
        ResponseEntity<HashMap<String, Object>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<HashMap<String, Object>>() {}
        );
        HashMap<String, Object> response = responseEntity.getBody();

        if (response == null || !response.containsKey("rates")) {
            throw new IllegalStateException("Exchange rates not found in response");
        }

        @SuppressWarnings("unchecked")
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");

        return rates;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency) {
        Map<String, Double> rates = getExchangeRates(fromCurrency);
        return rates.get(toCurrency);
    }
}
