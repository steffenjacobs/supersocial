import * as React from "react";
import './PublishMessageTile.css';
import './CredentialSettingsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";

export interface CredentialList {
    credentials: Credential[]
    eventBus: EventBus
}

export interface Credential {
    id: string;
    value: string;
    descriptor: string;
    omitted: boolean;
}

/** Contains a form to publish new messages. */
export class CredentialSettingsTile extends React.Component<CredentialList, CredentialList>{
    constructor(props: CredentialList) {
        super(props);
        this.state = { eventBus: props.eventBus, credentials: [] };
        this.fetchCredentials();
    }

    private fetchCredentials() {
        fetch(DeploymentManager.getUrl() + 'api/credential', {
            method: 'GET',
            credentials: 'include',
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                this.setState({
                    eventBus: this.state.eventBus,
                    credentials: (data as Credential[]).sort((p1, p2) => p1.descriptor.localeCompare(p2.descriptor))
                });
            });
    }

    private saveCredential(credentialId: string) {
        this.state.credentials.filter(cr => cr.id === credentialId).forEach(c => {
            fetch(DeploymentManager.getUrl() + 'api/credential', {
                method: 'PUT',
                headers: new Headers({
                    'Content-Type': 'application/json'
                }),
                credentials: 'include',
                body: JSON.stringify({
                    descriptor: c.descriptor,
                    value: c.value
                })
            })
                .then(data => {
                    this.fetchCredentials();
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
            return;
        }

        let identifier = id.substring(property.length + 1);
        let cred = this.state.credentials.find(c => c.id === identifier);

        if (cred) {
            if (property === 'descriptor') {
                cred.descriptor = value;
            }
            else if (property === 'value') {
                cred.value = value;
            }
            this.updateOmittedState(cred, value);
        }

        this.setState({
            eventBus: this.state.eventBus,
            credentials: this.state.credentials
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
        const newCreds = Object.assign([], this.state.credentials);
        newCreds.push({ id: this.makeString(), value: "", descriptor: "", omitted: false });
        this.setState({
            eventBus: this.state.eventBus,
            credentials: newCreds
        });
    }

    /** @return a random string to be used as an identifier in the UI. This will not be the identifier used in the back end. */
    makeString(): string {
        let outString: string = '';
        let inOptions: string = 'abcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < 32; i++) {
            outString += inOptions.charAt(Math.floor(Math.random() * inOptions.length));
        }
        return outString;
    }

    public render() {
        const elems = this.state.credentials.map(cred => {
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
                    Stored Credentials
                </div>
                <div className="box-content">
                    {elems}
                    <div className="btn btn-icon btn-add btn-icon-big">
                        <div className="btn-add-inner" onClick={this.addCredential.bind(this)} >{ImageProvider.getImage("add-icon")}</div>
                    </div>
                </div>
            </div >
        );
    }
}