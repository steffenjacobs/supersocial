import React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { LoginManager } from "./LoginManager";
import { Redirect } from "react-router-dom";

export interface LoginCredentials {
    username: string
    password: string
    eventBus: EventBus
    loginManager: LoginManager
    loggedIn?: boolean
}

export class Login extends React.Component<LoginCredentials, LoginCredentials> {
    constructor(props: LoginCredentials, state: LoginCredentials) {
        super(props);
        this.state = {username: props.username, password: props.password, eventBus: props.eventBus, loginManager: props.loginManager, loggedIn: props.loginManager.isLoggedIn()};
        props.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
    }

    private onUserChange(eventData?: any) {
        this.setState({username: this.state.username, password: this.state.password,
            eventBus: this.state.eventBus,
            loginManager: this.state.loginManager, loggedIn: eventData.loggedIn});
    }

    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    private update(id: string, value: string) {
        if (id === "username") {
            this.setState({
                username: value,
                password: this.state.password,
                eventBus: this.state.eventBus,
                loginManager: this.state.loginManager,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "password") {
            this.setState({
                username: this.state.username,
                password: value,
                eventBus: this.state.eventBus,
                loginManager: this.state.loginManager,
                loggedIn: this.state.loggedIn
            });
        }
    }

    private submit() {
        this.state.loginManager.logIn(this.state.username, this.state.password);
    }

    public render() {
        if (this.state.loggedIn) {
            return <Redirect to="/" />;
        }
        return (
            <div className="container centered-container">
                <div className="box-header">
                    Sign In
                        </div>
                <div className="box-content">
                    <div>
                        <div className="messageLabel">Username</div>
                        <input className="textarea" placeholder="Enter Username" id="username" onChange={this.formInputFieldUpdated.bind(this)} value={this.state.username} />

                        <div className="messageLabel">Password</div>
                        <input type="password" className="textarea" placeholder="Enter Password" id="password" onChange={this.formInputFieldUpdated.bind(this)} value={this.state.password} />
                    </div>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.submit.bind(this)}
                    >
                        Sign In &gt;
            </button>
                </div>
            </div>
        );
    }
}
