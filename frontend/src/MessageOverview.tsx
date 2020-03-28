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
        return (
            <div>
                <PublishMessageTile message="" platforms={new Set<string>()} eventBus={this.state.eventBus} />
                <div className="vspacer" />
                <PublishedPostsTile posts={[]} eventBus={this.state.eventBus} />
            </div>
        );
    }
}