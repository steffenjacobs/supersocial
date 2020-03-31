import React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { LoginManager } from "./LoginManager";
import { Redirect } from "react-router-dom";
import { DeploymentManager } from "./DeploymentManager";

export interface RegistrationCredentials {
    username: string
    password: string
    email: string
    eventBus: EventBus
    loginManager: LoginManager
    loggedIn?: boolean
}

/** Login Page. Consult the LoginManger.tsx for further login logic. */
export class Registration extends React.Component<RegistrationCredentials, RegistrationCredentials> {
    constructor(props: RegistrationCredentials, state: RegistrationCredentials) {
        super(props);
        this.state = { username: props.username, password: props.password, email: props.email, eventBus: props.eventBus, loginManager: props.loginManager, loggedIn: props.loginManager.isLoggedIn() };
        props.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
    }

    private onUserChange(eventData?: any) {
        this.setState({
            username: this.state.username,
            password: this.state.password,
            email: this.state.email,
            eventBus: this.state.eventBus,
            loginManager: this.state.loginManager,
            loggedIn: eventData.loggedIn
        });
    }

    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    private update(id: string, value: string) {
        if (id === "username") {
            this.setState({
                username: value,
                password: this.state.password,
                email: this.state.email,
                eventBus: this.state.eventBus,
                loginManager: this.state.loginManager,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "password") {
            this.setState({
                username: this.state.username,
                password: value,
                email: this.state.email,
                eventBus: this.state.eventBus,
                loginManager: this.state.loginManager,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "email") {
            this.setState({
                username: this.state.username,
                password: this.state.password,
                email: value,
                eventBus: this.state.eventBus,
                loginManager: this.state.loginManager,
                loggedIn: this.state.loggedIn
            });
        }
    }

    private signUp() {
        fetch(DeploymentManager.getUrl() + 'api/register', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({ displayName: this.state.username, email: this.state.email, password: this.state.password })
        })
            .then(response => {
                if (response.ok) {
                    this.state.loginManager.logIn(this.state.username, this.state.password);
                } else {
                    //TODO: show registration failed text
                }
            });
    }

    public render() {
        if (this.state.loggedIn) {
            return <Redirect to="/" />;
        }
        return (
            <div className="container centered-container">
                <div className="box-header">
                    Sign Up
                </div>
                <div className="box-content">
                    <div>
                        <div className="messageLabel">Username</div>
                        <input className="textarea" placeholder="Enter Username" id="username" onChange={this.formInputFieldUpdated.bind(this)} value={this.state.username} />

                        <div className="messageLabel">Email</div>
                        <input className="textarea" placeholder="Enter Email" id="email" onChange={this.formInputFieldUpdated.bind(this)} value={this.state.email} />

                        <div className="messageLabel">Password</div>
                        <input type="password" className="textarea" placeholder="Enter Password" id="password" onChange={this.formInputFieldUpdated.bind(this)} value={this.state.password} />
                    </div>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.signUp.bind(this)}
                    >
                        Sign Up &gt;
                    </button>
                </div>
            </div>
        );
    }
}