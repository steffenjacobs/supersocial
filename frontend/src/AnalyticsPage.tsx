import * as React from "react";
import { EventBus } from "./EventBus";
import { SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";
import { PostAnalyticsTile } from "./PostAnalyticsTile";

export interface AnalyticsProps {
    eventBus: EventBus
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class AnalyticsPage extends React.Component<AnalyticsProps, AnalyticsProps>{
    constructor(props: AnalyticsProps) {
        super(props);
        this.state = { eventBus: props.eventBus };
    }

    public render() {
        return (
            <div>
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Views" endpointUrl="api/analytics/post/views" eventBus={this.state.eventBus} />
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Comments" endpointUrl="api/analytics/post/comment" eventBus={this.state.eventBus} />
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Likes" endpointUrl="api/analytics/post/likes" eventBus={this.state.eventBus} />
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Shares" endpointUrl="api/analytics/post/shares" eventBus={this.state.eventBus} />
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Followers" endpointUrl="api/analytics/account/followers" eventBus={this.state.eventBus} />
                <PostAnalyticsTile post={{ postId: "", viewCount: 0 }} label="# Posts" endpointUrl="api/analytics/account/post" eventBus={this.state.eventBus} />
            </div>
        );
    }
}