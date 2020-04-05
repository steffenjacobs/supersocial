import * as React from "react";
import './PublishMessageTile.css';
import './AccountDetailsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";
import { SocialMediaAccount } from "./SocialMediaAccountsListTile";
import { ToastManager } from "./ToastManager";
import { EntityUtil } from "./EntityUtil";
import { SnippetManager } from "./SnippetManager";

export interface AccountDetailsProps {
    account: SocialMediaAccount,
    eventBus: EventBus
    displayNameLastSaved?: string
    platformIdLastSaved?: number
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
        this.state = { eventBus: props.eventBus, account: props.account, displayNameLastSaved: props.account.displayName, platformIdLastSaved: props.account.platformId };
        this.fetchCredentials();
        this.state.eventBus.register(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, this.updateSelected.bind(this));
    }

    private updateSelected(type: EventBusEventType, data: any) {
        if (data && data.id === this.state.account.id) {
            this.setState({
                account: data,
                displayNameLastSaved: data.displayName,
                platformIdLastSaved: data.platformId
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
                            account: this.mergeCredentials(data),
                            displayNameLastSaved: data.displayName,
                            platformIdLastSaved: data.platformId
                        });
                    });
                    this.state.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                }
            });
    }

    private mergeCredentials(account: SocialMediaAccount): SocialMediaAccount {
        this.state.account.credentials.forEach(c => {
            if (account.credentials.filter(x => x.descriptor === c.descriptor).length === 0) {
                account.credentials.push(c);
            }
        })
        return account;
    }

    private saveCredential(credentialId: string) {
        if (EntityUtil.isGeneratedId(this.state.account.id)) {
            this.saveAccountDetails(() => this.saveCredentialNoCheck(credentialId));
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

    private saveAccountDetails(callback?: any) {
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
                            account: this.mergeCredentials(data),
                            displayNameLastSaved: data.displayName,
                            platformIdLastSaved: data.platformId
                        });
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS, undefined);
                        if (callback) {
                            callback();
                        }
                    });
                }
            });
    }

    private removeCredential(credentialId: string) {
        this.setState({
            account: {
                id: this.state.account.id,
                displayName: this.state.account.displayName,
                platformId: this.state.account.platformId,
                credentials: this.state.account.credentials.filter(x => x.id !== credentialId)
            }
        }, () => {
            if (EntityUtil.isGeneratedId(credentialId)) {
                return;
            }
            fetch(DeploymentManager.getUrl() + 'api/credential/' + credentialId, {
                method: 'DELETE',
                credentials: 'include'
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                    } else {
                        ToastManager.showSuccessToast("Removed credential.");
                        this.fetchCredentials();
                    }
                });
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

    private prepareCredentials(platformId: string) {
        if (platformId === "1") {
            this.addCredentialIfNotPresent("facebook.page.id", () =>
                this.addCredentialIfNotPresent("facebook.page.accesstoken", () =>
                    this.updateProperty("platformId", platformId)
                ));
        }
        else if (platformId === "2") {
            this.addCredentialIfNotPresent("twitter.api.key", () =>
                this.addCredentialIfNotPresent("twitter.api.secret", () =>
                    this.addCredentialIfNotPresent("twitter.api.accesstoken", () =>
                        this.addCredentialIfNotPresent("twitter.api.accesstoken.secret", () =>
                            this.updateProperty("platformId", platformId)
                        ))));
        } else {
            this.updateProperty("platformId", platformId);
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
    private addCredentialIfNotPresent(descriptor: string, callback?: any) {
        if (this.state.account.credentials.filter(c => c.descriptor === descriptor).length === 0) {
            this.addCredential(descriptor, callback);
        } else {
            if (callback) {
                callback();
            }
        }
    }

    private addCredential(descriptor?: string, callback?: any) {
        const newCreds = Object.assign([], this.state.account.credentials);
        newCreds.push({ id: EntityUtil.makeId(), value: "", descriptor: descriptor ? descriptor : "", omitted: false, created: new Date() });
        this.setState({
            eventBus: this.state.eventBus,
            account: {
                credentials: newCreds,
                displayName: this.state.account.displayName,
                id: this.state.account.id,
                error: this.state.account.error,
                platformId: this.state.account.platformId
            }
        }, callback);
    }

    public close() {
        this.state.eventBus.fireEvent(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, undefined);
    }
    private createInfo(url: string){
        return SnippetManager.createInfo(url, "Find out more about how to get the API keys and secrets ", "margin-left-big");
    }

    public render() {
        const credentialElements = this.state.account.credentials.sort((c1, c2) => c1.descriptor.localeCompare(c2.descriptor)).map(cred => {
            const credentialsField = cred.omitted ?
                <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" placeholder="(omitted)" value="" />
                : <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" value={cred.value} />;

            const saveButton = cred.omitted ? undefined : <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveCredential(cred.id)}>{ImageProvider.getImage("save-icon")}</div>;
            return (
                <div key={cred.id} className="box-content">
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

        const displayNameSaveButton = this.state.displayNameLastSaved !== this.state.account.displayName && <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveAccountDetails()}>{ImageProvider.getImage("save-icon")}</div>;
        const platformSaveButton = this.state.platformIdLastSaved !== this.state.account.platformId && <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveAccountDetails()}>{ImageProvider.getImage("save-icon")}</div>;
        return (
            <div className="container double-container container-top-margin" >
                <div className="box-header box-header-with-icon">
                    <div className="inline-block"> Account Details for {this.state.account.displayName ? this.state.account.displayName : "<unnamed>"}</div>
                    <div className="btn-small" onClick={this.close.bind(this)}>✖︎</div>
                </div>
                <div className="box-content">
                    <div className="box-content">
                        <div className="credential">
                            <div className="credential-label">Display Name: </div>
                            <input placeholder="Enter a display name." className="credential-field textarea monospace-font credential-field" onChange={o => this.updateProperty(o.currentTarget.id, o.currentTarget.value)} id="displayName" type="text" value={this.state.account.displayName} />
                            {displayNameSaveButton}
                        </div>
                        <div className="credential">
                            <div className="credential-label">Platform: </div>
                            <select value={this.state.account.platformId} placeholder="Enter a platform." id="platformId" className="credential-field-select textarea monospace-font" onChange={o => this.prepareCredentials(o.currentTarget.value)}>
                                <option value="0">&nbsp;</option>
                                <option value="1">Facebook</option>
                                <option value="2">Twitter</option>
                            </select>
                            {platformSaveButton}
                        </div>
                        {
                            this.state.account.platformId && this.state.account.platformId == 1 ? this.createInfo("https://confluence.supersocial.cloud/display/SP/Connecting+with+a+Facebook+Page"):
                                (this.state.account.platformId == 2 &&
                                this.createInfo("https://confluence.supersocial.cloud/display/SP/Connecting+with+a+Twitter+Account"))
                        }
                    </div>
                    <div className="displayName">Credentials: </div>
                    {credentialElements}
                    <div className="btn btn-icon btn-add btn-icon-big" onClick={(_e) => this.addCredential()}>
                        <div className="btn-add-inner">{ImageProvider.getImage("add-icon")}</div>
                    </div>
                </div>
            </div >
        );
    }
}