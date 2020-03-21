import * as React from "react";
import './PublishMessageTile.css';
import './UiTile.css';
import './UiElements.css';

export interface SendTextForm {
    message: string
    platforms: Set<string>
}

export class PublishMessageTile extends React.Component<any, SendTextForm>{
    constructor(props: any, state: SendTextForm) {
        super(props);
        this.state = state;
    }

    private submit() {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/api/publish', false);
        xhr.onload = function () { console.log(this); };
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send(JSON.stringify(this.state));
        console.log("submitted: " + JSON.stringify(this.state));
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
                platforms: this.state.platforms
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
            platforms: platforms
        });
    }

    private validate(id: string, value: string): any {
        return true;
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
                        <textarea className="textarea" placeholder="Enter your message here." id="textMsg" onChange={this.formTextAreaUpdated.bind(this)} />
                        </div>
                    </div>
                    <div>
                        <input id="facebook" type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} />
                        <span>Send to Facebook</span>
                    </div>
                    <div>
                        <input id="twitter" type="checkbox" onChange={this.formInputFieldUpdated.bind(this)} />
                        <span>Send to Twitter</span>
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