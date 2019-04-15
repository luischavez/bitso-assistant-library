package com.luischavezb.bitso.assistant.library;

import java.math.BigDecimal;
import java.util.List;

public interface AssistantListener {

    void onError(String code, String message);

    void onException(Exception exception);

    void onTickers(List<Ticker> tickers);

    void onInfo(List<Fee> fees, List<Balance> balances);

    void onPlaceBuy(Profile profile, String oid,
                    BigDecimal amount, BigDecimal price,
                    Balance majorBalance, Balance minorBalance, Fee fee);

    void onPlaceSell(Profile profile, String oid,
                     BigDecimal amount, BigDecimal price,
                     Balance majorBalance, Balance minorBalance, Fee fee);

    void onChangedData();
}
