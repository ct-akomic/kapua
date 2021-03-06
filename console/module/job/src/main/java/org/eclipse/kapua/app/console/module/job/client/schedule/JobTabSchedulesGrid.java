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
package org.eclipse.kapua.app.console.module.job.client.schedule;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.ui.grid.EntityGrid;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractEntityView;
import org.eclipse.kapua.app.console.module.api.shared.model.query.GwtQuery;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.job.client.messages.ConsoleJobMessages;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTrigger;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerQuery;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtTriggerService;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtTriggerServiceAsync;

import java.util.ArrayList;
import java.util.List;

public class JobTabSchedulesGrid extends EntityGrid<GwtTrigger> {

    private static final GwtTriggerServiceAsync TRIGGER_SERVICE = GWT.create(GwtTriggerService.class);
    private static final ConsoleJobMessages JOB_MSGS = GWT.create(ConsoleJobMessages.class);

    private String jobId;
    private GwtTriggerQuery query;

    private JobTabSchedulesToolbar toolbar;

    public JobTabSchedulesGrid(AbstractEntityView<GwtTrigger> entityView, GwtSession currentSession) {
        super(entityView, currentSession);
    }

    @Override
    protected RpcProxy<PagingLoadResult<GwtTrigger>> getDataProxy() {
        return new RpcProxy<PagingLoadResult<GwtTrigger>>() {

            @Override
            protected void load(Object o, AsyncCallback<PagingLoadResult<GwtTrigger>> asyncCallback) {
                TRIGGER_SERVICE.findByJobId((PagingLoadConfig) o, currentSession.getSelectedAccountId(), jobId, asyncCallback);
            }
        };
    }

    @Override
    protected List<ColumnConfig> getColumns() {
        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

        ColumnConfig columnConfig = new ColumnConfig("triggerName", JOB_MSGS.gridJobSchedulesColumnHeaderName(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("startsOnFormatted", JOB_MSGS.gridJobSchedulesColumnHeaderStartsOnFormatted(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("endsOnFormatted", JOB_MSGS.gridJobSchedulesColumnHeaderEndsOnFormatted(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("cronScheduling", JOB_MSGS.gridJobSchedulesColumnHeaderCronScheduling(), 200);
        columnConfigs.add(columnConfig);

        columnConfig = new ColumnConfig("retryInterval", JOB_MSGS.gridJobSchedulesColumnHeaderRetryInterval(), 200);
        columnConfigs.add(columnConfig);

        return columnConfigs;
    }

    @Override
    public GwtQuery getFilterQuery() {
        return query;
    }

    @Override
    public void setFilterQuery(GwtQuery filterQuery) {
        query = (GwtTriggerQuery) filterQuery;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    protected JobTabSchedulesToolbar getToolbar() {
        if (toolbar == null) {
            toolbar = new JobTabSchedulesToolbar(currentSession);

            toolbar.setEditButtonVisible(false);
        }
        return toolbar;
    }
}
