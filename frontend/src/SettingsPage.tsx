import * as React from "react";
import { EventBus } from "./EventBus";
import { CredentialSettingsTile } from "./CredentialSettingsTile";
import { SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";

export interface Props {
    eventBus: EventBus
}

/** Message overview page. Contains the message publishing tile (PublishMessageTile.tsx) and the list of published posts (PublishedPostsTile.tsx). */
export class SettingsPage extends React.Component<Props, Props>{
    constructor(props: Props, state: Props) {
        super(props);
        this.state = props;
    }

    public render() {
        return (
            <div>
                <SocialMediaAccountsListTile accounts={[]} eventBus={this.state.eventBus} />
            </div>
        );
    }
}