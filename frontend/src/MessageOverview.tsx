import * as React from "react";
import { PublishMessageTile } from "./PublishMessageTile";
import { PublishedPostsTile } from "./PublishedPostsTile";

export class MessageOverview extends React.Component<any, any>{
    constructor(props: any, state: any) {
        super(props);
        this.state = state;
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
                <PublishMessageTile message="Test Message" platforms={new Set<string>()} />
                <div className="vspacer"/>
                <PublishedPostsTile posts={posts} />
            </div>
        );
    }
}