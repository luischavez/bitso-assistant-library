package com.luischavezb.bitso.assistant.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luischavez
 */
public abstract class Assistant implements AssistantListener, Bitso.Storage {

    private static final Logger LOGGER = LoggerFactory.getLogger(Assistant.class);

    /**
     * Instancia de la api.
     */
    private final Bitso bitso;

    /**
     * Listeners.
     */
    private final List<AssistantListener> listeners;

    /**
     * Informacion del asistente.
     */
    private Data data;

    public Assistant(String baseUrl) {
        this.bitso = new Bitso(baseUrl, this);
        this.listeners = new ArrayList<>();
    }

    protected abstract Data readData();

    protected abstract void saveData(Data data);

    public Bitso bitso() {
        return bitso;
    }

    public void addListener(AssistantListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AssistantListener listener) {
        listeners.remove(listener);
    }

    public void saveData() {
        saveData(data);
    }

    public Data getData() {
        if (null == data) {
            data = readData();
        }

        return data;
    }

    public Profile getProfile(long id) {
        if (null == data) {
            data = readData();
        }

        for (Profile profile : data.getProfiles()) {
            if (profile.getId() == id) {
                return profile;
            }
        }

        return null;
    }

    public void addProfile(Profile profile) {
        if (null == data) {
            data = readData();
        }

        data.getProfiles().add(profile);

        saveData();

        onChangedData();
    }

    public void removeProfile(Profile profile) {
        if (null == data) {
            data = readData();
        }

        data.getProfiles().remove(profile);

        saveData();

        onChangedData();
    }

    @Override
    public void onError(String code, String message) {
        for (AssistantListener listener : listeners) {
            listener.onError(code, message);
        }
    }

    @Override
    public void onException(Exception exception) {
        for (AssistantListener listener : listeners) {
            listener.onException(exception);
        }
    }

    @Override
    public void onTickers(List<Ticker> tickers) {
        for (AssistantListener listener : listeners) {
            listener.onTickers(tickers);
        }
    }

    @Override
    public void onInfo(List<Fee> fees, List<Balance> balances) {
        for (AssistantListener listener : listeners) {
            listener.onInfo(fees, balances);
        }
    }

    @Override
    public void onPlaceBuy(Profile profile, String oid,
                           BigDecimal amount, BigDecimal price,
                           Balance majorBalance, Balance minorBalance, Fee fee) {
        for (AssistantListener listener : listeners) {
            listener.onPlaceBuy(profile, oid, amount, price, majorBalance, minorBalance, fee);
        }
    }

    @Override
    public void onPlaceSell(Profile profile, String oid,
                            BigDecimal amount, BigDecimal price,
                            Balance majorBalance, Balance minorBalance, Fee fee) {
        for (AssistantListener listener : listeners) {
            listener.onPlaceSell(profile, oid, amount, price, majorBalance, minorBalance, fee);
        }
    }

    @Override
    public void onChangedData() {
        for (AssistantListener listener : listeners) {
            listener.onChangedData();
        }
    }

    public <T> T resolve(ApiResponse<T> apiResponse) {
        if (apiResponse.success()) {
            return apiResponse.object();
        }

        if (null != apiResponse.exception()) {
            LOGGER.error("", apiResponse.exception());

            onException(apiResponse.exception());
        }

        if (null != apiResponse.errorCode()) {
            onError(apiResponse.errorCode(), apiResponse.errorMessage());
        }

        return null;
    }

