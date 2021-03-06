package com.xeiam.xchange.gatecoin;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.Order.OrderType;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.dto.marketdata.Trades.TradeSortType;
import com.xeiam.xchange.dto.trade.LimitOrder;
import com.xeiam.xchange.dto.trade.UserTrade;
import com.xeiam.xchange.dto.trade.UserTrades;
import com.xeiam.xchange.dto.trade.Wallet;
import com.xeiam.xchange.gatecoin.dto.account.GatecoinBalance;
import com.xeiam.xchange.gatecoin.dto.marketdata.GatecoinDepth;
import com.xeiam.xchange.gatecoin.dto.marketdata.GatecoinTicker;
import com.xeiam.xchange.gatecoin.dto.marketdata.GatecoinTransaction;
import com.xeiam.xchange.gatecoin.dto.marketdata.Results.GatecoinDepthResult;
import com.xeiam.xchange.gatecoin.dto.trade.GatecoinTradeHistory;
import com.xeiam.xchange.gatecoin.dto.trade.Results.GatecoinTradeHistoryResult;
import com.xeiam.xchange.utils.DateUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Various adapters for converting from Gatecoin DTOs to XChange DTOs
 */
public final class GatecoinAdapters {

  /**
   * private Constructor
   */
  private GatecoinAdapters() {

  }

  /**
   * Adapts a GatecoinBalance to a AccountInfo
   *
   * @param gatecoinBalances
   * @param userName         The user name
   * @return The account info
   */
  public static AccountInfo adaptAccountInfo(GatecoinBalance[] gatecoinBalances, String userName) {

    ArrayList<Wallet> wallets = new ArrayList<Wallet>();

    for (GatecoinBalance balance : gatecoinBalances) {
      String ccy = balance.getCurrency();
      if (ccy.equalsIgnoreCase("btc") || ccy.equalsIgnoreCase("usd") || ccy.equalsIgnoreCase("hkd") || ccy.equalsIgnoreCase("eur")) {
        wallets.add(new Wallet(ccy, balance.getBalance(), balance.getAvailableBalance(), balance.getOpenOrder()));
      }
    }
    return new AccountInfo(userName, wallets);
  }

  /**
   * Adapts a com.xeiam.xchange.gatecoin.api.model.OrderBook to a OrderBook Object
   *
   * @param gatecoinDepthResult
   * @param currencyPair        (e.g. BTC/USD)
   * @param timeScale           polled order books provide a timestamp in seconds, stream in ms
   * @return The XChange OrderBook
   */
  public static OrderBook adaptOrderBook(
      GatecoinDepthResult gatecoinDepthResult,
      CurrencyPair currencyPair,
      int timeScale
  ) {

    List<LimitOrder> asks = createOrders(currencyPair, Order.OrderType.ASK, gatecoinDepthResult.getAsks());
    List<LimitOrder> bids = createOrders(currencyPair, Order.OrderType.BID, gatecoinDepthResult.getBids());
    Date date = new Date();
    return new OrderBook(date, asks, bids);
  }

  public static List<LimitOrder> createOrders(
      CurrencyPair currencyPair,
      Order.OrderType orderType,
      GatecoinDepth[] orders
  ) {

    List<LimitOrder> limitOrders = new ArrayList<LimitOrder>();
    for (GatecoinDepth priceVolume : orders) {

      limitOrders.add((createOrder(currencyPair, priceVolume, orderType)));
    }
    return limitOrders;
  }

  public static LimitOrder createOrder(
      CurrencyPair currencyPair,
      GatecoinDepth priceAndAmount,
      Order.OrderType orderType
  ) {

    return new LimitOrder(orderType, priceAndAmount.getVolume(), currencyPair, "", null, priceAndAmount.getPrice());
  }

  public static void checkArgument(boolean argument, String msgPattern, Object... msgArgs) {

    if (!argument) {
      throw new IllegalArgumentException(MessageFormat.format(msgPattern, msgArgs));
    }
  }

