import React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { LoginManager } from "./LoginManager";
import { Redirect } from "react-router-dom";
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";

export interface RegistrationProps {
    eventBus: EventBus
    loginManager: LoginManager
}
interface RegistrationCredentials {
    username: string
    password: string
    email: string
    loggedIn?: boolean
}

/** Registration Page. The user can register here or go back to the Login Page. */
export class Registration extends React.Component<RegistrationProps, RegistrationCredentials> {
    constructor(props: RegistrationProps) {
        super(props);
        this.state = { username: "", password: "", email: "", loggedIn: props.loginManager.isLoggedIn() };
        props.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
    }

    /** Called when the logged in user changed. */
    private onUserChange(eventData?: any) {
        this.setState({
            username: this.state.username,
            password: this.state.password,
            email: this.state.email,
            loggedIn: eventData.loggedIn
        });
    }

    /** Called when the user enters a username, password or email address. Requests an update of the internal state.*/
    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    /** Update the internal state after the user entered a username, password or email address. */
    private update(id: string, value: string) {
        if (id === "username") {
            this.setState({
                username: value,
                password: this.state.password,
                email: this.state.email,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "password") {
            this.setState({
                username: this.state.username,
                password: value,
                email: this.state.email,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "email") {
            this.setState({
                username: this.state.username,
                password: this.state.password,
                email: value,
                loggedIn: this.state.loggedIn
            });
        }
    }

    /**Create a new user in the backend. */
    private signUp() {
        fetch(`${DeploymentManager.getUrl()}api/register`, {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json'
            }),
            body: JSON.stringify({ displayName: this.state.username, email: this.state.email, password: this.state.password })
        })
            .then(response => {
                if (response.ok) {
                    this.props.loginManager.logIn(this.state.username, this.state.password);
                } else {
                    response.json().then(e => ToastManager.showErrorToast(e.error));
                }
            });
    }

    public render() {
        //redirect to main page if the user is already logged in.
        if (this.state.loggedIn) {
            return <Redirect to="/overview" />;
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
                    <span>Already have an account? Click <a href="/login">here</a> to sign in.</span>
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
