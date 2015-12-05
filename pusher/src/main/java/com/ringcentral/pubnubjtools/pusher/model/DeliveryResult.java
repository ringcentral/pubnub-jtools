package com.ringcentral.pubnubjtools.pusher.model;

public class DeliveryResult implements Describable {

    private final boolean success;
    private final Message message;
    private final Request request;
    private final long durationMillis;
    private final Response response;
    private final Throwable exception;

    public DeliveryResult(Message message, Request request, long durationMillis, Response response) {
        this.message = message;
        this.request = request;
        this.durationMillis = durationMillis;
        this.response = response;
        this.exception = null;
        this.success = isSuccess();
    }

    public DeliveryResult(Message message, Request request, long durationMillis, Throwable exception) {
        this.message = message;
        this.request = request;
        this.durationMillis = durationMillis;
        this.response = null;
        this.exception = exception;
        this.success = isSuccess();
    }

    public boolean isSuccess() {
        return response != null && response.getStatus() == 200;
    }

    public boolean isFailed() {
        return response == null || response.getStatus() != 200;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Response getResponse() {
        return response;
    }

    public Message getMessage() {
        return message;
    }

    public Throwable getException() {
        return exception;
    }

    public Request getRequest() {
        return request;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public String toString() {
        return "DeliveryResult{" +
                " success=" + success +
                ", message=" + message +
                ", request=" + request +
                ", durationMillis=" + durationMillis +
                ", response=" + response +
                ", exception=" + exception +

                '}';
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
        return "DeliveryResult{" +
                " success=" + success +
                ", message=" + message.getDebugDescription() +
                ", request=" + request.getDebugDescription() +
                ", durationMillis=" + durationMillis +
                ", response=" + response.getDebugDescription() +
                (exception == null? "" : ", exception=" +  exception.getMessage()) +
                '}';
    }

    @Override
    public String getInfoDescription() {
        return "DeliveryResult{" +
                " success=" + success +
                ", message=" + message.getInfoDescription() +
                ", request=" + request.getInfoDescription() +
                ", durationMillis=" + durationMillis +
                ", response=" + response.getInfoDescription() +
                (exception == null? "" : ", exception=" +  exception.getMessage()) +
                '}';
    }

}