  public static Trades adaptTrades(GatecoinTransaction[] transactions, CurrencyPair currencyPair) {

    List<Trade> trades = new ArrayList<Trade>();
    long lastTradeId = 0;
    for (GatecoinTransaction tx : transactions) {
      final long tradeId = tx.getTransactionId();
      if (tradeId > lastTradeId) {
        lastTradeId = tradeId;
      }
      trades
          .add(new Trade(null, tx.getQuantity(), currencyPair, tx.getPrice(), DateUtils.fromMillisUtc(tx.getTransacationTime() * 1000L), String.valueOf(tradeId)));
    }
    return new Trades(trades, lastTradeId, TradeSortType.SortByID);
  }

  public static Ticker adaptTicker(GatecoinTicker[] gatecoinTickers, CurrencyPair currencyPair) {

    String ccyPair = currencyPair.toString().replace('/', ' ').replaceAll("\\s", "");
    for (GatecoinTicker ticker : gatecoinTickers) {
      String responseCcyPair = ticker.getCurrencyPair();
      if (responseCcyPair.compareTo(ccyPair) == 0) {
        BigDecimal last = ticker.getLast();
        BigDecimal bid = ticker.getBid();
        BigDecimal ask = ticker.getAsk();
        BigDecimal high = ticker.getHigh();
        BigDecimal low = ticker.getLow();
        BigDecimal vwap = ticker.getVwap();
        BigDecimal volume = ticker.getVolume();
        Date timestamp = new Date(ticker.getTimestamp() * 1000L);

        return new Ticker.Builder().currencyPair(currencyPair).last(last).bid(bid).ask(ask).high(high).low(low).vwap(vwap).volume(volume).timestamp(timestamp)
            .build();
      }
    }
    return null;
  }

  /**
   * Adapt the user's trades
   *
   * @param gatecoinUserTrades
   * @return
   */
  public static UserTrades adaptTradeHistory(GatecoinTradeHistoryResult gatecoinUserTrades) {

    List<UserTrade> trades = new ArrayList<UserTrade>();
    long lastTradeId = 0;
    if (gatecoinUserTrades != null) {
      GatecoinTradeHistory[] tradeHistory = gatecoinUserTrades.getTransactions();
      for (GatecoinTradeHistory gatecoinUserTrade : tradeHistory) {
        final boolean isAsk = Objects.equals(gatecoinUserTrade.getWay().toLowerCase(), "ask");
        OrderType orderType = isAsk ? OrderType.ASK : OrderType.BID;
        BigDecimal tradableAmount = gatecoinUserTrade.getQuantity();
        BigDecimal price = gatecoinUserTrade.getPrice();
        Date timestamp = GatecoinUtils.parseUnixTSToDateTime(gatecoinUserTrade.getTransactionTime());
        long transactionId = gatecoinUserTrade.getTransactionId();
        if (transactionId > lastTradeId) {
          lastTradeId = transactionId;
        }
        final String tradeId = String.valueOf(transactionId);
        final String orderId = isAsk ? gatecoinUserTrade.getAskOrderID() : gatecoinUserTrade.getBidOrderID();
        final BigDecimal feeRate = gatecoinUserTrade.getFeeRate();
        final BigDecimal feeAmount = feeRate.multiply(tradableAmount).multiply(price).setScale(8, BigDecimal.ROUND_CEILING);

        final CurrencyPair currencyPair = new CurrencyPair(gatecoinUserTrade.getCurrencyPair().substring(0, 3), gatecoinUserTrade.getCurrencyPair().substring(3, 6));
        UserTrade trade = new UserTrade(orderType, tradableAmount, currencyPair, price, timestamp, tradeId, orderId, feeAmount,
            currencyPair.counterSymbol);
        trades.add(trade);
      }
    }
    return new UserTrades(trades, lastTradeId, TradeSortType.SortByID);
  }
}
