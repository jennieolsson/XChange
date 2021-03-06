package com.xeiam.xchange.gatecoin.dto.trade.Results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xeiam.xchange.gatecoin.dto.GatecoinResult;
import com.xeiam.xchange.gatecoin.dto.marketdata.ResponseStatus;

/**
 * @author sumedha
 */
public class GatecoinCancelOrderResult extends GatecoinResult {

  @JsonCreator
  public GatecoinCancelOrderResult(@JsonProperty("responseStatus") ResponseStatus responseStatus) {
    super(responseStatus);
  }
}
