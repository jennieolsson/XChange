package com.xeiam.xchange.hitbtc.dto.marketdata;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class HitbtcTickersJsonTest {
  
  @Test
  public void tesetUnmarshal() throws IOException {
    
    // Read in the JSON from the example resources
    InputStream is = HitbtcTickersJsonTest.class.getResourceAsStream("/marketdata/example-tickers-data.json");

    // Use Jackson to parse it
    ObjectMapper mapper = new ObjectMapper();

    Map<String,HitbtcTicker> tickers = mapper.readValue(is, new TypeReference<Map<String,HitbtcTicker>>(){});

    assertThat(tickers).hasSize(18);

    HitbtcTicker ticker = tickers.get("BTCUSD");

    assertThat(ticker.getAsk()).isEqualTo("347.13");
    assertThat(ticker.getBid()).isEqualTo("346.14");
    assertThat(ticker.getLast()).isEqualTo("346.56");
    assertThat(ticker.getHigh()).isEqualTo("354.66");
    assertThat(ticker.getLow()).isEqualTo("342.36");
    assertThat(ticker.getVolume()).isEqualTo("588.98");
    assertThat(ticker.getTimestamp()).isEqualTo(1447508357822L);
  }
}
