import * as React from "react";
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import './PostAnalyticsTile.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";

export class AnalyticsType {
    public static POST = new AnalyticsType("postId", EventBusEventType.REFRESH_POST_ANALYTICS, EventBusEventType.REFRESH_POST_ANALYTICS_REQ);
    public static ACCOUNT = new AnalyticsType("accId", EventBusEventType.REFRESH_ACCOUNT_ANALYTICS, EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ);
    constructor(identifierName: string, refreshEvent: EventBusEventType, requestRefreshEvent: EventBusEventType) {
        this.identifierName = identifierName;
        this.refreshEvent = refreshEvent;
        this.requestRefreshEvent = requestRefreshEvent;
    }

    readonly identifierName: string;
    readonly refreshEvent: EventBusEventType;
    readonly requestRefreshEvent: EventBusEventType;
}

export interface AnalyticsNumber {
    relId: string
    value: number
}

export interface PostAnalyticsNumberProps {
    analyticsNumber: AnalyticsNumber
    updating?: boolean
    eventBus: EventBus
    label: string
    keyVal: string
    type: AnalyticsType
    background: string
}

/** Lists the already published posts. */

//TODO: make table scrollable and pageable.
export class PostAnalyticsTile extends React.Component<PostAnalyticsNumberProps, PostAnalyticsNumberProps>{
    constructor(props: PostAnalyticsNumberProps) {
        super(props);
        this.state = { analyticsNumber: props.analyticsNumber, updating: props.updating, eventBus: props.eventBus, label: props.label, keyVal: props.keyVal, type: props.type, background: props.background };

        props.eventBus.register(props.type.refreshEvent, (t, e) => this.refreshData(e));
    }

    private refreshData(dynamicJsonData: any) {
        this.setState({
            analyticsNumber: {
                relId: this.state.analyticsNumber.relId,
                value: dynamicJsonData[0]._source[this.state.keyVal]
            }
        });
    }

    public render() {
        let classUpdating = ["inline-block", "btn-icon", "btn-small"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        return (
            <div className=" container container-two-thirds inline-block container-margins">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">{this.state.label}</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={e => this.state.eventBus.fireEvent(this.state.type.requestRefreshEvent, { relId: this.state.analyticsNumber.relId })}
                    >
                        {ImageProvider.getImage("refresh")}
                    </div>
                </div>
                <div className="box-content block">
                    <div className="kpi">{this.state.analyticsNumber.value ? this.state.analyticsNumber.value : 0}</div>
                </div>
                <div className="box-content block kpi_background" style={
                    { backgroundImage: "url(icons/" + this.state.background + ".svg)" }
                }>
                </div>
            </div >
        );
    }
}