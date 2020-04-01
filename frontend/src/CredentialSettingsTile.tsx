import * as React from "react";
import './PublishMessageTile.css';
import './CredentialSettingsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";
import { SocialMediaAccount, SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";
import { SSL_OP_PKCS1_CHECK_1 } from "constants";

export interface CredentialSetting {
    account: SocialMediaAccount,
    eventBus: EventBus
}

export interface Credential {
    id: string;
    value: string;
    descriptor: string;
    omitted: boolean;
    created: Date;
}

/** Contains a form to publish new messages. */
export class CredentialSettingsTile extends React.Component<CredentialSetting, CredentialSetting>{
    constructor(props: CredentialSetting) {
        super(props);
        this.state = { eventBus: props.eventBus, account: props.account };
        this.fetchCredentials();
        this.state.eventBus.register(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, this.updateSelected.bind(this));
    }

    private updateSelected(type: EventBusEventType, data: any) {
        this.setState({
            account: data
        });
    }

    private fetchCredentials() {
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount/' + this.state.account.id, {
            method: 'GET',
            credentials: 'include',
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                this.setState({
                    eventBus: this.state.eventBus,
                    account: data
                });
            });
    }

    private saveCredential(credentialId: string) {
        this.state.account.credentials.filter(cr => cr.id === credentialId).forEach(c => {
            fetch(DeploymentManager.getUrl() + 'api/credential', {
                method: 'PUT',
                headers: new Headers({
                    'Content-Type': 'application/json'
                }),
                credentials: 'include',
                body: JSON.stringify({
                    descriptor: c.descriptor,
                    value: c.value,
                    accountId: this.state.account.id,
                    id: c.id.startsWith("fe_id_") ? "" : c.id
                })
            })
                .then(data => {
                    this.fetchCredentials();
                    this.state.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                });
        });
    }

    private saveDisplayName() {
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount/' + this.state.account.id, {
            method: 'PUT',
            credentials: 'include',

            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                id: this.state.account.id,
                displayName: this.state.account.displayName,
                platformId: this.state.account.platformId
            })
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                this.setState({
                    eventBus: this.state.eventBus,
                    account: data
                });
            });
    }

    private removeCredential(credentialId: string) {
        fetch(DeploymentManager.getUrl() + 'api/credential/' + credentialId, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(data => {
                this.fetchCredentials();
            });
    }

    private updateStateIfNecessary(property: string, id: string, value: string) {
        if (!id.startsWith(property)) {
            if (id === "displayName") {
                this.setState({
                    account: {
                        credentials: this.state.account.credentials,
                        id: this.state.account.id,
                        platformId: this.state.account.platformId,
                        displayName: value,
                        error: this.state.account.error
                    }
                });
            }
            return;
        }

        let identifier = id.substring(property.length + 1);
        let cred = this.state.account.credentials.find(c => c.id === identifier);

        if (cred) {
            if (property === 'descriptor') {
                cred.descriptor = value;
            }
            else if (property === 'value') {
                cred.value = value;
            }
            this.updateOmittedState(cred, value);
        }
        //trigger render
        this.setState({
            eventBus: this.state.eventBus,
            account: this.state.account
        });
    }

    private updateOmittedState(cred: Credential, value: string) {
        if (value) {
            cred.omitted = false;
        } else {
            cred.omitted = true;
        }
    }

    private addCredential() {
        const newCreds = Object.assign([], this.state.account.credentials);
        newCreds.push({ id: this.makeString(), value: "", descriptor: "", omitted: false, created: new Date() });
        this.setState({
            eventBus: this.state.eventBus,
            account: {
                credentials: newCreds,
                displayName: this.state.account.displayName,
                id: this.state.account.id,
                error: this.state.account.error,
                platformId: this.state.account.platformId
            }
        });
    }

    /** @return a random string to be used as an identifier in the UI. This will not be the identifier used in the back end. */
    makeString(): string {
        let outString: string = 'fe_id_';
        let inOptions: string = 'abcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < 32; i++) {
            outString += inOptions.charAt(Math.floor(Math.random() * inOptions.length));
        }
        return outString;
    }

    public render() {
        console.log(this.state.account.credentials);
        const elems = this.state.account.credentials.sort((c1, c2) => new Date(c1.created).getTime() - new Date(c2.created).getTime()).map(cred => {
            const credentialsField = cred.omitted ?
                <input className="credentialField textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" placeholder="(omitted)" value="" />
                : <input className="credentialField textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" value={cred.value} />;

            const saveButton = cred.omitted ? undefined : <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveCredential(cred.id)}>{ImageProvider.getImage("save-icon")}</div>;
            return (
                <div className="box-content">
                    <div className="credential">
                        <div className="credentialLabel">Descriptor: </div>
                        <input className="credentialField textarea monospace-font" onChange={o => this.updateStateIfNecessary("descriptor", o.currentTarget.id, o.currentTarget.value)} id={"descriptor_" + cred.id} type="text" value={cred.descriptor} />
                        <div className="btn-icon btn-icon-delete btn-credentials" onClick={o => this.removeCredential(cred.id)}>{ImageProvider.getImage("none")} </div>
                    </div>
                    <div className="credential">
                        <div className="credentialLabel">Value: </div>
                        {credentialsField}
                        {saveButton}
                    </div>

                </div>
            );
        });
        return (
            <div className="container double-container" >
                <div className="box-header">
                    Account Details for {this.state.account.displayName}
                </div>
                <div className="box-content">
                    <div className="credential">
                        <div className="credentialLabel">Display Name: </div>
                        <input className="credentialField textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id="displayName" type="text" value={this.state.account.displayName} />
                        <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveDisplayName()}>{ImageProvider.getImage("save-icon")}</div>
                    </div>
                    <div className="displayName">Credentials: </div>
                    {elems}
                    <div className="btn btn-icon btn-add btn-icon-big">
                        <div className="btn-add-inner" onClick={this.addCredential.bind(this)} >{ImageProvider.getImage("add-icon")}</div>
                    </div>
                </div>
            </div >
        );
    }
}