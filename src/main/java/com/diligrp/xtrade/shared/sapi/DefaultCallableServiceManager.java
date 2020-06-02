package com.diligrp.xtrade.shared.sapi;

import com.diligrp.xtrade.shared.constant.Constants;
import com.diligrp.xtrade.shared.domain.Message;
import com.diligrp.xtrade.shared.domain.MessageEnvelop;
import com.diligrp.xtrade.shared.domain.RequestContext;
import com.diligrp.xtrade.shared.domain.ServiceRequest;
import com.diligrp.xtrade.shared.exception.ServiceAccessException;
import com.diligrp.xtrade.shared.util.AssertUtils;
import com.diligrp.xtrade.shared.util.JsonUtils;
import com.diligrp.xtrade.shared.util.ObjectUtils;

public class DefaultCallableServiceManager implements ICallableServiceManager {

    private ICallableServiceEngine callableServiceEngine;

    public DefaultCallableServiceManager() {
    }

    public DefaultCallableServiceManager(ICallableServiceEngine callableServiceEngine) {
        this.callableServiceEngine = callableServiceEngine;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Message<?> callService(RequestContext context, String payload) throws Throwable {
        String service = context.getString(Constants.ENDPOINT_SERVICE);
        AssertUtils.notEmpty(service, "service missed");

        return doCallService(service, context, payload);
    }

    @Override
    public Message<?> callService(RequestContext context, MessageEnvelop envelop) throws Throwable {
        String service = envelop.getRecipient() == null ?
            context.getString(Constants.ENDPOINT_SERVICE) :envelop.getRecipient();
        AssertUtils.notEmpty(service, "service missed");

        return doCallService(service, context, envelop.getPayload());
    }

    private Message<?> doCallService(String service, RequestContext context, String payload) throws Throwable {
        CallableServiceEndpoint<?> endpoint = callableServiceEngine.callableServiceEndpoint(service);
        if (endpoint == null) {
            throw new ServiceAccessException(1000, String.format("Callable service {%s} unavailable", service));
        }

        ServiceRequest request = new ServiceRequest();
        request.setContext(context);
        if (ObjectUtils.isNotEmpty(payload)) {
            request.setData(JsonUtils.fromJsonString(payload, endpoint.getRequiredType()));
        }

        Object result = endpoint.call(request);
        if (result instanceof Message) {
            return (Message) result;
        } else {
            return Message.success(result);
        }
    }
}
