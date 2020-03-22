import * as React from "react";
import './PublishMessageTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";

export interface SendTextForm {
    message: string
    platforms: Set<string>
    eventBus: EventBus
}

export class PublishMessageTile extends React.Component<SendTextForm, SendTextForm>{
    constructor(props: SendTextForm, state: SendTextForm) {
        super(props);
        this.state = props;

        this.state.eventBus.register(EventBusEventType.SELECTED_POST_CHANGED, (eventType, eventData?) => this.selectPost(eventData));
    }

    private selectPost(eventData?: any) {
        var platforms = new Set<string>();
        platforms.add("" + eventData.platformId);
        this.setState({
            message: eventData.text,
            platforms: platforms,
            eventBus: this.state.eventBus
        });
    }

    private submit() {
        fetch('http://localhost:8080/api/publish', {
            method: 'POST',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            credentials: 'include',
            body: JSON.stringify({message: this.state.message, platforms: Array.from(this.state.platforms.values())})
        })
            .then(response => {
                response.json();})
            .then(data => {
                console.log("Result: " + data);
                this.state.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
            });
    }

    private formTextAreaUpdated(event: React.ChangeEvent<HTMLTextAreaElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    private update(id: string, value: string) {
        if (id === "textMsg") {
            this.setState({
                message: value,
                platforms: this.state.platforms,
                eventBus: this.state.eventBus
            });
        }
        if (id === "facebook" || id === "twitter") {
            this.updateCheckbox(id, value);
        }
    }

    private updateCheckbox(id: string, value: string) {
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
            eventBus: this.state.eventBus
        });
    }

    private createCheckbox(id: string, checked: boolean) {
        if (checked) {
            return <input id={id} type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} checked />
        }
        return <input id={id} type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} />
    }

    public render() {
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
                    <div className="channel-selection">
                        <div className="messageLabel">Distribution Channels</div>
                        <div>
                            {this.createCheckbox("facebook", this.state.platforms.has("1"))}
                            <span>Distribute via Facebook</span>
                        </div>
                        <div>
                            {this.createCheckbox("twitter", this.state.platforms.has("2"))}
                            <span>Distribute via Twitter</span>
                        </div>
                    </div>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.submit.bind(this)}
                    >
                        Send
                        </button>
                </div>
            </div>
        );
    }
}