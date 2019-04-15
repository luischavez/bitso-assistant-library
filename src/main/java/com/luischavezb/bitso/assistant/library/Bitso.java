package com.luischavezb.bitso.assistant.library;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luischavez
 */
public class Bitso {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bitso.class);

    public static final String UNKNOWN_ERROR = "0101";
    public static final String INVALID_RIPPLE_WITHDRAWAL = "0102";
    public static final String INVALID_NAME_OR_INVALID_CREDENTIALS = "0201";
    public static final String UNKNOWN_ORDER_BOOK = "0301";
    public static final String INCORRECT_TIME_FRAME = "0302";
    public static final String REQUIRED_FIELD_MISSING = "0303";
    public static final String REQUIRED_FIELD_NOT_VALID = "0304";
    public static final String INVALID_SMS_CODE = "0305";
    public static final String ORDER_SIDE_NOT_VALID = "0306";
    public static final String ORDER_TYPE_NOT_VALID = "0307";
    public static final String ORDER_REQUEST_INCLUDED_BOTH_MINOR_AND_MAJOR = "0308";
    public static final String ORDER_REQUEST_DOES_NOT_INCLUDE_NEITHER_MINOR_OR_MAJOR = "0309";
    public static final String INCORRECT_WID = "0310";
    public static final String INCORRECT_FID = "0311";
    public static final String INCORRECT_OID = "0312";
    public static final String SELECTED_CURRENCY_NOT_VALID = "0313";
    public static final String AUTO_TRADE_NOT_AVAILABLE_FOR_SELECTED_CURRENCIES = "0314";
    public static final String INVALID_ADDRESS = "0315";
    public static final String INVALID_RIPPLE_CURRENCY = "0316";
    public static final String INVALID_SPEI_NUMBER = "0317";
    public static final String INVALID_SPEI_NUMERIC_REF = "0318";
    public static final String INVALID_SPEI_NOTES_REF = "0319";
    public static final String INVALID_PAGINATION_PARAMETERS = "0320";
    public static final String INCORRECT_TID = "0321";
    public static final String NOT_A_VALID_URL = "0322";
    public static final String NO_ASSOCIATED_COUNTRY_CODE = "0323";
    public static final String NUMBER_ALREADY_IN_USE = "0324";
    public static final String PHONE_ALREADY_VERIFIED = "0325";
    public static final String INCORRECT_PRICE_BELOW_MINIMUM = "0401";
    public static final String INCORRECT_PRICE_ABOVE_MAXIMUM = "0402";
    public static final String INCORRECT_MAJOR_BELOW_MINIMUM = "0403";
    public static final String INCORRECT_MAJOR_ABOVE_MAXIMUM = "0404";
    public static final String INCORRECT_MINOR_BELOW_MINIMUM = "0405";
    public static final String INCORRECT_MINOR_ABOVE_MAXIMUM = "0406";
    public static final String INCORRECT_PRECISION = "0407";
    public static final String EXCEEDS_USER_LIMIT_FOR_WITHDRAWALS = "0501";
    public static final String NOT_ENOUGH_BTC_FUNDS = "0601";
    public static final String NOT_ENOUGH_MXN_FUNDS = "0602";
    public static final String YOU_HAVE_HIT_REQUEST_RATE_LIMIT = "0801";
    public static final String UNSUPPORTED_HTTP_METHOD = "0901";

    /**
     * Libros disponibles en la plataforma.
     */
    public static enum Book {

        BTC_MXN("Bitcoin", "BTC", "Pesos", "MXN"),
        ETH_MXN("Ethereum", "ETH", "Pesos", "MXN"),
        XRP_MXN("Ripple", "XRP", "Pesos", "MXN"),
        LTC_MXN("Litecoin", "LTC", "Pesos", "MXN"),
        XRP_BTC("Ripple", "XRP", "Bitcoin", "BTC"),
        ETH_BTC("Ethereum", "ETH", "Bitcoin", "BTC"),
        BCH_BTC("BCash", "BCH", "Bitcoin", "BTC"),
        LTC_BTC("Litecoin", "LTC", "Bitcoin", "BTC"),
        BCH_MXN("BCash", "BCH", "Pesos", "MXN"),
        TUSD_MXN("TrueUSD", "TUSD", "Pesos", "MXN"),
        TUSD_BTC("TrueUSD", "TUSD", "Bitcoin", "BTC"),
        MANA_MXN("Mana", "MANA", "Pesos", "MXN"),
        MANA_BTC("Mana", "MANA", "Bitcoin", "BTC"),
        GNT_MXN("Golem", "GNT", "Pesos", "MXN"),
        GNT_BTC("Golem", "GNT", "Bitcoin", "BTC"),
        BAT_MXN("Basic Attention Token", "BAT", "Pesos", "MXN"),
        BAT_BTC("Basic Attention Token", "BAT", "Bitcoin", "BTC");

        String majorName, majorCoin;
        String minorName, minorCoin;

        Book(String majorName, String majorCoin, String minorName, String minorCoin) {
            this.majorName = majorName;
            this.majorCoin = majorCoin;
            this.minorName = minorName;
            this.minorCoin = minorCoin;
        }

        public String majorName() {
            return majorName;
        }

        public String majorCoin() {
            return majorCoin;
        }

        public String minorName() {
            return minorName;
        }

        public String minorCoin() {
            return minorCoin;
        }

        public String majorLegend() {
            return String.format("%s (%s)", majorName, majorCoin);
        }

        public String minorLegend() {
            return String.format("%s (%s)", minorName, minorCoin);
        }
    }

    /**
     * HTTP METHOD.
     */
    protected enum HttpMethod {
        GET, POST, DELETE
    }

    /**
     * Respuesta de la api.
     */
    protected static class HttpResponse {

        public final HttpMethod httpMethod;
        public final String path;
        public final int code;
        public final String body;

        public HttpResponse(HttpMethod httpMethod, String path, int code, String body) {
            this.httpMethod = httpMethod;
            this.path = path;
            this.code = code;
            this.body = body;
        }

        @Override
        public String toString() {
            return String.format("[%d] %s", code, body);
        }
    }

    /**
     * Constructor para las respuestas de la api.
     *
     * @param <T>
     */
    protected interface ResponseBuilder<T> {

        T onSuccess(HttpResponse httpResponse);
    }

    /**
     * Credenciales de bitso.
     */
    public static class Credentials implements Serializable {

        /**
         * LLave publica de la api.
         */
        public final String key;

        /**
         * Clave secreta.
         */
        public final String secret;

        public Credentials(String key, String secret) {
            this.key = key;
            this.secret = secret;
        }
    }

    /**
     * Almacenamiento de las credenciales.
     */
    public interface Storage {

        /**
         * Obtiene las credenciales del almacenamiento.
         *
         * @return credenciales o null si no existen.
         */
        public Credentials loadCredentials();
    }

    /**
     * JSON.
     */
    private final JSONParser jsonParser = new JSONParser();

    /**
     * Direccion base para las peticiones.
     * <p>
     * https://api.bitso.com/v3/
     */
    private final String baseUrl;

    /**
     * Almacenamiento para uso interno.
     */
    private final Storage storage;

    /**
     * Tiempo permitido entre llamadas, 1 segundo en milisegundos.
     * <p>
     * 300 peticiones por cada 5 minutos 60 peticiones por minuto 1 peticion por
     * segundo
     */
    public static final long LIMIT_TIME_MILLIS = 1000;

    /**
     * Tiempo en milisegundos de la ultima peticion.
     */
    private long lastApiCallMillis;

    /**
     * Constructor.
     *
     * @param baseUrl direccion base de la api
     * @param storage almacenamiento
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public Bitso(String baseUrl, Storage storage) {
        if (null == baseUrl) {
            throw new NullPointerException("baseUrl is required");
        }

        this.baseUrl = baseUrl;

        if (null == storage) {
            throw new NullPointerException("storage is required");
        }

        this.storage = storage;

        lastApiCallMillis = System.currentTimeMillis();
    }

    /**
     * Url base de la api.
     *
     * @return url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Almacenamiento de las credenciales.
     *
     * @return almacenamiento
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Verifica si es posible realizar una peticion sin sobrepasar el limite.
     *
     * @return true si es posible realizar la peticion, false si no
     */
    public boolean availableCall() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - lastApiCallMillis;

        return LIMIT_TIME_MILLIS < elapsedTimeMillis;
    }

    /**
     * Esepara hasta que se pueda realizar una nueva llamada a la API.
     */
    public void waitAvailableCall() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - lastApiCallMillis;

        if (LIMIT_TIME_MILLIS > elapsedTimeMillis) {
            try {
                Thread.sleep(LIMIT_TIME_MILLIS - elapsedTimeMillis + 100);
            } catch (Exception ex) {
                LOGGER.error("", ex);
            }
        }
    }

    /**
     * Firma la peticion.
     *
     * @param httpMethod metodo http
     * @param path       ruta de la api
     * @param payload    datos e enviar
     * @param nonce      id unico de la peticion
     * @return firma
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    protected String signRequest(
            HttpMethod httpMethod, String path, String payload, long nonce)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String message = String.valueOf(nonce) + httpMethod.name() + path + payload;

        byte[] secretBytes = storage.loadCredentials().secret.getBytes();
        SecretKeySpec localMac = new SecretKeySpec(secretBytes, "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(localMac);

        byte[] arrayOfByte = mac.doFinal(message.getBytes());
        BigInteger localBigInteger = new BigInteger(1, arrayOfByte);

        return String.format("%0" + (arrayOfByte.length << 1) + "x",
                new Object[]{localBigInteger});
    }

    /**
     * Ejecuta una peticion a la api.
     *
     * @param privateApi indica si es una llamada privada
     * @param httpMethod metodo http
     * @param path       ruta de la api
     * @param payload    datos e enviar
     * @param apiRequest indica si es una peticion a la api
     * @return respuesta http
     * @throws BitsoRequestException
     * @throws RateLimitException
     */
    protected HttpResponse execute(
            boolean privateApi,
            HttpMethod httpMethod, String path, String payload, boolean apiRequest)
            throws BitsoRequestException, RateLimitException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[{}] {}: {}", httpMethod, path, payload);
        }

        if (privateApi) {
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - lastApiCallMillis;

            if (LIMIT_TIME_MILLIS > elapsedTimeMillis) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("RATE LIMIT EXCEEDED");
                }
                throw new RateLimitException();
            }
        }

        URL url;
        HttpURLConnection connection;

        try {
            url = new URL(apiRequest ? baseUrl + path : path);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod.name());

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(60000);
        } catch (MalformedURLException ex) {
            LOGGER.error("", ex);
            throw new BitsoRequestException("invalid url", ex);
        } catch (IOException ex) {
            LOGGER.error("", ex);
            throw new BitsoRequestException("can't open connection", ex);
        }

        connection.setRequestProperty("User-Agent", "Bitso Assistant");

        if (privateApi) {
            long nonce = System.currentTimeMillis() + System.currentTimeMillis();

            String signature;
            try {
                signature = signRequest(httpMethod, path, payload, nonce);
            } catch (InvalidKeyException ex) {
                LOGGER.error("", ex);
                throw new BitsoRequestException("can't sign request", ex);
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.error("", ex);
                throw new BitsoRequestException("can't sign request", ex);
            }

            String authHeader = String.format(
                    "Bitso %s:%s:%s",
                    storage.loadCredentials().key, nonce, signature);

            connection.setRequestProperty("Authorization", authHeader);
            connection.setRequestProperty("Content-Type", "application/json");
        }

        connection.setUseCaches(false);

        if (!payload.isEmpty()) {
            connection.setDoOutput(true);

            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(payload);
                outputStream.flush();
            } catch (IOException ex) {
                LOGGER.error("", ex);
                throw new BitsoRequestException("can't send request", ex);
            }
        }

        try {
            int responseCode = connection.getResponseCode();

            InputStream inputStream = connection.getErrorStream();

            if (null == inputStream) {
                inputStream = connection.getInputStream();
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
            }

            String responseBody = response.toString();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[{}] {}", path, responseCode);
            }

            return new HttpResponse(httpMethod, path, responseCode, responseBody);
        } catch (IOException ex) {
            LOGGER.error("", ex);
            throw new BitsoRequestException("can't execute request", ex);
        } finally {
            connection.disconnect();

            if (privateApi) {
                lastApiCallMillis = System.currentTimeMillis();
            }
        }
    }

    /**
     * Ejecuta una peticion a la api.
     *
     * @param privateApi indica si es una llamada privada
     * @param httpMethod metodo http
     * @param path       ruta de la api
     * @param payload    datos e enviar
     * @return respuesta http
     * @throws BitsoRequestException
     * @throws RateLimitException
     */
    protected HttpResponse execute(
            boolean privateApi,
            HttpMethod httpMethod, String path, String payload)
            throws BitsoRequestException, RateLimitException {
        return execute(privateApi, httpMethod, path, payload, true);
    }

    /**
     * Convierte un string json en un objeto.
     *
     * @param json
     * @return objeto
     */
    private JSONObject parseObject(String json) {
        try {
            return (JSONObject) jsonParser.parse(json);
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }

        return null;
    }

    /**
     * Convierte un string json en un arreglo.
     *
     * @param json
     * @return arreglo
     */
    private JSONArray parseArray(String json) {
        try {
            return (JSONArray) jsonParser.parse(json);
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }

        return null;
    }

    /**
     * Construye la respuesta a partir de los datos especificados.
     *
     * @param <T>        tipo de dato de la respuesta
     * @param privateApi indica si es una llamda privada
     * @param httpMethod metodo http
     * @param path       ruta de la api
     * @param payload    datos e enviar
     * @param builder    constructor de los datos de la respuesta
     * @return respuesta
     */
    protected <T> ApiResponse<T> buildApiResponse(
            boolean privateApi,
            HttpMethod httpMethod, String path, String payload,
            ResponseBuilder<T> builder) {
        try {
            HttpResponse httpResponse = execute(privateApi, httpMethod, path, payload);

            if (httpResponse.body.startsWith("{\"success\":false")) {
                JSONObject responseJsonObject = parseObject(httpResponse.body);
                JSONObject errorJsonObject = parseObject(responseJsonObject.get("error").toString());

                final String code = errorJsonObject.get("code").toString();
                final String message = errorJsonObject.get("message").toString();

                return new ApiResponse<>(code, message);
            } else if (httpResponse.body.startsWith("{\"success\":true")) {
                T o = builder.onSuccess(httpResponse);

                if (null == o) {
                    throw new RuntimeException(path + ": null response");
                }

                return new ApiResponse<>(o);
            }

            throw new RuntimeException("unknown response: " + httpResponse.code + ", " + path);
        } catch (Exception ex) {
            return new ApiResponse<>(ex);
        }
    }

    /**
     * Obtiene la informacion de todos los libros.
     *
     * @return informacin de todos los libros
     */
    public ApiResponse<List<Ticker>> tickers() {
        return buildApiResponse(false,
                HttpMethod.GET, "/api/v3/ticker",
                "", new ResponseBuilder<List<Ticker>>() {
                    @Override
                    public List<Ticker> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray payloadJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Ticker> tickers = new ArrayList<>();

                        for (Iterator iterator = payloadJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject payloadJsonObject = (JSONObject) iterator.next();

                            Book book;
                            try {
                                book = Book.valueOf(payloadJsonObject.get("book").toString().toUpperCase());
                            } catch (IllegalArgumentException ex) {
                                continue;
                            }

                            final BigDecimal volume = new BigDecimal(
                                    null == payloadJsonObject.get("volume") ?
                                            "0" : payloadJsonObject.get("volume").toString());
                            final BigDecimal vwap = new BigDecimal(
                                    null == payloadJsonObject.get("vwap") ?
                                            "0" : payloadJsonObject.get("vwap").toString());
                            final BigDecimal low = new BigDecimal(
                                    null == payloadJsonObject.get("low") ?
                                            "0" : payloadJsonObject.get("low").toString());
                            final BigDecimal high = new BigDecimal(
                                    null == payloadJsonObject.get("high") ?
                                            "0" : payloadJsonObject.get("high").toString());
                            final BigDecimal ask = new BigDecimal(
                                    null == payloadJsonObject.get("ask") ?
                                            "0" : payloadJsonObject.get("ask").toString());
                            final BigDecimal bid = new BigDecimal(
                                    null == payloadJsonObject.get("bid") ?
                                            "0" : payloadJsonObject.get("bid").toString());
                            final BigDecimal last = new BigDecimal(
                                    null == payloadJsonObject.get("last") ?
                                            "0" : payloadJsonObject.get("last").toString());
                            final Date createdAt = Utilities.parseDateTime(payloadJsonObject.get("created_at"));

                            tickers.add(new Ticker(book, volume, vwap, low, high, ask, bid, last, createdAt));
                        }

                        return tickers;
                    }
                });
    }

    /**
     * Obtiene la informacion de un libro especifico.
     *
     * @param book libro
     * @return informacin del libro
     */
    public ApiResponse<Ticker> ticker(final Book book) {
        return buildApiResponse(false,
                HttpMethod.GET, "/api/v3/ticker?book=" + book.name().toLowerCase(),
                "", new ResponseBuilder<Ticker>() {
                    @Override
                    public Ticker onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());

                        final BigDecimal volume = new BigDecimal(
                                null == payloadJsonObject.get("volume") ?
                                        "0" : payloadJsonObject.get("volume").toString());
                        final BigDecimal vwap = new BigDecimal(
                                null == payloadJsonObject.get("vwap") ?
                                        "0" : payloadJsonObject.get("vwap").toString());
                        final BigDecimal low = new BigDecimal(
                                null == payloadJsonObject.get("low") ?
                                        "0" : payloadJsonObject.get("low").toString());
                        final BigDecimal high = new BigDecimal(
                                null == payloadJsonObject.get("high") ?
                                        "0" : payloadJsonObject.get("high").toString());
                        final BigDecimal ask = new BigDecimal(
                                null == payloadJsonObject.get("ask") ?
                                        "0" : payloadJsonObject.get("ask").toString());
                        final BigDecimal bid = new BigDecimal(
                                null == payloadJsonObject.get("bid") ?
                                        "0" : payloadJsonObject.get("bid").toString());
                        final BigDecimal last = new BigDecimal(
                                null == payloadJsonObject.get("last") ?
                                        "0" : payloadJsonObject.get("last").toString());
                        final Date createdAt = Utilities.parseDateTime(payloadJsonObject.get("created_at"));

                        return new Ticker(book, volume, vwap, low, high, ask, bid, last, createdAt);
                    }
                });
    }

    /**
     * Obtiene todos los retiros de la cuenta o los especificados.
     *
     * @param wids retiros a recuperar
     * @return retiros
     */
    public ApiResponse<List<Withdrawal>> withdrawals(String... wids) {
        String q = "";

        for (String wid : wids) {
            if (!q.isEmpty()) {
                q += "-";
            }
            q += wid;
        }

        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/withdrawals/" + q, "", new ResponseBuilder<List<Withdrawal>>() {
                    @Override
                    public List<Withdrawal> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray withdrawalsJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Withdrawal> withdrawals = new ArrayList<>();

                        for (Iterator iterator = withdrawalsJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject withdrawalJsonObject = (JSONObject) iterator.next();

                            final String wid
                                    = withdrawalJsonObject.get("wid").toString();
                            final String status
                                    = withdrawalJsonObject.get("status").toString();
                            final String currency
                                    = withdrawalJsonObject.get("currency").toString();
                            final String method
                                    = withdrawalJsonObject.get("method").toString();
                            final BigDecimal amount = new BigDecimal(
                                    withdrawalJsonObject.get("amount").toString());
                            final String details
                                    = withdrawalJsonObject.get("details").toString();
                            final Date createdAt = Utilities.parseDateTime(withdrawalJsonObject.get("created_at"));

                            withdrawals.add(new Withdrawal(wid, status, currency, method, amount, details, createdAt));
                        }

                        return withdrawals;
                    }
                });
    }

    /**
     * Obtiene todos los fondeos de la cuento o los especificados.
     *
     * @param fids fondeos a recuperar
     * @return fondeos
     */
    public ApiResponse<List<Funding>> fundings(String... fids) {
        String q = "";

        for (String fid : fids) {
            if (!q.isEmpty()) {
                q += "-";
            }
            q += fid;
        }

        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/fundings/" + q, "", new ResponseBuilder<List<Funding>>() {
                    @Override
                    public List<Funding> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray fundingsJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Funding> fundings = new ArrayList<>();

                        for (Iterator iterator = fundingsJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject fundingJsonObject = (JSONObject) iterator.next();

                            final String fid
                                    = fundingJsonObject.get("fid").toString();
                            final String status
                                    = fundingJsonObject.get("status").toString();
                            final String currency
                                    = fundingJsonObject.get("currency").toString();
                            final String method
                                    = fundingJsonObject.get("method").toString();
                            final BigDecimal amount = new BigDecimal(
                                    fundingJsonObject.get("amount").toString());
                            final String details
                                    = fundingJsonObject.get("details").toString();
                            final Date createdAt = Utilities.parseDateTime(fundingJsonObject.get("created_at"));

                            fundings.add(new Funding(fid, status, currency, method, amount, details, createdAt));
                        }

                        return fundings;
                    }
                });
    }

    /**
     * Obtiene los trades de la cuenta, opcionalmente se puede especificar a partir de cual trade se iniciara la
     * recuperacion de la informacion.
     *
     * @param marker trade a partir del cual se recuperara la informacion
     * @return trades
     */
    public ApiResponse<List<Trade>> trades(String marker) {
        String q = "?sort=asc&limit=100";

        if (null != marker) {
            q += "&marker=" + marker;
        }

        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/user_trades" + q, "", new ResponseBuilder<List<Trade>>() {
                    @Override
                    public List<Trade> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray tradesJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Trade> trades = new ArrayList<>();

                        for (Iterator iterator = tradesJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject tradeJsonObject = (JSONObject) iterator.next();

                            Book book;
                            try {
                                book = Book.valueOf(tradeJsonObject.get("book").toString().toUpperCase());
                            } catch (IllegalArgumentException ex) {
                                continue;
                            }

                            final BigDecimal major = new BigDecimal(
                                    tradeJsonObject.get("major").toString());
                            final BigDecimal minor = new BigDecimal(
                                    tradeJsonObject.get("minor").toString());
                            final BigDecimal feesAmount = new BigDecimal(
                                    tradeJsonObject.get("fees_amount").toString());
                            final String feesCurrency
                                    = tradeJsonObject.get("fees_currency").toString();
                            final BigDecimal price = new BigDecimal(
                                    tradeJsonObject.get("price").toString());
                            final String tid
                                    = tradeJsonObject.get("tid").toString();
                            final String oid
                                    = tradeJsonObject.get("oid").toString();
                            final String side
                                    = tradeJsonObject.get("side").toString();
                            final Date createdAt = Utilities.parseDateTime(tradeJsonObject.get("created_at"));

                            trades.add(new Trade(book, major, minor, feesAmount, feesCurrency, price, tid, oid, side, createdAt));
                        }

                        return trades;
                    }
                });
    }

    /**
     * Obtiene las ordenes abiertas del libro especificado.
     *
     * @param book libro
     * @return ordenes
     */
    public ApiResponse<List<Order>> orders(final Book book) {
        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/open_orders?book=" + book.name().toLowerCase(),
                "", new ResponseBuilder<List<Order>>() {
                    @Override
                    public List<Order> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray ordersJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Order> orders = new ArrayList<>();

                        for (Iterator iterator = ordersJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject orderJsonObject = (JSONObject) iterator.next();

                            final BigDecimal originalAmount = new BigDecimal(
                                    orderJsonObject.get("original_amount").toString());
                            final BigDecimal unfilledAmount = new BigDecimal(
                                    orderJsonObject.get("unfilled_amount").toString());
                            final BigDecimal originalValue = new BigDecimal(
                                    orderJsonObject.get("original_value").toString());
                            final BigDecimal price = new BigDecimal(
                                    orderJsonObject.get("price").toString());
                            final String oid
                                    = orderJsonObject.get("oid").toString();
                            final String side
                                    = orderJsonObject.get("side").toString();
                            final String status
                                    = orderJsonObject.get("status").toString();
                            final String type
                                    = orderJsonObject.get("type").toString();
                            final Date createdAt = Utilities.parseDateTime(orderJsonObject.get("created_at"));
                            final Date updatedAt = Utilities.parseDateTime(orderJsonObject.get("updated_at"));

                            orders.add(new Order(book, originalAmount, unfilledAmount,
                                    originalValue, price, oid, side, status, type, createdAt, updatedAt));
                        }

                        return orders;
                    }
                });
    }

    /**
     * Obtiene la orden asociada al oid.
     *
     * @param oid id de la orden
     * @return orden
     */
    public ApiResponse<Order> order(String oid) {
        return buildApiResponse(true, HttpMethod.GET, "/api/v3/orders/" + oid, "", new ResponseBuilder<Order>() {
            @Override
            public Order onSuccess(HttpResponse httpResponse) {
                JSONObject responseJsonObject = parseObject(httpResponse.body);
                JSONArray payloadJsonArray
                        = parseArray(responseJsonObject.get("payload").toString());

                if (0 == payloadJsonArray.size()) return null;

                JSONObject orderJsonObject = (JSONObject) payloadJsonArray.get(0);

                Book book;
                try {
                    book = Book.valueOf(orderJsonObject.get("book").toString().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    return null;
                }

                final BigDecimal originalAmount = new BigDecimal(
                        orderJsonObject.get("original_amount").toString());
                final BigDecimal unfilledAmount = new BigDecimal(
                        orderJsonObject.get("unfilled_amount").toString());
                final BigDecimal originalValue = new BigDecimal(
                        orderJsonObject.get("original_value").toString());
                final BigDecimal price = new BigDecimal(
                        orderJsonObject.get("price").toString());
                final String oid
                        = orderJsonObject.get("oid").toString();
                final String side
                        = orderJsonObject.get("side").toString();
                final String status
                        = orderJsonObject.get("status").toString();
                final String type
                        = orderJsonObject.get("type").toString();
                final Date createdAt = Utilities.parseDateTime(orderJsonObject.get("created_at"));
                final Date updatedAt = Utilities.parseDateTime(orderJsonObject.get("updated_at"));

                return new Order(book, originalAmount, unfilledAmount,
                        originalValue, price, oid, side, status, type, createdAt, updatedAt);
            }
        });
    }

    /**
     * Obtiene las ordenes asociadas a los oids.
     *
     * @param oids ids de las ordenes
     * @return ordenes
     */
    public ApiResponse<List<Order>> orders(String... oids) {
        String q = "";

        for (String oid : oids) {
            if (!q.isEmpty()) {
                q += "-";
            }

            q += oid;
        }

        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/orders/" + q, "", new ResponseBuilder<List<Order>>() {
                    @Override
                    public List<Order> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray payloadJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        ArrayList<Order> orders = new ArrayList<>();

                        for (Iterator iterator = payloadJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject orderJsonObject = (JSONObject) iterator.next();

                            Book book;
                            try {
                                book = Book.valueOf(orderJsonObject.get("book").toString().toUpperCase());
                            } catch (IllegalArgumentException ex) {
                                continue;
                            }

                            final BigDecimal originalAmount = new BigDecimal(
                                    orderJsonObject.get("original_amount").toString());
                            final BigDecimal unfilledAmount = new BigDecimal(
                                    orderJsonObject.get("unfilled_amount").toString());
                            final BigDecimal originalValue = new BigDecimal(
                                    orderJsonObject.get("original_value").toString());
                            final BigDecimal price = new BigDecimal(
                                    orderJsonObject.get("price").toString());
                            final String oid
                                    = orderJsonObject.get("oid").toString();
                            final String side
                                    = orderJsonObject.get("side").toString();
                            final String status
                                    = orderJsonObject.get("status").toString();
                            final String type
                                    = orderJsonObject.get("type").toString();
                            final Date createdAt = Utilities.parseDateTime(orderJsonObject.get("created_at"));
                            final Date updatedAt = Utilities.parseDateTime(orderJsonObject.get("updated_at"));

                            orders.add(new Order(book, originalAmount, unfilledAmount,
                                    originalValue, price, oid, side, status, type, createdAt, updatedAt));
                        }

                        return orders;
                    }
                });
    }

    /**
     * Pone un nueva orden.
     *
     * @param book        libro
     * @param side        compra o venta
     * @param type        limitada
     * @param majorAmount
     * @param minorAmount
     * @param price       precio
     * @return oid de la orden
     */
    protected ApiResponse<String> placeOrder(Book book, String side, String type,
                                             BigDecimal majorAmount, BigDecimal minorAmount, BigDecimal price) {
        JSONObject payloadJsonObject = new JSONObject();
        payloadJsonObject.put("book", book.name().toLowerCase());
        payloadJsonObject.put("side", side);
        payloadJsonObject.put("type", type);
        payloadJsonObject.put("price", price.toPlainString());

        if (null != majorAmount) payloadJsonObject.put("major", majorAmount.toPlainString());
        if (null != minorAmount) payloadJsonObject.put("minor", minorAmount.toPlainString());

        String payload = payloadJsonObject.toJSONString();

        return buildApiResponse(true, HttpMethod.POST, "/api/v3/orders", payload, new ResponseBuilder<String>() {
            @Override
            public String onSuccess(HttpResponse httpResponse) {
                JSONObject responseJsonObject = parseObject(httpResponse.body);
                JSONObject payloadJsonObject
                        = parseObject(responseJsonObject.get("payload").toString());

                return payloadJsonObject.get("oid").toString();
            }
        });
    }

    /**
     * Pone una nueva orden de compra.
     *
     * @param book   libro
     * @param amount cantidad
     * @param price  precio por unidad
     * @param minor  indica si la cantidad esta expresada en terminos de minor
     * @return oid de la orden
     */
    public ApiResponse<String> buy(Book book, BigDecimal amount, BigDecimal price, boolean minor) {
        if (minor) {
            if ("XRP".equals(book.majorCoin())) {
                amount = amount.divide(price, 6, RoundingMode.DOWN);
            } else {
                amount = amount.divide(price, 8, RoundingMode.DOWN);
            }
        } else {
            if ("XRP".equals(book.majorCoin())) {
                amount = amount.setScale(6, RoundingMode.DOWN);
            } else {
                amount = amount.setScale(8, RoundingMode.DOWN);
            }
        }

        if ("MXN".equals(book.minorCoin())) {
            price.setScale(2, RoundingMode.DOWN);
        } else {
            price.setScale(8, RoundingMode.DOWN);
        }

        return placeOrder(book, "buy", "limit", amount, null, price);
    }

    /**
     * Pone una nueva orden de venta.
     *
     * @param book   libro
     * @param amount cantidad
     * @param price  precio por unidad
     * @param minor  indica si la cantidad esta expresada en terminos de minor
     * @return oid de la orden
     */
    public ApiResponse<String> sell(Book book, BigDecimal amount, BigDecimal price, boolean minor) {
        if (minor) {
            if ("XRP".equals(book.majorCoin())) {
                amount = amount.divide(price, 6, RoundingMode.DOWN);
            } else {
                amount = amount.divide(price, 8, RoundingMode.DOWN);
            }
        } else {
            if ("XRP".equals(book.majorCoin())) {
                amount = amount.setScale(6, RoundingMode.DOWN);
            } else {
                amount = amount.setScale(8, RoundingMode.DOWN);
            }
        }

        if ("MXN".equals(book.minorCoin())) {
            price.setScale(2, RoundingMode.UP);
        } else {
            price.setScale(8, RoundingMode.UP);
        }

        return placeOrder(book, "sell", "limit",
                amount, null,
                price);
    }

    /**
     * Cancela la orden con el oid especificado.
     *
     * @param oid id de la orden
     * @return oid de la orden
     */
    public ApiResponse<String> cancel(String oid) {
        return buildApiResponse(true, HttpMethod.DELETE, "/api/v3/orders/" + oid,
                "", new ResponseBuilder<String>() {
                    @Override
                    public String onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONArray payloadJsonArray
                                = parseArray(responseJsonObject.get("payload").toString());

                        return payloadJsonArray.get(0).toString();
                    }
                });
    }

    /**
     * Obtiene el balance de la cuenta.
     *
     * @return balance
     */
    public ApiResponse<List<Balance>> balances() {
        return buildApiResponse(true, HttpMethod.GET, "/api/v3/balance", "",
                new ResponseBuilder<List<Balance>>() {
                    @Override
                    public List<Balance> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());
                        JSONArray balancesJsonArray
                                = parseArray(payloadJsonObject.get("balances").toString());

                        ArrayList<Balance> balances = new ArrayList<>();

                        for (Iterator iterator = balancesJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject balanceJsonObject = (JSONObject) iterator.next();

                            final String currency
                                    = balanceJsonObject.get("currency").toString();
                            final BigDecimal available = new BigDecimal(
                                    balanceJsonObject.get("available").toString());
                            final BigDecimal locked = new BigDecimal(
                                    balanceJsonObject.get("locked").toString());
                            final BigDecimal total = new BigDecimal(
                                    balanceJsonObject.get("total").toString());

                            balances.add(new Balance(currency, available, locked, total));
                        }

                        return balances;
                    }
                });
    }

    /**
     * Obtiene la lista de comisiones.
     *
     * @return comiciones
     */
    public ApiResponse<List<Fee>> fees() {
        return buildApiResponse(true, HttpMethod.GET, "/api/v3/fees", "",
                new ResponseBuilder<List<Fee>>() {
                    @Override
                    public List<Fee> onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());
                        JSONArray feesJsonArray
                                = parseArray(payloadJsonObject.get("fees").toString());

                        ArrayList<Fee> fees = new ArrayList<>();

                        for (Iterator iterator = feesJsonArray.iterator(); iterator.hasNext(); ) {
                            JSONObject feeJsonObject = (JSONObject) iterator.next();

                            Book book;
                            try {
                                book = Book.valueOf(feeJsonObject.get("book").toString().toUpperCase());
                            } catch (IllegalArgumentException ex) {
                                continue;
                            }

                            final BigDecimal decimal = new BigDecimal(
                                    feeJsonObject.get("fee_decimal").toString());
                            final BigDecimal percent = new BigDecimal(
                                    feeJsonObject.get("fee_percent").toString());

                            fees.add(new Fee(book, decimal, percent));
                        }

                        return fees;
                    }
                });
    }

    /**
     * Obtiene la direccion para fondear la moneda.
     *
     * @param currency moneda
     * @return direccion
     */
    public ApiResponse<FundingDestination> fundingDestination(final String currency) {
        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/funding_destination?fund_currency=" + currency, "",
                new ResponseBuilder<FundingDestination>() {
                    @Override
                    public FundingDestination onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());

                        final String id = payloadJsonObject.get("account_identifier_name").toString();
                        final String account = payloadJsonObject.get("account_identifier").toString();

                        return new FundingDestination(currency, id, account);
                    }
                });
    }

    /**
     * Establece el telefono a confirmar.
     *
     * @param phoneNumber telefono
     * @return telefono
     */
    public ApiResponse<String> phoneNumber(String phoneNumber) {
        JSONObject payloadJsonObject = new JSONObject();
        payloadJsonObject.put("phone_number", phoneNumber);

        final String payload = payloadJsonObject.toJSONString();

        return buildApiResponse(true, HttpMethod.POST,
                "/api/v3/phone_number", payload, new ResponseBuilder<String>() {
                    @Override
                    public String onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());

                        return payloadJsonObject.get("phone").toString();
                    }
                });
    }

    /**
     * Envia el codigo de confirmacion del telefono.
     *
     * @param code codigo de confirmacion
     * @return numero de telefono confirmado
     */
    public ApiResponse<String> phoneVerification(String code) {
        JSONObject payloadJsonObject = new JSONObject();
        payloadJsonObject.put("verification_code", code);

        final String payload = payloadJsonObject.toJSONString();

        return buildApiResponse(true, HttpMethod.POST,
                "/api/v3/phone_verification", payload, new ResponseBuilder<String>() {
                    @Override
                    public String onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());

                        return payloadJsonObject.get("phone").toString();
                    }
                });
    }

    /**
     * Obtiene la infromacion y estatus de la cuenta.
     *
     * @return informacion y estatus
     */
    public ApiResponse<AccountStatus> accountStatus() {
        return buildApiResponse(true, HttpMethod.GET,
                "/api/v3/account_status", "", new ResponseBuilder<AccountStatus>() {
                    @Override
                    public AccountStatus onSuccess(HttpResponse httpResponse) {
                        JSONObject responseJsonObject = parseObject(httpResponse.body);
                        JSONObject payloadJsonObject
                                = parseObject(responseJsonObject.get("payload").toString());

                        final long clientId = Long.valueOf(payloadJsonObject.get("client_id").toString());
                        final String firstName = payloadJsonObject.get("first_name").toString();
                        final String lastName = payloadJsonObject.get("last_name").toString();
                        final String status = payloadJsonObject.get("status").toString();
                        final BigDecimal dailyLimit = new BigDecimal(payloadJsonObject.get("daily_limit").toString());
                        final BigDecimal monthlyLimit = new BigDecimal(payloadJsonObject.get("monthly_limit").toString());
                        final BigDecimal dailyRemaining = new BigDecimal(payloadJsonObject.get("daily_remaining").toString());
                        final BigDecimal monthlyRemaining = new BigDecimal(payloadJsonObject.get("monthly_remaining").toString());
                        final String cellphoneNumber = payloadJsonObject.get("cellphone_number").toString();
                        final String cellphoneNumberStored = payloadJsonObject.get("cellphone_number_stored").toString();
                        final String emailStored = payloadJsonObject.get("email_stored").toString();
                        final String officialId = payloadJsonObject.get("official_id").toString();
                        final String proofOfResidency = payloadJsonObject.get("proof_of_residency").toString();
                        final String signedContract = payloadJsonObject.get("signed_contract").toString();
                        final String originOfFunds = payloadJsonObject.get("origin_of_funds").toString();

                        return new AccountStatus(clientId, firstName, lastName, status,
                                dailyLimit, monthlyLimit, dailyRemaining, monthlyRemaining,
                                cellphoneNumber, cellphoneNumberStored, emailStored,
                                officialId, proofOfResidency, signedContract, originOfFunds);
                    }
                });
    }

    /**
     * Obtiene el historial de precios del libro especificado.
     *
     * @param book libro
     * @return historial
     */
    public List<History> history(Book book, String afterDate) {
        try {
            String major = book.majorCoin().toLowerCase();
            String minor = book.minorCoin().toLowerCase();

            String url = String.format("https://bitso.com/trade/market/%s/%s", major, minor);

            HttpResponse httpResponse = execute(false, HttpMethod.GET, url, "", false);

            if (200 != httpResponse.code) {
                return null;
            }

            Pattern pattern = null == afterDate
                    ? Pattern.compile("chartData = (?<data>\\[.+\\])")
                    : Pattern.compile(
                    String.format("chartData = \\[.*%s(?<data>.*)\\]", afterDate.replace("-", "\\-")));
            Matcher matcher = pattern.matcher(httpResponse.body);

            ArrayList<History> histories = new ArrayList<>();

            if (matcher.find()) {
                String data = matcher.group(1);

                if (null == data || data.isEmpty()) {
                    return null;
                }

                if (null != afterDate) {
                    String[] parts = data.split("\\},", 2);

                    if (1 == parts.length) {
                        return histories;
                    } else {
                        data = String.format("[%s]", parts[1].trim());
                    }
                }

                JSONArray dataJsonArray = parseArray(data);

                if (null == dataJsonArray) {
                    return null;
                }

                for (Iterator iterator = dataJsonArray.iterator(); iterator.hasNext(); ) {
                    JSONObject historyJsonObject = (JSONObject) iterator.next();

                    final String date = historyJsonObject.get("date").toString();
                    final String dated = historyJsonObject.get("dated").toString();
                    final BigDecimal value = new BigDecimal(historyJsonObject.get("value").toString());
                    final BigDecimal volume = new BigDecimal(historyJsonObject.get("volume").toString());
                    final BigDecimal open = new BigDecimal(historyJsonObject.get("open").toString());
                    final BigDecimal low = new BigDecimal(historyJsonObject.get("low").toString());
                    final BigDecimal high = new BigDecimal(historyJsonObject.get("high").toString());
                    final BigDecimal close = new BigDecimal(historyJsonObject.get("close").toString());
                    final BigDecimal vwap = new BigDecimal(historyJsonObject.get("vwap").toString());

                    histories.add(new History(book, date, dated, value, volume, open, low, high, close, vwap));
                }
            }

            return histories;
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }

        return null;
    }
}
