import * as React from "react";
import './PublishMessageTile.css';
import './UiTile.css';
import './UiElements.css';
import './react-datetime.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import Datetime from 'react-datetime';

export interface SendTextForm {
    message: string
    platforms: Set<string>
    eventBus: EventBus
    schedule: boolean
    scheduled: Date
}

/** Contains a form to publish new messages. */
export class PublishMessageTile extends React.Component<SendTextForm, SendTextForm>{
    constructor(props: SendTextForm, state: SendTextForm) {
        super(props);
        this.state = { message: props.message, platforms: props.platforms, eventBus: props.eventBus, scheduled: props.scheduled, schedule: props.schedule };

        this.state.eventBus.register(EventBusEventType.SELECTED_POST_CHANGED, (eventType, eventData?) => this.selectPost(eventData));
    }

    /** Event handler for when a post had been selected, e.g. via PublishedPostsTile.tsx */
    private selectPost(eventData?: any) {
        var platforms = new Set<string>();
        platforms.add("" + eventData.platformId);
        this.setState({
            message: eventData.text,
            platforms: platforms,
            eventBus: this.state.eventBus,
            schedule: this.state.schedule,
            scheduled: this.state.scheduled
        });
    }

    private createPost(callback?: Function) {
        fetch(DeploymentManager.getUrl() + 'api/post', {
            method: 'PUT',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({ message: this.state.message, platforms: Array.from(this.state.platforms.values()) })
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                console.log("Result: " + data);
                this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                if (callback) {
                    data.forEach(element => {
                        callback(element.id);
                    });
                }
            });
    }

    private createAndPublishPost() {
        fetch(DeploymentManager.getUrl() + 'api/publish', {
            method: 'POST',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({ message: this.state.message, platforms: Array.from(this.state.platforms.values()) })
        })
            .then(response => {
                response.json();
            })
            .then(data => {
                console.log("Result: " + data);
                this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
            });
    }

    private createScheduledPost(postId: string) {
        fetch(DeploymentManager.getUrl() + 'api/schedule/post', {
            method: 'PUT',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({ postId: postId, scheduled: this.state.scheduled })
        })
            .then(response => {
                response.json();
            })
            .then(data => {
                console.log("Schedule result: " + data);
                this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
            });
    }

    /** Send the request to the back end triggerin a post on the selected platforms. */
    private send() {
        if (this.state.schedule) {
            this.createPost(this.createScheduledPost.bind(this));
        }
        else {
            if (this.state.platforms.size === 0) {
                this.createPost();
            } else {
                this.createAndPublishPost();
            }
        }
    }

    private formTextAreaUpdated(event: React.ChangeEvent<HTMLTextAreaElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, "" + event.currentTarget.checked);
    }

    private update(id: string, value: string) {
        if (id === "textMsg") {
            this.setState({
                message: value,
                platforms: this.state.platforms,
                eventBus: this.state.eventBus,
                schedule: this.state.schedule,
                scheduled: this.state.scheduled
            });
        }
        if (id === "1" || id === "2") {
            this.updateCheckbox(id, value === "true");
        }
    }

    private updateCheckbox(id: string, value: boolean) {
        var platforms = this.state.platforms;
        if (!platforms) {
            platforms = new Set<string>();
        }
        if (value) {
            platforms.add(id);
        } else {
            platforms.delete(id);
        }

        this.setState({
            message: this.state.message,
            platforms: platforms,
            eventBus: this.state.eventBus,
            schedule: this.state.schedule,
            scheduled: this.state.scheduled
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
            message: this.state.message,
            platforms: this.state.platforms,
            eventBus: this.state.eventBus,
            schedule: event.currentTarget.checked,
            scheduled: this.state.scheduled
        });
    }

    onDateChange = date => this.setState({
        message: this.state.message,
        platforms: this.state.platforms,
        eventBus: this.state.eventBus,
        schedule: true,
        scheduled: date
    })

    public render() {
        var checkBoxSchedule;
        if (this.state.schedule) {
            checkBoxSchedule = <input type="checkbox" onChange={this.updateSchedulingMode.bind(this)} checked />
        } else {

            checkBoxSchedule = <input type="checkbox" onChange={this.updateSchedulingMode.bind(this)} />
        }
        return (
            <div className="container">
                <div className="box-header">
                    Publish Message
                </div>
                <div className="box-content">
                    <div>
                        <div className="messageLabel">Message</div>
                        <div>
                            <textarea className="textarea" placeholder="Enter your message here." id="textMsg" onChange={this.formTextAreaUpdated.bind(this)} value={this.state.message} />
                        </div>
                    </div>

                    <div className="messageLabel">Schedule Publishing</div>
                    {checkBoxSchedule}
                    <Datetime dateFormat="YYYY-MM-DD" timeFormat="HH:mm" closeOnSelect={true} onChange={this.onDateChange} value={this.state.scheduled} />
                    <div className="channel-selection">
                        <div className="messageLabel">Distribution Channels</div>
                        <div>
                            {this.createCheckbox("1", this.state.platforms.has("1"))}
                            <span>Distribute via Facebook</span>
                        </div>
                        <div>
                            {this.createCheckbox("2", this.state.platforms.has("2"))}
                            <span>Distribute via Twitter</span>
                        </div>
                    </div>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.send.bind(this)}
                    >
                        Send
                    </button>
                </div>
            </div>
        );
    }
}