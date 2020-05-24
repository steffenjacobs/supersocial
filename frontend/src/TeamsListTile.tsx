import * as React from "react";
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";
import { EntityUtil } from "./EntityUtil";
import { TeamDetailsTile } from "./TeamDetailsTile";

export interface TeamsListTileProps {
    eventBus: EventBus
}

interface TeamMember{
    id: string
    name: string
}

export interface Team {
    id: string
    name: string
    users: TeamMember[]
    error?: string
}

interface Teams {
    teams: Team[]
    updating?: boolean
    selected?: Team
}

/** Lists the teams the current user has access to. */

//TODO: make table scrollable and pageable.
export class TeamsListTile extends React.Component<TeamsListTileProps, Teams>{
    constructor(props: TeamsListTileProps, state: Teams) {
        super(props);
        this.state = { teams: [], updating: true };
        this.refreshTeams(true);
        this.props.eventBus.register(EventBusEventType.REFRESH_TEAMS, (eventType, eventData) => this.refreshTeams());
        this.props.eventBus.register(EventBusEventType.SELECTED_TEAM_CHANGED, (eventType, eventData) => this.selectTeam(eventData));
    }

    /** Triggers a refresh of this list. This is also triggered when a REFRESH_TEAMS event is received via the EventBus. */
    private refreshTeams(notMounted?: boolean) {
        if (!notMounted) {
            this.setState({ teams: this.state.teams, updating: true });
        }
        fetch(`${DeploymentManager.getUrl()}api/organization`, {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => this.setState({
                        teams: data,
                        updating: false,
                        selected: data.filter(a => a.id === (this.state.selected ? this.state.selected.id : undefined)) ? this.state.selected : undefined
                    }));
                }
            });
    }

    /** Selects a team in the TeamDetailsTile */
    private selectTeam(team: Team) {
        if (team && (team.id === (this.state.selected ? this.state.selected?.id : false))) {
            return;
        }
        if (!team) {
            this.setState({
                selected: undefined
            });
            return;
        }
        this.setState({
            selected: {
                id: team.id,
                name: team.name,
                users: team.users
            }
        }, () => this.props.eventBus.fireEvent(EventBusEventType.SELECTED_TEAM_CHANGED, team));
    }

    /** Delete a given team in the backend.
     * Updates the list of teams afterwards.
      */
    private deleteTeam(team: Team) {
        fetch(`${DeploymentManager.getUrl()}api/organization/${team.id}`, {
            method: 'delete',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    ToastManager.showSuccessToast("Deleted team '" + team.name + "'");
                    if(this.state.selected && team.id === this.state.selected.id){
                        this.selectTeam(undefined as any);
                    }
                    this.refreshTeams();
                }
            });
    }

    /**Add a new team to the current state without persisting to the backend. */
    private addTeam() {
        this.selectTeam({
            id: EntityUtil.makeId(),
            name: "",
            users: []
        });
    }

    public render() {
        //list of teams
        const teams = this.state.teams.sort(
            (p1, p2) => p1.name.localeCompare(p2.name)).map(elem => {

                //allowed acions for this team
                const allowedActions = (
                    <div className="inline-block">
                        <span className="table-icon">
                            <span onClick={() => this.selectTeam(elem)}>{ImageProvider.getImage("edit")}</span>
                        </span>
                        {<span className="table-icon table-icon-del">
                            <span onClick={() => this.deleteTeam(elem)} >{ImageProvider.getImage("none")}</span>
                        </span>}
                    </div>);

                return (
                    <tr key={elem.id}>
                        <td>{elem.name}</td>
                        <td>{elem.users.length}</td>
                        <td>{allowedActions}</td>
                    </tr>
                );
            });

        //refresh button animation
        let classUpdating = ["align-middle", "inline-block", "btn-icon", "btn-small"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        //details panel
        return (
            <div>
                <div className="container double-container inline-block">
                    <div className="box-header box-header-with-icon">
                        <div className="inline-block">All Teams</div>
                        <div>
                             <div className="btn btn-icon btn-add btn-header font6" onClick={(_e) => this.addTeam()}>
                                <div className="align-middle">{ImageProvider.getImage("add")}<span className="btn-header-text">Add Team</span></div>
                            </div>
                            <div
                                className={classUpdating.join(" ")}
                                onClick={e => this.refreshTeams()}
                            >
                                {ImageProvider.getImage("refresh")}
                            </div>
                        </div>
                    </div>
                    <div className="box-content">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th className="table-text-column">Display Name</th>
                                    <th># Users</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {teams}
                            </tbody>

                        </table>
                    </div>
                </div >
                {this.state.selected ? <TeamDetailsTile eventBus={this.props.eventBus} team={this.state.selected} /> : <div />}
            </div>
        );
    }
}