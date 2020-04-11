import React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { LoginManager } from "./LoginManager";
import { Redirect } from "react-router-dom";

export interface LoginProps {
    eventBus: EventBus
    loginManager: LoginManager
}
interface LoginCredentials {
    username: string
    password: string
    loggedIn?: boolean
}

/** Login Page. The user can login here or go to the Registration page to create a new user account. 
 * Consult the LoginManger.tsx for further login logic. */
export class Login extends React.Component<LoginProps, LoginCredentials> {
    constructor(props: LoginProps) {
        super(props);
        this.state = { username: "", password: "", loggedIn: props.loginManager.isLoggedIn() };
        props.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
    }

    /** Called when the logged in user changed. */
    private onUserChange(eventData?: any) {
        this.setState({
            username: this.state.username, password: this.state.password,
            loggedIn: eventData.loggedIn
        });
    }

    /** Called when the user enters a username or password. */
    private formInputFieldUpdated(event: React.ChangeEvent<HTMLInputElement>) {
        this.update(event.currentTarget.id, event.currentTarget.value);
    }

    /**Update the internal state with thew new username or password from the input field. */
    private update(id: string, value: string) {
        if (id === "username") {
            this.setState({
                username: value,
                password: this.state.password,
                loggedIn: this.state.loggedIn
            });
        }
        if (id === "password") {
            this.setState({
                username: this.state.username,
                password: value,
                loggedIn: this.state.loggedIn
            });
        }
    }

    /**Sign the user in. */
    private signIn() {
        this.props.loginManager.logIn(this.state.username, this.state.password);
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
                        <input type="password" className="textarea" placeholder="Enter Password" id="password" onKeyDown={this.signIn.bind(this)} onChange={this.formInputFieldUpdated.bind(this)} value={this.state.password} />
                    </div>
                    <span>Not registered yet? Click <a href="/register">here</a> to sign up.</span>
                    <button
                        className="btn btn-primary send-button"
                        onClick={this.signIn.bind(this)}
                    >
                        Sign In &gt;
                    </button>
                </div>
            </div>
        );
    }
}
