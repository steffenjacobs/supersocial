import * as React from "react";
import { PublishMessageTile } from "./PublishMessageTile";
import { PublishedPostsTile } from "./PublishedPostsTile";
import { EventBus } from "./EventBus";

export interface Props {
    eventBus: EventBus
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class MessageOverview extends React.Component<Props, Props>{
    constructor(props: Props, state: Props) {
        super(props);
        this.state = props;
    }

    public render() {
        let posts = [{
            id: 0,
            text: "Test text",
            platformId: 1,
            created: new Date(2020, 3, 22, 0,0,0,0),
            creatorName: "Steffen"
        }, {
            id: 1,
            text: "Test text 2",
            platformId: 2,
            created: new Date(2020, 3, 20, 0,0,0,0),
            creatorName: "Steffen"
        }, {
            id: 2,
            text: "Posted on invalid platform",
            platformId: 0,
            created: new Date(2020, 3, 21, 0,0,0,0),
            creatorName: "Steffen"
        }];
        return (
            <div>
                <PublishMessageTile message="" platforms={new Set<string>()} eventBus={this.state.eventBus} />
                <div className="vspacer" />
                <PublishedPostsTile posts={posts} eventBus={this.state.eventBus} />
            </div>
        );
    }
}