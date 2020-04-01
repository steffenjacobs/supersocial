import * as React from "react";
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";
import { DeploymentManager } from "./DeploymentManager";
import { Credential, CredentialSettingsTile } from "./CredentialSettingsTile";

export interface SocialMediaAccount {
    id: string
    displayName: string
    credentials: Credential[]
    platformId: number
    error: string
}

export interface SocialMediaAccounts {
    accounts: SocialMediaAccount[]
    updating?: boolean
    eventBus: EventBus
    selected?: SocialMediaAccount
}

/** Lists the already published posts. */

//TODO: make table scrollable and pageable.
export class SocialMediaAccountsListTile extends React.Component<SocialMediaAccounts, SocialMediaAccounts>{
    constructor(props: SocialMediaAccounts, state: SocialMediaAccounts) {
        super(props);
        this.state = { accounts: props.accounts, updating: props.updating, eventBus: props.eventBus };

        this.refreshAccounts();
        this.state.eventBus.register(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS, (eventType, eventData) => this.refreshAccounts());
    }

    /** Triggers a refresh of this list. This is also triggered when a REFRESH_POSTS event is received via the EventBus. */
    private refreshAccounts() {
        this.setState({ accounts: this.state.accounts, updating: true });
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (response.status === 401) {
                    console.error("Could not fetch data: 401");
                } else {
                    response.json().then(data => this.setState({
                        accounts: data,
                        eventBus: this.state.eventBus,
                        updating: false,
                        selected: data.filter(a => a.id === (this.state.selected ? this.state.selected.id : undefined)) ? this.state.selected : undefined
                    }));
                }
            });
    }

    /** @returns a social media icon for a given platform identifier. */
    private getSocialmediaIcon(platformId: number) {
        if (platformId === 1) {
            return ImageProvider.getImage("facebook-logo");
        }
        else if (platformId === 2) {
            return ImageProvider.getImage("twitter-logo");
        }
        else {
            return ImageProvider.getImage("none-logo");
        }
    }

    private createPlatformElement(platformId: number) {
        if (platformId === 0) {
            return <td className="centered tooltip x-gray">{ImageProvider.getImage("none")}<span className="tooltiptext">No Platform selected yet.</span></td>;
        }
        else {
            return <td className="centered icon-medium">{this.getSocialmediaIcon(platformId)}</td>;
        }
    }

    /** Fire an event to change the selected post. Lets all listeners on the event bus (e.g. PublishMessageTile.tsx) know to update accordingly. */
    private selectAccount(account: SocialMediaAccount) {
        this.setState({
            selected: account
        });
        this.state.eventBus.fireEvent(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, account);
    }

    private addAccount() {

    }

    public render() {
        const accounts = this.state.accounts.sort(
            (p1, p2) => p1.displayName.localeCompare(p2.displayName)).map(elem => {

                return (
                    <tr key={elem.id} onClick={() => this.selectAccount(elem)}>
                        {this.createPlatformElement(elem.platformId)}
                        <td>{elem.displayName}</td>
                        <td>{elem.credentials.length}</td>
                    </tr>
                );
            });

        let classUpdating = ["inline-block", "btn-icon"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        const accountSettings = this.state.selected ? <CredentialSettingsTile eventBus={this.state.eventBus} account={this.state.selected} /> : <div />;

        return (
            <div>
                <div className="container inline-block">
                    <div className="box-header box-header-with-icon">
                        <div className="inline-block">All Accounts</div>
                        <div
                            className={classUpdating.join(" ")}
                            onClick={this.refreshAccounts.bind(this)}
                        >
                            {ImageProvider.getImage("refresh")}
                        </div>
                    </div>
                    <div className="box-content">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>Platform</th>
                                    <th>Display Name</th>
                                    <th># Credentials</th>
                                </tr>
                            </thead>
                            <tbody>
                                {accounts}
                            </tbody>

                        </table>
                        <div className="btn btn-icon btn-add btn-icon-big btn-margins">
                            <div className="btn-add-inner" onClick={this.addAccount.bind(this)} >{ImageProvider.getImage("add-icon")}</div>
                        </div>
                    </div>
                </div >

                {accountSettings}
            </div>
        );
    }
}