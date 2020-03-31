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
        this.state = { eventBus: props.eventBus };
    }

    public render() {
        return (
            <div>
                <PublishMessageTile eventBus={this.state.eventBus} accounts={[]} sendTextForm={{
                    message: "", accountIds: [], schedule: false, scheduled: new Date()
                }} />
                <div className="vspacer" />
                <PublishedPostsTile posts={[]} eventBus={this.state.eventBus} />
            </div>
        );
    }
}