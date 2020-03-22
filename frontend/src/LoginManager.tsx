import { EventBus, EventBusEventType } from "./EventBus";
import Cookies from "js-cookie";

export interface LoginStatus {
    loggedIn: boolean
    username: string
}

export class LoginManager {
    eventBus: EventBus;
    loginStatus = {
        loggedIn: false,
        username: "Not logged in"
    };

    constructor(eventBus: EventBus) {
        this.eventBus = eventBus;
    }

    public isLoggedIn() {
        if (!this.loginStatus.loggedIn) {
            this.logInWithCookie();
            return false;
        }
        return this.loginStatus.loggedIn;
    }

    public logInWithCookie() {
        fetch('http://localhost:8080/api/loginstatus', {
            method: 'get',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        this.updateLoginStatus({ loggedIn: true, username: data.name });
                    });
                } else {
                    this.updateLoginStatus({ loggedIn: false, username: "Not logged in" });
                }
            });
    }

    private updateLoginStatus(newLoginStatus: LoginStatus) {
        this.loginStatus = { loggedIn: newLoginStatus.loggedIn, username: newLoginStatus.username };
        this.eventBus.fireEvent(EventBusEventType.USER_CHANGE, this.loginStatus);
    }

    public logIn(username: string, password: string) {
        fetch('http://localhost:8080/api/loginstatus', {
            method: 'get',
            credentials: 'include',
            headers: new Headers({
                'Authorization': 'Basic ' + btoa(username + ":" + password)
            })
        })
            .then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        this.updateLoginStatus({ loggedIn: true, username: data.name });
                    });
                } else {
                    this.updateLoginStatus({ loggedIn: false, username: "Not logged in" });
                }
            });
    }

    public getLoginStatus() {
        if (!this.loginStatus.loggedIn) {
            this.logInWithCookie();
        }
        return { username: this.loginStatus.username, loggedIn: this.loginStatus.loggedIn };
    }
}