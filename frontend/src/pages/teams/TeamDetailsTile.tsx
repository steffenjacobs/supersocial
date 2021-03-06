import * as React from "react";
import '../../UiTile.css';
import '../../UiElements.css';
import './TeamDetailsTile.css';
import { EventBus, EventBusEventType } from "../../misc/EventBus";
import { DeploymentManager } from "../../misc/DeploymentManager";
import { ImageProvider } from "../../ImageProvider";
import { ToastManager } from "../../misc/ToastManager";
import { EntityUtil } from "../../misc/EntityUtil";
import { SnippetManager } from "../../misc/SnippetManager";
import { Team } from "./TeamsListTile";
import { SocialMediaAccount } from "../settings/SocialMediaAccountsListTile";
import { Multiselect } from 'multiselect-react-dropdown';

export interface TeamDetailsProps {
    eventBus: EventBus
    team?: Team
    accounts: SocialMediaAccount[]
}
export interface TeamDetailsState {
    team: Team,
    displayNameLastSaved?: string
    usernameToAdd?: string
}

//TODO extract from here + AnalyticsPage to permanent location
interface MultiSelectValue {
    label: string
    value: string
    type: string
}

/** Contains a form to create and update team properties like team name and associated users.*/
export class TeamDetailsTile extends React.Component<TeamDetailsProps, TeamDetailsState>{
    constructor(props: TeamDetailsProps, state: TeamDetailsState) {
        super(props);
        this.state = {
            displayNameLastSaved: props.team ? props.team.name : "",
            team: props.team ? props.team : { name: "<none>", id: "", users: [] }
        };
        this.props.eventBus.register(EventBusEventType.SELECTED_TEAM_CHANGED, (type, t) => this.updateSelected(t));
    }

    /*Refresh the details view with a new selected team. */
    private updateSelected(data: Team, keepUsernameField?: boolean): Promise<any> {
        return new Promise<any>((resolve, reject) => {
            if (data) {
                this.setState({
                    team: data,
                    displayNameLastSaved: data.name,
                    usernameToAdd: keepUsernameField ? this.state.usernameToAdd : ""
                }, resolve);
            } else {
                resolve();
            }
        });
    }

    /** Save the team as it currently is in the editor to the back end. */
    private saveTeam() {
        this.setState({ displayNameLastSaved: this.state.team.name }, () => this.createOrUpdateTeam(this.state.team));
    }

    /**Close the details view. */
    public close() {
        this.props.eventBus.fireEvent(EventBusEventType.SELECTED_TEAM_CHANGED);
    }

    /** Update the team name on text field change. */
    private updateNameState(value: string) {
        this.setState({
            team: {
                id: this.state.team.id,
                name: value,
                users: this.state.team.users
            }
        });
    }

    /** Update the name of the user currently added via the text field on. */
    private updateUserToAddState(value: string) {
        this.setState({ usernameToAdd: value });
    }

    /** Create or update the given team to the back end.*/
    private createOrUpdateTeam(team: Team, keepUsernameField?: boolean): Promise<any> {
        return new Promise<any>((resolve, reject) => {
            fetch(`${DeploymentManager.getUrl()}api/organization/`, {
                method: 'PUT',
                credentials: 'include',
                headers: new Headers({
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify({
                    id: EntityUtil.isGeneratedId(team.id) ? "" : team.id,
                    name: team.name,
                    users: team.users
                })
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                        resolve();
                    } else {
                        ToastManager.showSuccessToast(`${EntityUtil.isGeneratedId(team.id) ? "Created" : "Updated"} team.`);
                        this.props.eventBus.fireEvent(EventBusEventType.REFRESH_TEAMS);
                        response.json().then(json => this.updateSelected(json, keepUsernameField).then(x => resolve()));
                    }
                });
        });
    }

    /** Add a user by it's username or id to the currently selected team in the back end. */
    private addUserToTeam() {
        let addUserToTeamCall = function (instance: TeamDetailsTile, state: TeamDetailsState, props: TeamDetailsProps) {
            fetch(`${DeploymentManager.getUrl()}api/organization/${state.team.id}/${state.usernameToAdd}`, {
                method: 'PUT',
                credentials: 'include',
                headers: new Headers({
                    'Content-Type': 'application/json'
                })
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                    } else {
                        ToastManager.showSuccessToast("Added user to team.");
                        props.eventBus.fireEvent(EventBusEventType.REFRESH_TEAMS);
                        response.json().then(json => instance.updateSelected(json));
                    }
                });
        }

        if (EntityUtil.isGeneratedId(this.state.team.id)) {
            this.createOrUpdateTeam(this.state.team, true).then(x => addUserToTeamCall(this, this.state, this.props));
        } else {
            addUserToTeamCall(this, this.state, this.props);
        }
    }

