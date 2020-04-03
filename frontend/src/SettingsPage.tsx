import * as React from "react";
import { EventBus } from "./EventBus";
import { SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";

export interface SettingsProps {
    eventBus: EventBus
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class SettingsPage extends React.Component<SettingsProps, SettingsProps>{
    constructor(props: SettingsProps) {
        super(props);
        this.state = {eventBus: props.eventBus};
    }

    public render() {
        return (
            <div>
                <SocialMediaAccountsListTile accounts={[]} eventBus={this.state.eventBus} />
            </div>
        );
    }
}