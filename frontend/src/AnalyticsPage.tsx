import * as React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { PostAnalyticsTile, AnalyticsType } from "./PostAnalyticsTile";
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";

export interface AnalyticsProps {
    eventBus: EventBus
}

/** Analytics page. Contains all sorts of analytics tiles with KPIs and graphs. */
export class AnalyticsPage extends React.Component<AnalyticsProps, AnalyticsProps>{
    constructor(props: AnalyticsProps) {
        super(props);
        this.state = { eventBus: props.eventBus };
        this.updateData(EventBusEventType.REFRESH_POST_ANALYTICS_REQ, "cd8cfa00-aaf0-49b5-90f1-429d5f63dcda");
        props.eventBus.register(EventBusEventType.REFRESH_POST_ANALYTICS_REQ, (t, e) => this.updateData(t, e.relId));
        props.eventBus.register(EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ, (t, e) => this.updateData(t, e.relId));
    }

    public render() {
        let post = { relId: "cd8cfa00-aaf0-49b5-90f1-429d5f63dcda", value: 0 }
        return (
            <div>
                <PostAnalyticsTile analyticsNumber={post} label="# Impressions" eventBus={this.state.eventBus} keyVal="impressions" type={AnalyticsType.POST} />
                <PostAnalyticsTile analyticsNumber={post} label="# Comments" eventBus={this.state.eventBus} keyVal="comments" type={AnalyticsType.POST} />
                <PostAnalyticsTile analyticsNumber={post} label="# Likes" eventBus={this.state.eventBus} keyVal="likes" type={AnalyticsType.POST} />
                <PostAnalyticsTile analyticsNumber={post} label="# Shares" eventBus={this.state.eventBus} keyVal="shares" type={AnalyticsType.POST} />
                <PostAnalyticsTile analyticsNumber={post} label="# Followers" eventBus={this.state.eventBus} keyVal="followers" type={AnalyticsType.ACCOUNT} />
                <PostAnalyticsTile analyticsNumber={post} label="# Posts" eventBus={this.state.eventBus} keyVal="posts" type={AnalyticsType.ACCOUNT} />
            </div>
        );
    }

    private updateData(eventType: EventBusEventType, relId: string) {
        let typedPath = eventType === EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ ? "account" : "post";
        fetch(DeploymentManager.getUrl() + "api/analytics/" + typedPath + "/" + relId + "?query=", {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data =>
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POST_ANALYTICS, data)
                    );
                }
            });
    }
}