package com.luischavezb.bitso.assistant.library;

import com.neovisionaries.ws.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BitsoWebSocketClient implements WebSocketListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitsoWebSocketClient.class);

    public static final String WS_URL = "wss://ws.bitso.com";

    /**
     * JSON.
     */
    private final JSONParser jsonParser = new JSONParser();

    /**
     * Socket factory.
     */
    private final WebSocketFactory factory;

    /**
     * Book.
     */
    private Bitso.Book book;

    /**
     * Socket.
     */
    private WebSocket webSocket;

    /**
     * Indica si se cargaran todas las ordenes.
     */
    private boolean loadOrders;

    public BitsoWebSocketClient(Bitso.Book book) {
        factory = new WebSocketFactory();

        this.book = book;
    }

    protected abstract void onOrders(Bitso.Book book, List<WebSocketOrder> asks, List<WebSocketOrder> bids);

    protected abstract void onOrder(Bitso.Book book, WebSocketOrder order);

    protected abstract void onRemove(Bitso.Book book, WebSocketOrder order);

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

    public boolean connected() {
        return null != webSocket && webSocket.isOpen();
    }

    public void disconnect() {
        if (null == webSocket) {
            return;
        }

        try {
            webSocket.disconnect();

            if (!webSocket.isOpen()) {
                webSocket.removeListener(this);
                webSocket = null;
            }
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
    }

    public void connect(boolean loadOrders) {
        this.loadOrders = loadOrders;

        disconnect();

        if (null != webSocket) {
            return;
        }

        try {
            webSocket = factory.createSocket(WS_URL);
            webSocket.addListener(this);
            webSocket.connect();
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        if (loadOrders) {
            webSocket.sendText("{ \"action\": \"subscribe\", \"book\": \"" + book.name().toLowerCase() + "\", \"type\": \"orders\" }");
        } else {
            webSocket.sendText("{ \"action\": \"subscribe\", \"book\": \"" + book.name().toLowerCase() + "\", \"type\": \"diff-orders\" }");
        }
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
    }

    @Override
    public void onDisconnected(WebSocket websocket,
                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                               boolean closedByServer) throws Exception {
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        JSONObject messageJsonObject = parseObject(text);

        final String messageType = messageJsonObject.get("type").toString();
        final Bitso.Book book = Bitso.Book.valueOf(messageJsonObject.get("book").toString().toUpperCase());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("WS: {} {}", messageType, book);
        }

        if ("orders".equals(messageType)) {
            JSONObject ordersJsonObject = parseObject(messageJsonObject.get("payload").toString());
            JSONArray asksJsonArray = parseArray(ordersJsonObject.get("asks").toString());
            JSONArray bidsJsonArray = parseArray(ordersJsonObject.get("bids").toString());

            ArrayList<WebSocketOrder> asks = new ArrayList<>();
            ArrayList<WebSocketOrder> bids = new ArrayList<>();

            for (Iterator iterator = asksJsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject askJsonObject = (JSONObject) iterator.next();

                final String type = askJsonObject.get("t").toString();
                final String oid = askJsonObject.get("o").toString();
                final BigDecimal rate = new BigDecimal(askJsonObject.get("r").toString());
                final BigDecimal amount = new BigDecimal(askJsonObject.get("a").toString());
                final BigDecimal value = new BigDecimal(askJsonObject.get("v").toString());
                final long timestamp = Long.valueOf(askJsonObject.get("d").toString());

                asks.add(new WebSocketOrder(book, type, oid, rate, amount, value, timestamp));
            }

            for (Iterator iterator = bidsJsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject bidJsonObject = (JSONObject) iterator.next();

                final String type = bidJsonObject.get("t").toString();
                final String oid = bidJsonObject.get("o").toString();
                final BigDecimal rate = new BigDecimal(bidJsonObject.get("r").toString());
                final BigDecimal amount = new BigDecimal(bidJsonObject.get("a").toString());
                final BigDecimal value = new BigDecimal(bidJsonObject.get("v").toString());
                final long timestamp = Long.valueOf(bidJsonObject.get("d").toString());

                bids.add(new WebSocketOrder(book, type, oid, rate, amount, value, timestamp));
            }

            onOrders(book, asks, bids);

            connect(false);
        } else if ("diff-orders".equals(messageType)) {
            JSONArray payloadJsonArray = parseArray(messageJsonObject.get("payload").toString());

            for (Iterator iterator = payloadJsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject orderJsonObject = (JSONObject) iterator.next();

                final String status = orderJsonObject.get("s").toString();
                final String type = orderJsonObject.get("t").toString();
                final String oid = orderJsonObject.get("o").toString();
                final long timestamp = Long.valueOf(orderJsonObject.get("d").toString());

                if ("open".equals(status)) {
                    final BigDecimal rate = new BigDecimal(orderJsonObject.get("r").toString());
                    final BigDecimal amount = new BigDecimal(orderJsonObject.get("a").toString());
                    final BigDecimal value = new BigDecimal(orderJsonObject.get("v").toString());

                    onOrder(book, new WebSocketOrder(book, type, oid, rate, amount, value, timestamp));
                } else {
                    onRemove(book, new WebSocketOrder(book, type, oid, null, null, null, timestamp));
                }
            }
        }
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
    }

    @Override
    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
    }

    @Override
    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {
    }
}
