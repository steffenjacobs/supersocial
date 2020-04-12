import * as React from "react";
import { EventBus } from "./EventBus";
import { SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";
import { LoginManager } from "./LoginManager";
import { MapContainer } from "./MapContainer";

export interface SettingsProps {
    eventBus: EventBus
    loginManager: LoginManager
}

/** Settings page. Contains the social media account management. */
export class SettingsPage extends React.Component<SettingsProps>{

    public render() {
        return (
            <div>
                <SocialMediaAccountsListTile eventBus={this.props.eventBus} />
                <div className="vspacer" />
                <MapContainer loginManager = {this.props.loginManager} eventBus={this.props.eventBus}/>
            </div>
        );
    }
}