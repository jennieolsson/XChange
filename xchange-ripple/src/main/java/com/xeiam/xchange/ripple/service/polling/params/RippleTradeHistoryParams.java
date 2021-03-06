package com.xeiam.xchange.ripple.service.polling.params;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamCurrencyPair;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamPaging;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamsAll;
import com.xeiam.xchange.service.polling.trade.params.TradeHistoryParamsTimeSpan;

/**
 * The complete set of parameters that a Ripple trade history query will consider.
 */
public class RippleTradeHistoryParams implements TradeHistoryParamCurrencyPair, TradeHistoryParamPaging, TradeHistoryParamsTimeSpan,
    RippleTradeHistoryAccount, RippleTradeHistoryHashLimit, RippleTradeHistoryCount, RippleTradeHistoryPreferredCurrencies {

  public static final int DEFAULT_PAGE_LENGTH = 20;

  private final TradeHistoryParamsAll all = new TradeHistoryParamsAll();

  private String account = null;
  private String hashLimit = null;

  private int tradeCount = 0;
  private int tradeCountLimit = RippleTradeHistoryCount.DEFAULT_TRADE_COUNT_LIMIT;

  private int apiCallCount = 0;
  private int apiCallCountLimit = RippleTradeHistoryCount.DEFAULT_API_CALL_COUNT;

  private Collection<String> preferredBaseCurrency = new HashSet<String>();
  private Collection<String> preferredCounterCurrency = new HashSet<String>();

  public RippleTradeHistoryParams() {
    setPageLength(DEFAULT_PAGE_LENGTH);
  }

  public void setAccount(final String value) {
    account = value;
  }

  @Override
  public String getAccount() {
    return account;
  }

  public void setHashLimit(final String value) {
    hashLimit = value;
  }

  @Override
  public String getHashLimit() {
    return hashLimit;
  }

  @Override
  public void resetApiCallCount() {
    apiCallCount = 0;
  }

  @Override
  public void incrementApiCallCount() {
    apiCallCount++;
  }

  @Override
  public int getApiCallCount() {
    return apiCallCount;
  }

  public void setApiCallCountLimit(final int value) {
    apiCallCountLimit = value;
  }

  @Override
  public int getApiCallCountLimit() {
    return apiCallCountLimit;
  }

  @Override
  public void resetTradeCount() {
    tradeCount = 0;
  }

  @Override
  public void incrementTradeCount() {
    tradeCount++;
  }

  @Override
  public int getTradeCount() {
    return tradeCount;
  }

  public void setTradeCountLimit(final int value) {
    tradeCountLimit = value;
  }

  @Override
  public int getTradeCountLimit() {
    return tradeCountLimit;
  }

  public void addPreferredBaseCurrency(final String value) {
    preferredBaseCurrency.add(value);
  }

  @Override
  public Collection<String> getPreferredBaseCurrency() {
    return preferredBaseCurrency;
  }

  public void addPreferredCounterCurrency(final String value) {
    preferredCounterCurrency.add(value);
  }

  @Override
  public Collection<String> getPreferredCounterCurrency() {
    return preferredCounterCurrency;
  }

  @Override
  public void setCurrencyPair(final CurrencyPair value) {
    all.setCurrencyPair(value);
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return all.getCurrencyPair();
  }

  @Override
  public void setPageLength(final Integer value) {
    all.setPageLength(value);
  }

  /**
   * @return the number of notifications to return in a single query, if not set the server assumes a default of 10.
   */
  @Override
  public Integer getPageLength() {
    return all.getPageLength();
  }

  @Override
  public void setPageNumber(final Integer value) {
    all.setPageNumber(value);
  }

  @Override
  public Integer getPageNumber() {
    return all.getPageNumber();
  }

  @Override
  public void setStartTime(final Date value) {
    all.setStartTime(value);
  }

  @Override
  public Date getStartTime() {
    return all.getStartTime();
  }

  @Override
  public void setEndTime(final Date value) {
    all.setEndTime(value);
  }

  @Override
  public Date getEndTime() {
    return all.getEndTime();
  }
}
