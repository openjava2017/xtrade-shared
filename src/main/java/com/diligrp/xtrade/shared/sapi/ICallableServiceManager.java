package com.diligrp.xtrade.shared.sapi;

import com.diligrp.xtrade.shared.domain.Message;
import com.diligrp.xtrade.shared.domain.MessageEnvelop;
import com.diligrp.xtrade.shared.domain.RequestContext;

public interface ICallableServiceManager {
    Message callService(RequestContext context, String payload) throws Throwable;

    Message callService(RequestContext context, MessageEnvelop envelop) throws Throwable;
}
