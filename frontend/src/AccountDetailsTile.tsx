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
    eventBus: EventBus
    account?: SocialMediaAccount
}
export interface AccountDetailsState {
    account: SocialMediaAccount,
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

/** Contains a form to create and update account proerties like name and social media network. Allows to create, update and delete associated credentials. */
export class AccountDetailsTile extends React.Component<AccountDetailsProps, AccountDetailsState>{
    constructor(props: AccountDetailsProps, state: AccountDetailsState) {
        super(props);
        this.state = { account: props.account ? props.account : { credentials: [], displayName: "<none>", id: "", platformId: -1 } };
        this.props.eventBus.register(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, (type, acc) => this.updateSelected(acc));
    }

    /*Refresh the details view with a new selected account. */
    private updateSelected(data: any) {
        console.log(data);
        if (data) {
            this.setState({
                account: data,
                displayNameLastSaved: data.displayName,
                platformIdLastSaved: data.platformId
            }, () => this.fetchCredentials());
        }
    }

    /**Fetch the credentials for the currently selected social media account from the backend. 
     * Refreshes all accounts afterwards.
    */
    private fetchCredentials() {
        if (EntityUtil.isGeneratedId(this.state.account.id)) {
            return;
        }
        fetch(`${DeploymentManager.getUrl()}api/socialmediaaccount/${this.state.account.id}`, {
            method: 'GET',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => {
                        this.setState({
                            account: this.mergeCredentials(data),
                            displayNameLastSaved: data.displayName,
                            platformIdLastSaved: data.platformId
                        });
                    });
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                }
            });
    }

    /** Merges the credentials of the new account with the unsaved ones to avoid losing unsaved changes. 
     * This would happen because the account is refreshed on save. 
     * Refreshes the selected account afterwards.*/
    private mergeCredentials(account: SocialMediaAccount): SocialMediaAccount {
        this.state.account.credentials.forEach(c => {
            if (account.credentials.filter(x => x.descriptor === c.descriptor).length === 0) {
                account.credentials.push(c);
            }
        })
        return account;
    }

    /** Save the given credential to the currently selected account. This will also save the account details (name and platform),
     *  if the account was newly created. 
     * Refreshes the selected account afterwards. */
    private saveCredential(credentialId: string) {
        if (EntityUtil.isGeneratedId(this.state.account.id)) {
            this.saveAccountDetails(() => this.saveCredentialNoCheck(credentialId));
        } else {
            this.saveCredentialNoCheck(credentialId);
        }
    }

    /**Saves the credentials without checking if the account was just created. 
     * Refreshes the selected account afterwards.
    */
    private saveCredentialNoCheck(credentialId: string) {
        this.state.account.credentials.filter(cr => cr.id === credentialId).forEach(c => {
            fetch(`${DeploymentManager.getUrl()}api/credential`, {
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
                        this.props.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                    }
                });
        });
    }

    /** Saves the account details (display name and selected platform).
     * Refreshes all accounts afterwards.*/
    private saveAccountDetails(callback?: any) {
        fetch(`${DeploymentManager.getUrl()}api/socialmediaaccount`, {
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
                            account: this.mergeCredentials(data),
                            displayNameLastSaved: data.displayName,
                            platformIdLastSaved: data.platformId
                        });
                        this.props.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS, undefined);
                        if (callback) {
                            callback();
                        }
                    });
                }
            });
    }

    /**Remove a given credential from a social media account.
     * Refreshes the credentials for the given account afterwards.
     */
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
            fetch(`${DeploymentManager.getUrl()}api/credential/${credentialId}`, {
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

    /**Update the current state of a given property without persisting it to the backend. */
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

    /** Realize a template of credentials necessary to integrate a given social media platform. */
    private prepareCredentials(platformId: string) {
        if (platformId === "1") {
            SnippetManager.asyncReduceFn([
                this.addCredentialIfNotPresent.bind(this, "facebook.page.id"),
                this.addCredentialIfNotPresent.bind(this, "facebook.page.accesstoken"),
                this.updateProperty.bind(this, "platformId", platformId),
            ]);
        }
        else if (platformId === "2") {
            SnippetManager.asyncReduceFn([
                this.addCredentialIfNotPresent.bind(this, "twitter.api.accountname"),
                this.addCredentialIfNotPresent.bind(this, "twitter.api.key"),
                this.addCredentialIfNotPresent.bind(this, "twitter.api.secret"),
                this.addCredentialIfNotPresent.bind(this, "twitter.api.accesstoken"),
                this.addCredentialIfNotPresent.bind(this, "twitter.api.accesstoken.secret"),
                this.updateProperty.bind(this, "platformId", platformId),
            ]);
        } else {
            this.updateProperty("platformId", platformId);
        }
    }

    /** Update the current state of a given credential without persisting it to the backend. */
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
            account: this.state.account
        });
    }

    /** Update the ommited state of a given credential. Indicates if there are unsaved changes in the credential.*/
    private updateOmittedState(cred: Credential, value: string) {
        if (value) {
            cred.omitted = false;
        } else {
            cred.omitted = true;
        }
    }

    /** Insert a new credential with a given descriptor if none is already present. 
     * Used to insert template credentials for social media platforms without duplication.
     */
    private addCredentialIfNotPresent(descriptor: string, resolve: () => void, reject: () => void) {
        if (this.state.account.credentials.filter(c => c.descriptor === descriptor).length === 0) {
            this.addCredential(descriptor, resolve, reject);
        } else {
            resolve();
        }
    }

    /**Add a credential to a social media account without persisting it to the backend. */
    private addCredential(descriptor?: string, resolve?: () => void, reject?: () => void) {
        const newCreds = Object.assign([], this.state.account.credentials);
        newCreds.push({ id: EntityUtil.makeId(), value: "", descriptor: descriptor ? descriptor : "", omitted: false, created: new Date() });
        this.setState({
            account: {
                credentials: newCreds,
                displayName: this.state.account.displayName,
                id: this.state.account.id,
                error: this.state.account.error,
                platformId: this.state.account.platformId
            }
        }, resolve);
    }

    /**Close the details view. */
    public close() {
        this.props.eventBus.fireEvent(EventBusEventType.SELECTED_SOCIAL_MEDIA_ACCOUNT_CHANGED, undefined);
    }
    private createInfo(url: string) {
        return SnippetManager.createInfo(url, "Find out more about how to get the API keys and secrets ", ["margin-left-big", "info-label"]);
    }

    public render() {
        //table of credentials for the currently selected social media account
        const credentialElements = this.state.account.credentials.sort((c1, c2) => !c1 ? 1 : c1.descriptor.localeCompare(c2.descriptor)).map(cred => {
            const credentialsField = cred.omitted ?
                <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" placeholder={cred.value} value="" />
                : <input className="credential-field textarea monospace-font" onChange={o => this.updateStateIfNecessary("value", o.currentTarget.id, o.currentTarget.value)} id={"value_" + cred.id} type="text" value={cred.value} />;

            //save button: is omitted if no change occured.
            const saveButton = cred.omitted ? undefined : <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveCredential(cred.id)}>{ImageProvider.getImage("save")}</div>;
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

        //save buttons for display name and platform which are only visible if there were changes.
        const displayNameSaveButton = this.state.displayNameLastSaved !== this.state.account.displayName && <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveAccountDetails()}>{ImageProvider.getImage("save")}</div>;
        const platformSaveButton = this.state.platformIdLastSaved !== this.state.account.platformId && <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveAccountDetails()}>{ImageProvider.getImage("save")}</div>;
        return (
            <div className="container double-container container-top-margin" >
                <div className="box-header box-header-with-icon">
                    <div className="inline-block"> Account Details for {this.state.account.displayName ? this.state.account.displayName : "<unnamed>"}
                        {SnippetManager.isLinked(this.state.account) && <span className="table-icon superscript-icon">
                            {SnippetManager.isLinked(this.state.account) && <span onClick={() => SnippetManager.goToAccount(this.state.account)} >{ImageProvider.getImage("link")}
                            </span>}
                        </span>}</div>
                    <div>
                        <div className="btn btn-icon btn-add btn-header" onClick={(_e) => this.addCredential()}>
                            <div className="align-middle">{ImageProvider.getImage("add")}<span className="btn-header-text">Add Credential</span></div>
                        </div>
                        <div className="btn-small inline-block align-middle" onClick={this.close.bind(this)}>✖︎</div>
                    </div>
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
                            this.state.account.platformId && this.state.account.platformId === 1 ? this.createInfo(SnippetManager.createConfluenceLink("Connecting+with+a+Facebook+Page")) :
                                (this.state.account.platformId === 2 &&
                                    this.createInfo(SnippetManager.createConfluenceLink("Connecting+with+a+Twitter+Account")))
                        }
                    </div>
                    <div className="displayName display-name-bold inline-block">Credentials: </div>
                    {credentialElements}
                </div>
            </div >
        );
    }
}