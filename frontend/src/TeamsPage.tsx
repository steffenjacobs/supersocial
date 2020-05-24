import * as React from "react";
import { EventBus } from "./EventBus";
import { LoginManager } from "./LoginManager";
import { TeamsListTile } from "./TeamsListTile";

export interface TeamsPageProps {
    eventBus: EventBus
    loginManager: LoginManager
}

/** Settings page. Contains the social media account management. */
export class TeamsPage extends React.Component<TeamsPageProps>{

    public render() {
        return (
            <div>
                <TeamsListTile eventBus={this.props.eventBus} />
            </div>
        );
    }
}