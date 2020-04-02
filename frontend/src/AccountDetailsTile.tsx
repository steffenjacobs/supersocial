import * as React from "react";
import './PublishMessageTile.css';
import './AccountDetailsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";
import { SocialMediaAccount, SocialMediaAccountsListTile } from "./SocialMediaAccountsListTile";
import { SSL_OP_PKCS1_CHECK_1 } from "constants";
import { ToastManager } from "./ToastManager";
import { EntityUtil } from "./EntityUtil";

export interface AccountDetailsProps {
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
export class AccountDetailsTile extends React.Component<AccountDetailsProps, AccountDetailsProps>{
    constructor(props: AccountDetailsProps) {
        super(props);
        this.state = { eventBus: props.eventBus, account: props.account };
        this.fetchCredentials();
        this.state.eventBus.register(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, this.updateSelected.bind(this));
    }

    private updateSelected(type: EventBusEventType, data: any) {
        if (data && data.id === this.state.account.id) {
            this.setState({
                account: data
            });
        }
    }

    private fetchCredentials() {
        if (EntityUtil.isGeneratedId(this.state.account.id)) {
            return;
        }
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount/' + this.state.account.id, {
            method: 'GET',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => {
                        this.setState({
                            eventBus: this.state.eventBus,
                            account: data
                        });
                    });
                }
            });
    }

    private saveCredential(credentialId: string) {
        if (EntityUtil.isGeneratedId(this.state.account.id)) {
            this.saveDisplayName(() => this.saveCredentialNoCheck(credentialId));
        } else {
            this.saveCredentialNoCheck(credentialId);
        }

    }

    private saveCredentialNoCheck(credentialId: string) {
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
                    id: EntityUtil.isGeneratedId(c.id) ? "" : c.id
                })
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                    } else {
                        ToastManager.showSuccessToast("Saved credential '" + c.descriptor + "'.");
                        this.fetchCredentials();
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                    }
                });
        });
    }

    private saveDisplayName(callback?: any) {
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount', {
            method: 'PUT',
            credentials: 'include',

            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({
                id: EntityUtil.isGeneratedId(this.state.account.id) ? "" : this.state.account.id,
                displayName: this.state.account.displayName,
                platformId: this.state.account.platformId
            })
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => {
                        ToastManager.showSuccessToast("Updated social media account properties.");
                        this.setState({
                            eventBus: this.state.eventBus,
                            account: data
                        });
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS, undefined);
                        if (callback) {
                            callback();
                        }
                    });
                }
            })

    }

    private removeCredential(credentialId: string) {
        fetch(DeploymentManager.getUrl() + 'api/credential/' + credentialId, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    ToastManager.showSuccessToast("Updated social media account properties.");
                    this.fetchCredentials();
                }
            });
    }


    private updateProperty(id: string, value: string) {
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
        } else if (id === "platformId") {
            this.setState({
                account: {
                    credentials: this.state.account.credentials,
                    id: this.state.account.id,
                    platformId: Number.parseInt(value),
                    displayName: this.state.account.displayName,
                    error: this.state.account.error
                }
            });

        }
    }

    private updateStateIfNecessary(property: string, id: string, value: string) {
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
        newCreds.push({ id: EntityUtil.makeId(), value: "", descriptor: "", omitted: false, created: new Date() });
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

    public close() {
        this.state.eventBus.fireEvent(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, undefined);
    }

    public render() {
        const elems = this.state.account.credentials.sort((c1, c2) => new Date(c1.created).getTime() - new Date(c2.created).getTime()).map(cred => {
            const credentialsField = cred.omitted ?
                <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" placeholder="(omitted)" value="" />
                : <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" value={cred.value} />;

            const saveButton = cred.omitted ? undefined : <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveCredential(cred.id)}>{ImageProvider.getImage("save-icon")}</div>;
            return (
                <div className="box-content">
                    <div className="credential">
                        <div className="credential-label">Descriptor: </div>
                        <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("descriptor", o.currentTarget.id, o.currentTarget.value)} id={"descriptor_" + cred.id} type="text" value={cred.descriptor} />
                        <div className="btn-icon btn-icon-delete btn-credentials" onClick={o => this.removeCredential(cred.id)}>{ImageProvider.getImage("none")} </div>
                    </div>
                    <div className="credential">
                        <div className="credential-label">Value: </div>
                        {credentialsField}
                        {saveButton}
                    </div>

                </div>
            );
        });
        return (
            <div className="container double-container container-top-margin" >
                <div className="box-header box-header-with-icon">
                    <div className="inline-block"> Account Details for {this.state.account.displayName}</div>
                    <div className="btn-small" onClick={this.close.bind(this)}>✖︎</div>
                </div>
                <div className="box-content">
                    <div className="box-content">
                        <div className="credential">
                            <div className="credential-label">Display Name: </div>
                            <input placeholder="Enter a display name." className="credential-field textarea monospace-font credential-field-select" onChange={o => this.updateProperty(o.currentTarget.id, o.currentTarget.value)} id="displayName" type="text" value={this.state.account.displayName} />
                            <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveDisplayName()}>{ImageProvider.getImage("save-icon")}</div>
                        </div>
                        <div className="credential">
                            <div className="credential-label">Platform: </div>
                            <select value={this.state.account.platformId} placeholder="Enter a platform." id="platformId" className="credential-field textarea monospace-font" onChange={o => this.updateProperty(o.currentTarget.id, o.currentTarget.value)}>
                                <option value="0">&nbsp;</option>
                                <option value="1">Facebook</option>
                                <option value="2">Twitter</option>
                            </select>
                            <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveDisplayName()}>{ImageProvider.getImage("save-icon")}</div>
                        </div>
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