import * as React from "react";
import { PublishMessageTile } from "./PublishMessageTile";
import { PublishedPostsTile } from "./PublishedPostsTile";
import { EventBus } from "../../misc/EventBus";
import { TrendingTile } from "./TrendingTile";
import { LoginManager } from "../../login/LoginManager";

export interface MessageOverviewProps {
    eventBus: EventBus
    loginManager: LoginManager
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class MessageOverview extends React.Component<MessageOverviewProps>{

    public render() {
        return (
            <div>
                <PublishMessageTile eventBus={this.props.eventBus} />
                <div className="hspacer inline-block" />
                <TrendingTile eventBus={this.props.eventBus} loginManager={this.props.loginManager}/>
                <div className="vspacer" />
                <PublishedPostsTile eventBus={this.props.eventBus} />
            </div>
        );
    }
}