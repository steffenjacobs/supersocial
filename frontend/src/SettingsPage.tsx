import * as React from "react";
import { EventBus } from "./EventBus";
import { SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";

export interface SettingsProps {
    eventBus: EventBus
}

/** Settings page. Contains the social media account management. */
export class SettingsPage extends React.Component<SettingsProps, SettingsProps>{
    constructor(props: SettingsProps) {
        super(props);
        this.state = { eventBus: props.eventBus };
    }

    public render() {
        return (
            <div>
                <SocialMediaAccountsListTile accounts={[]} eventBus={this.state.eventBus} />
            </div>
        );
    }
}