    /** Remove a user based on it's unique user id from the currently selected team in the back end. */
    private removeUserFromTeam(userId: string) {
        if (EntityUtil.isGeneratedId(userId)) {
            this.setState({
                team: {
                    id: this.state.team.id,
                    name: this.state.team.name,
                    users: this.state.team.users.filter(u => u.id !== userId)
                }
            });
            return;
        }
        fetch(`${DeploymentManager.getUrl()}api/organization/${this.state.team.id}/${userId}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_TEAMS);
                    if (response.status === 204) {
                        ToastManager.showSuccessToast("Removed the last user and therefore the entire team.");
                        this.close();
                    } else {
                        ToastManager.showSuccessToast("Removed user from team.");
                        response.json().then(json => this.updateSelected(json));
                    }
                }
            });
    }

    private removeAccountFromTeam(aclId: string) {
        console.log("removing " + aclId);
        fetch(`${DeploymentManager.getUrl()}api/security/acl/${aclId}/${this.state.team.id}/${0}`, {
            method: 'DELETE',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_TEAMS);
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                    ToastManager.showSuccessToast("Removed social media account from team.");
                    //response.json().then(json => this.updateSelected(json));
                }
            });
    }

    private addAccountToTeam(aclId: string) {
        fetch(`${DeploymentManager.getUrl()}api/security/acl/${aclId}/${this.state.team.id}/${31}`, {
            method: 'PUT',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    ToastManager.showSuccessToast("Added social media account to team.");
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_TEAMS);
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS);
                    //response.json().then(json => this.updateSelected(json));
                }
            });
    }

    private onSelectionChange(value: MultiSelectValue[]) {
        let selectedAccounts = this.calculateSelectedAccounts();//.map(x => x.id);

        //added
        value.filter(v => !selectedAccounts.find(x=> x.id === v.value)).forEach(addedAccount => this.addAccountToTeam((this.props.accounts.find(x=>x.id === addedAccount.value) as SocialMediaAccount).aclId));

        //removed
        selectedAccounts.filter(sa => !value.find(v => sa.id === v.value)).forEach(removedAccount => this.removeAccountFromTeam(removedAccount.aclId));
    }

    private calculateSelectedAccounts() {
        return this.props.accounts.sort((c1, c2) => !c1 ? 1 : c1.id.localeCompare(c2.id)).filter(acc => acc.acl.find(p => p.id === this.state.team.id))
    }

    public render() {
        const userElements = this.state.team.users.sort((c1, c2) => !c1 ? 1 : c1.id.localeCompare(c2.id)).map(usr => {
            return (
                <div className="align-middle-sub" key={usr.id}>
                    <span>{`${usr.name} (${usr.id})`}</span>
                    <span className="btn-icon btn-icon-delete btn-credentials" onClick={o => this.removeUserFromTeam(usr.id)}>
                        {ImageProvider.getImage("none")}
                    </span>
                </div>);
        });

        let selectableAccounts = this.props.accounts.sort((c1, c2) => !c1 ? 1 : c1.id.localeCompare(c2.id)).map(a => ({ label: a.displayName, value: a.id, type: "Accounts" }));
        let selectedAccounts = this.props.accounts.sort((c1, c2) => !c1 ? 1 : c1.id.localeCompare(c2.id)).filter(acc => acc.acl.find(p => p.id === this.state.team.id)).map(a => ({ label: a.displayName, value: a.id, type: "Accounts" }));

        const displayNameSaveButton = this.state.displayNameLastSaved !== this.state.team.name && <div className="btn-icon btn-credentials btn-save" onClick={o => this.saveTeam()}>{ImageProvider.getImage("save")}</div>;
        return (
            <div className="container double-container container-top-margin" >
                <div className="box-header box-header-with-icon">
                    <div className="inline-block"> Team Details for {this.state.team.name ? this.state.team.name : "<unnamed>"}</div>
                    <div className="btn-small inline-block align-middle" onClick={this.close.bind(this)}>✖︎</div>
                </div>
                <div className="box-content">
                    <div className="box-content">
                        <div className="credential">
                            <div className="credential-label">Display Name: </div>
                            <input placeholder="Enter a display name." className="credential-field textarea monospace-font credential-field" onChange={o => this.updateNameState(o.currentTarget.value)} id="displayName" type="text" value={this.state.team.name} />
                            {displayNameSaveButton}
                        </div>
                        {SnippetManager.createInfo(SnippetManager.createConfluenceLink("Managing+Teams"), "Find out more about how to manage teams ", ["margin-left-big", "info-label"])}
                    </div>
                    <div>
                        <input placeholder="Enter a user name or user id to add." className="credential-field textarea monospace-font credential-field" onChange={o => this.updateUserToAddState(o.currentTarget.value)} id="usernameToAdd" type="text" value={this.state.usernameToAdd} />
                        <div className="btn btn-icon btn-add btn-header btn-add-user" onClick={(_e) => this.addUserToTeam()}>
                            <div>{ImageProvider.getImage("add")}<span className="btn-header-text">Add User</span></div>
                        </div>
                    </div>
                    <div className="displayName display-name-bold">Users: </div>
                    {userElements}
                    <div className="displayName display-name-bold">Social Media Accounts: </div>
                    <Multiselect
                        placeholder="Select accounts to share"
                        options={selectableAccounts}
                        selectedValues={selectedAccounts}
                        onSelect={this.onSelectionChange.bind(this)}
                        onRemove={this.onSelectionChange.bind(this)}
                        displayValue="label"
                        closeIcon="cancel"
                        groupBy="type"
                    />
                </div>
            </div >
        );
    }
}