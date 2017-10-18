/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.translator.kura.kapua;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.lifecycle.KapuaNotifyChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaNotifyMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaNotifyPayload;
import org.eclipse.kapua.message.internal.device.management.KapuaNotifyChannelImpl;
import org.eclipse.kapua.message.internal.device.management.KapuaNotifyMessageImpl;
import org.eclipse.kapua.message.internal.device.management.KapuaNotifyPayloadImpl;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.management.KuraNotifyChannel;
import org.eclipse.kapua.service.device.call.message.kura.management.KuraNotifyMessage;
import org.eclipse.kapua.service.device.call.message.kura.management.KuraNotifyPayload;
import org.eclipse.kapua.service.device.management.registry.operation.OperationStatus;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

import java.math.BigInteger;

/**
 * Messages translator implementation from {@link KuraNotifyMessage} to {@link KapuaNotifyMessage}
 *
 * @since 1.0
 */
public class TranslatoManagementNotifyKuraKapua extends Translator<KuraNotifyMessage, KapuaNotifyMessage> {

    private KapuaIdFactory kapuaIdFactory = KapuaLocator.getInstance().getFactory(KapuaIdFactory.class);

    @Override
    public KapuaNotifyMessage translate(KuraNotifyMessage kuraNotifyMessage)
            throws KapuaException {
        KapuaNotifyMessage kapuaNotifyMessage = new KapuaNotifyMessageImpl();
        kapuaNotifyMessage.setChannel(translate(kuraNotifyMessage.getChannel()));
        kapuaNotifyMessage.setPayload(translate(kuraNotifyMessage.getPayload()));

        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(kuraNotifyMessage.getChannel().getScope());

        if (account == null) {
            throw new KapuaEntityNotFoundException(Account.TYPE, kuraNotifyMessage.getChannel().getScope());
        }

        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(account.getId(), kuraNotifyMessage.getChannel().getSenderClientId());

        if (device == null) {
            throw new KapuaEntityNotFoundException(Device.class.toString(), kuraNotifyMessage.getChannel().getClientId());
        }

        kapuaNotifyMessage.setDeviceId(device.getId());
        kapuaNotifyMessage.setScopeId(account.getId());
        kapuaNotifyMessage.setCapturedOn(kuraNotifyMessage.getPayload().getTimestamp());
        kapuaNotifyMessage.setSentOn(kuraNotifyMessage.getPayload().getTimestamp());
        kapuaNotifyMessage.setReceivedOn(kuraNotifyMessage.getTimestamp());
        kapuaNotifyMessage.setPosition(TranslatorKuraKapuaUtils.translate(kuraNotifyMessage.getPayload().getPosition()));

        return kapuaNotifyMessage;
    }

    private KapuaNotifyChannel translate(KuraNotifyChannel kuraNotifyChannel)
            throws KapuaException {
        KapuaNotifyChannel kapuaNotifyChannel = new KapuaNotifyChannelImpl();
        kapuaNotifyChannel.setClientId(kuraNotifyChannel.getSenderClientId());
        kapuaNotifyChannel.setResources(kuraNotifyChannel.getResources());
        return kapuaNotifyChannel;
    }

    private KapuaNotifyPayload translate(KuraNotifyPayload kuraNotifyPayload)
            throws KapuaException {
        KapuaNotifyPayload kapuaNotifyPayload = new KapuaNotifyPayloadImpl();
        kapuaNotifyPayload.setBody(kuraNotifyPayload.getBody());
        kapuaNotifyPayload.setMetrics(kuraNotifyPayload.getMetrics());

        kapuaNotifyPayload.setOperationId(kapuaIdFactory.newKapuaId(BigInteger.valueOf(kuraNotifyPayload.getOperationId())));
        kapuaNotifyPayload.setOperationStatus(translateOperationStatus(kuraNotifyPayload.getOperationStatus()));
        kapuaNotifyPayload.setOperationProgress(kuraNotifyPayload.getOperationProgress());
        kapuaNotifyPayload.setErrorMessage(kuraNotifyPayload.getErrorMessage());

        return kapuaNotifyPayload;
    }

    private String translateOperationStatus(String kuraOperationStatus) {

        if ("IN_PROGRESS".equals(kuraOperationStatus)) {
            return OperationStatus.RUNNING.name();
        }

        return OperationStatus.valueOf(kuraOperationStatus).name();
    }

    @Override
    public Class<KuraNotifyMessage> getClassFrom() {
        return KuraNotifyMessage.class;
    }

    @Override
    public Class<KapuaNotifyMessage> getClassTo() {
        return KapuaNotifyMessage.class;
    }

}