    protected List<Balance> resolveBalances() {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<List<Balance>> balances = bitso.balances();

            return resolve(balances);
        }
    }

    protected List<Fee> resolveFees() {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<List<Fee>> fees = bitso.fees();

            return resolve(fees);
        }
    }

    protected List<Ticker> resolveTickers() {
        ApiResponse<List<Ticker>> tickers = bitso.tickers();

        return resolve(tickers);
    }

    protected List<Order> resolveOrders(Bitso.Book book) {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<List<Order>> orders = bitso.orders(book);

            return resolve(orders);
        }
    }

    protected Order resolveOrder(String oid) {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<Order> order = bitso.order(oid);

            return resolve(order);
        }
    }

    protected String buy(Bitso.Book book, BigDecimal amount, BigDecimal price) {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<String> buy = bitso.buy(book, amount, price, true);

            return resolve(buy);
        }
    }

    protected String sell(Bitso.Book book, BigDecimal amount, BigDecimal price) {
        synchronized (bitso) {
            bitso.waitAvailableCall();

            ApiResponse<String> sell = bitso.sell(book, amount, price, false);

            return resolve(sell);
        }
    }

    protected void singleBuy(Profile profile, Balance majorBalance, Balance minorBalance, Ticker ticker, Fee fee) {
        boolean automatic = profile.isAutomatic();

        long currentTime = System.currentTimeMillis();
        long lastBuyTime = profile.getLastOperationTime();
        long timeBetweenBuys = profile.getTimeBetweenOperations();

        if (0L != lastBuyTime) {
            if ((currentTime - lastBuyTime) < timeBetweenBuys) {
                return;
            }
        }

        if (automatic && 0 != profile.getCurrentPrice().compareTo(profile.getPrice())) {
            long timeResetPrice = profile.getTimeResetPrice();

            if ((currentTime - lastBuyTime) >= timeResetPrice) {
                profile.setCurrentPrice(profile.getPrice());
            }
        }

        BigDecimal buyIfBelow = profile.getPrice();

        if (profile.isUseFee()) {
            buyIfBelow =
                    buyIfBelow.add(fee.getPercent()
                            .multiply(buyIfBelow)
                            .divide(new BigDecimal("100"), BigDecimal.ROUND_UP));
        }

        BigDecimal price = ticker.getAsk();

        if (-1 == buyIfBelow.compareTo(price)) {
            return;
        }

        BigDecimal amount = profile.getMaxAmount();

        if (-1 == minorBalance.getAvailable().compareTo(amount)) {
            amount = minorBalance.getAvailable();
        }

        if (0 == amount.compareTo(BigDecimal.ZERO)) {
            return;
        }

        Bitso.Book book = profile.getBook();

        String oid = buy(book, amount, price);

        if (null != oid) {
            profile.setLastOperationTime(System.currentTimeMillis());

            if (profile.isDisable()) {
                profile.setEnabled(false);
            }

            if (automatic) {
                profile.setCurrentPrice(price);
            }

            onPlaceBuy(profile, oid, amount, price, majorBalance, minorBalance, fee);
        }
    }

    protected void singleSell(Profile profile, Balance majorBalance, Balance minorBalance, Ticker ticker, Fee fee) {
        boolean automatic = profile.isAutomatic();

        long currentTime = System.currentTimeMillis();
        long lastSellTime = profile.getLastOperationTime();
        long timeBetweenSells = profile.getTimeBetweenOperations();

        if (0L != lastSellTime) {
            if ((currentTime - lastSellTime) < timeBetweenSells) {
                return;
            }
        }

        if (automatic && 0 != profile.getCurrentPrice().compareTo(profile.getPrice())) {
            long timeResetPrice = profile.getTimeResetPrice();

            if ((currentTime - lastSellTime) >= timeResetPrice) {
                profile.setCurrentPrice(profile.getPrice());
            }
        }

        BigDecimal sellAt = profile.getPrice();

        if (profile.isUseFee()) {
            sellAt =
                    sellAt.add(fee.getPercent()
                            .multiply(sellAt)
                            .divide(new BigDecimal("100"), BigDecimal.ROUND_UP));
        }

        BigDecimal price = ticker.getBid();

        if (-1 == price.compareTo(sellAt)) {
            return;
        }

        BigDecimal amount = profile.getMaxAmount();

        if (-1 == majorBalance.getAvailable().compareTo(amount)) {
            amount = majorBalance.getAvailable();
        }

        if (0 == amount.compareTo(BigDecimal.ZERO)) {
            return;
        }

        Bitso.Book book = profile.getBook();

        String oid = sell(book, amount, price);

        if (null != oid) {
            profile.setLastOperationTime(System.currentTimeMillis());

            if (profile.isDisable()) {
                profile.setEnabled(false);
            }

            if (automatic) {
                profile.setCurrentPrice(price);
            }

            onPlaceSell(profile, oid, amount, price, majorBalance, minorBalance, fee);
        }
    }

    public List<Ticker> tick(boolean useProfiles) {
        try {
            if (!useProfiles) {
                List<Ticker> tickers = resolveTickers();
                onTickers(tickers);

                return tickers;
            }
        } catch (Exception ex) {
            LOGGER.error("", ex);

            onException(ex);

            return null;
        }

        try {
            data = getData();

            List<Balance> balances = resolveBalances();
            if (null == balances) return null;
            data.setBalances(balances);

            List<Fee> fees = resolveFees();
            if (null == fees) return null;
            data.setFees(fees);

            List<Ticker> tickers = resolveTickers();
            if (null == tickers) return null;
            data.setTickers(tickers);

            onTickers(tickers);
            onInfo(fees, balances);

            for (Profile profile : data.getProfiles()) {
                if (!profile.isEnabled()) continue;

                Bitso.Book book = profile.getBook();

                Fee fee = data.feeOfBook(book);

                Balance[] balancesOfBook = data.balancesOfBook(book);
                Balance majorBalance = balancesOfBook[0];
                Balance minorBalance = balancesOfBook[1];

                Ticker ticker = data.tickerOfBook(book);

                switch (profile.getType()) {
                    case BUY:
                        singleBuy(profile, majorBalance, minorBalance, ticker, fee);
                        break;
                    case SELL:
                        singleSell(profile, majorBalance, minorBalance, ticker, fee);
                        break;
                }
            }

            saveData();
            onChangedData();

            return tickers;
        } catch (Exception ex) {
            LOGGER.error("", ex);

            onException(ex);
        }

        return null;
    }
}
