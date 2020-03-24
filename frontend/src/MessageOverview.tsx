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
            text: "",
            platformId: 0,
            created: new Date(2020, 3, 22, 0,0,0,0),
            creatorName: ""
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