import * as React from "react";
import './PublishMessageTile.css';
import './UiTile.css';
import './UiElements.css';
import './react-datetime.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import Datetime from 'react-datetime';
import { SocialMediaAccount } from "./SocialMediaAccountsListTile";
import { ImageProvider } from "./ImageProvider";
import { toast } from "react-toastify";
import { ToastManager } from "./ToastManager";

export interface PublishMessageTileState {
    sendTextForm: SendTextForm
    accounts: SocialMediaAccount[]
    eventBus: EventBus
}

export interface SendTextForm {
    message: string
    accountIds: string[]
    schedule: boolean
    scheduled: Date
}

/** Contains a form to publish new messages. */
export class PublishMessageTile extends React.Component<PublishMessageTileState, PublishMessageTileState>{
    constructor(props: PublishMessageTileState, state: PublishMessageTileState) {
        super(props);
        this.state = { sendTextForm: props.sendTextForm, accounts: props.accounts, eventBus: props.eventBus };

        this.state.eventBus.register(EventBusEventType.SELECTED_POST_CHANGED, (eventType, eventData?) => this.selectPost(eventData));
        this.refreshAccounts();
        this.state.eventBus.register(EventBusEventType.REFRESH_SOCIAL_MEDIA_ACCOUNTS, this.refreshAccounts.bind(this));
    }

