/*******************************************************************************
 * Copyright (c) 2017 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.endpoint;

import org.eclipse.kapua.model.KapuaUpdatableEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

/**
 * {@link EndpointInfo} entity definition.<br>
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "endpointInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = EndpointInfoXmlRegistry.class, factoryMethod = "newEntity")
public interface EndpointInfo extends KapuaUpdatableEntity {

    public static final String TYPE = "endpointInfo";

    @Override
    public default String getType() {
        return TYPE;
    }

    @XmlElement(name = "schema")
    public String getSchema();

    public void setSchema(String schema);

    @XmlElement(name = "dns")
    public String getDns();

    public void setDns(String dns);

    @XmlElement(name = "port")
    public int getPort();

    public void setPort(int port);

    @XmlElement(name = "secure")
    public boolean getSecure();

    public void setSecure(boolean secure);

    @XmlElement(name = "usage")
    public <E extends EndpointUsage> Set<E> getUsages();

    public void setUsages(Set<EndpointUsage> endpointUsages);

}
