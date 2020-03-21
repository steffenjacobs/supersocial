import * as React from "react";
import { PublishMessageTile } from "./PublishMessageTile";
import { PublishedPostsTile } from "./PublishedPostsTile";
import { EventBus } from "./EventBus";

export interface Props{
    eventBus: EventBus
}

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
            created: "2020-03-21",
            creatorName: "Steffen"
        },{
            id: 1,
            text: "Test text 2",
            platformId: 2,
            created: "2020-03-21",
            creatorName: "Steffen"
        },{
            id: 2,
            text: "Posted on invalid platform",
            platformId: 0,
            created: "2020-03-21",
            creatorName: "Steffen"
        }];
        return (
            <div>
                <PublishMessageTile message="" platforms={new Set<string>()} eventBus = {this.state.eventBus} />
                <div className="vspacer"/>
                <PublishedPostsTile posts={posts} eventBus = {this.state.eventBus}/>
            </div>
        );
    }
}