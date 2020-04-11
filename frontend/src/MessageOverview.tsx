import * as React from "react";
import { PublishMessageTile } from "./PublishMessageTile";
import { PublishedPostsTile } from "./PublishedPostsTile";
import { EventBus } from "./EventBus";
import { TrendingTile } from "./TrendingTile";

export interface MessageOverviewProps {
    eventBus: EventBus
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class MessageOverview extends React.Component<MessageOverviewProps>{
    constructor(props: MessageOverviewProps) {
        super(props);
    }

    public render() {
        return (
            <div>
                <PublishMessageTile eventBus={this.props.eventBus} />
                <div className="hspacer inline-block" />
                <TrendingTile eventBus={this.props.eventBus}/>
                <div className="vspacer" />
                <PublishedPostsTile eventBus={this.props.eventBus} />
            </div>
        );
    }
}