    private refreshAccounts() {
        fetch(DeploymentManager.getUrl() + 'api/socialmediaaccount', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => this.setState({
                        accounts: data
                    }));
                }
            });
    }

    /** Event handler for when a post had been selected, e.g. via PublishedPostsTile.tsx */
    private selectPost(eventData?: any) {
        var platforms = new Set<string>();
        platforms.add("" + eventData.platformId);
        this.setState({
            sendTextForm: {
                message: eventData.text,
                accountIds: eventData.accountIds,
                schedule: eventData.scheduled ? true : false,
                scheduled: eventData.scheduled ? eventData.scheduled : new Date()
            }
        });
    }

    private createPost(callback?: Function) {
        if(this.state.accounts.length === 0){
            ToastManager.showClickableInfoToast("Please create a social media account to publish to.");
            return;
        }
        if(this.state.sendTextForm.accountIds.length === 0){
            ToastManager.showWarnToast("Select a social media account to publish to.");
        }
        this.state.sendTextForm.accountIds.forEach(accId => {
            fetch(DeploymentManager.getUrl() + 'api/post', {
                method: 'PUT',
                headers: new Headers({
                    'Content-Type': 'application/json'
                }),
                credentials: 'include',
                body: JSON.stringify({ message: this.state.sendTextForm.message, accountId: accId })
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                    } else {
                        ToastManager.showSuccessToast("Created unpublished toast.");
                        response.json().then(data => {
                            this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                            if (callback) {
                                callback(data.id);
                            }
                        });
                    }
                });

        });
    }

    private createAndPublishPost() {
        if(this.state.accounts.length === 0){
            ToastManager.showClickableInfoToast("Please create a social media account to publish to.");
            return;
        }
        if(this.state.sendTextForm.accountIds.length === 0){
            ToastManager.showWarnToast("Select a social media account to publish to.");
        }
        this.state.sendTextForm.accountIds.forEach(accId => {
            fetch(DeploymentManager.getUrl() + 'api/publish', {
                method: 'POST',
                headers: new Headers({
                    'Content-Type': 'application/json'
                }),
                credentials: 'include',
                body: JSON.stringify({ message: this.state.sendTextForm.message, accountId: accId })
            })
                .then(response => {
                    if (!response.ok) {
                        ToastManager.showErrorToast(response);
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                    } else {
                        ToastManager.showSuccessToast("Created and immediately published post.");
                        response.json().then(data => {
                            this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                        });
                    }
                });

        })
    }

    private createScheduledPost(postId: string) {
        fetch(DeploymentManager.getUrl() + 'api/schedule/post', {
            method: 'PUT',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({ postId: postId, scheduled: this.state.sendTextForm.scheduled })
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                    this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                } else {
                    response.json().then(data => {
                        console.log("Schedule result: " + data);
                        this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                    });
                }
            });

    }

    /** Send the request to the back end triggerin a post on the selected platforms. */
    private send() {
        if (this.state.sendTextForm.schedule) {
            this.createPost(this.createScheduledPost.bind(this));
        } else {
            this.createAndPublishPost();
        }
        this.setState({
            sendTextForm: {
                message: "",
                accountIds: [],
                schedule: false,
                scheduled: new Date()
            }
        });
    }

    private formTextAreaUpdated(event: React.ChangeEvent<HTMLTextAreaElement>) {
        this.setState({
            sendTextForm: {
                message: event.currentTarget.value,
                schedule: this.state.sendTextForm.schedule,
                scheduled: this.state.sendTextForm.scheduled,
                accountIds: this.state.sendTextForm.accountIds
            }
        });
    }

    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        var ids = Object.assign([], this.state.sendTextForm.accountIds);
        if (event.currentTarget.checked) {
            ids.push(event.currentTarget.id);
        } else {
            const index = ids.indexOf(event.currentTarget.id, 0);
            if (index > -1) {
                ids.splice(index, 1);
            }
        }
        this.setState({
            sendTextForm: {
                message: this.state.sendTextForm.message,
                schedule: this.state.sendTextForm.schedule,
                scheduled: this.state.sendTextForm.scheduled,
                accountIds: ids
            }
        });
    }

    private createCheckbox(id: string, checked: boolean) {
        if (checked) {
            return <input id={id} type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} checked />
        }
        return <input id={id} type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} />
    }

    private updateSchedulingMode(event: React.ChangeEvent<HTMLInputElement>) {
        this.setState({
            sendTextForm: {
                message: this.state.sendTextForm.message,
                schedule: event.currentTarget.checked,
                scheduled: this.state.sendTextForm.scheduled,
                accountIds: this.state.sendTextForm.accountIds
            }
        });
    }

    onDateChange = date => this.setState({
        sendTextForm: {
            message: this.state.sendTextForm.message,
            schedule: true,
            scheduled: date,
            accountIds: this.state.sendTextForm.accountIds
        }
    })

    public render() {
        var checkBoxSchedule;
        if (this.state.sendTextForm.schedule) {
            checkBoxSchedule = <input type="checkbox" onChange={this.updateSchedulingMode.bind(this)} checked />
        } else {

            checkBoxSchedule = <input type="checkbox" onChange={this.updateSchedulingMode.bind(this)} />
        }

        const accounts = this.state.accounts.sort(
            (a1, a2) => a1.id.localeCompare(a2.id))
            .map(elem => (<div>
                {this.createCheckbox(elem.id, this.state.sendTextForm.accountIds && this.state.sendTextForm.accountIds.includes(elem.id))}
                <span className="icon-intext">{ImageProvider.getSocialmediaIcon(elem.platformId)}</span>
                <span>Distribute via {elem.displayName}</span>
            </div>)
            );

        return (
            <div className="container">
                <div className="box-header">
                    Publish Message
                </div>
                <div className="box-content">
                    <div>
                        <div className="messageLabel">Message</div>
                        <div>
                            <textarea className="textarea" placeholder="Enter your message here." id="textMsg" onChange={this.formTextAreaUpdated.bind(this)} value={this.state.sendTextForm.message} />
                        </div>
                    </div>

                    <div className="messageLabel">Schedule Publishing</div>
                    {checkBoxSchedule}
                    <Datetime dateFormat="YYYY-MM-DD" timeFormat="HH:mm" closeOnSelect={true} onChange={this.onDateChange} value={this.state.sendTextForm.scheduled} />
                    <div className="channel-selection">
                        <div className="messageLabel">Distribution Channels</div>
                        {accounts}
                    </div>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.send.bind(this)}
                    >
                        {this.state.sendTextForm.schedule?"Schedule":"Publish"}
                    </button>
                </div>
            </div>
        );
    }
}