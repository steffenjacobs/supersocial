import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";

export interface LoginStatus {
    loggedIn: boolean
    username: string
    config: UserConfiguration[]
}

export interface UserConfiguration {
    descriptor: string
    value: string
}

/** The LoginManager class contains all the required logic to perform logins and check and update the login status of the current user. */

//TODO: Logout
export class LoginManager {
    eventBus: EventBus;
    loginStatus: LoginStatus = {
        loggedIn: false,
        username: "Not logged in",
        config: []
    };

    constructor(eventBus: EventBus) {
        this.eventBus = eventBus;
    }

    /** @return true if the user is currently logged in. If not, the cookie is checked and a USER_CHANGE event is fired later. */
    public isLoggedIn() {
        if (!this.loginStatus.loggedIn) {
            //attempt login with cookie
            this.logIn();
            return false;
        }
        return this.loginStatus.loggedIn;
    }

    /** Update the login status of the user to log in or log out.*/
    private updateLoginStatus(newLoginStatus: LoginStatus) {
        this.loginStatus = { loggedIn: newLoginStatus.loggedIn, username: newLoginStatus.username, config: newLoginStatus.config };
        this.eventBus.fireEvent(EventBusEventType.USER_CHANGE, this.loginStatus);
    }

    /** Login with username and password. If both are undefined, login just with cookie is attempted. */
    public logIn(username?: string, password?: string) {
        if (username && password) {
            //login if credentials provided
            let fd = new FormData();
            fd.set('username', username);
            fd.set('password', password);

            fetch(`${DeploymentManager.getUrl()}api/perform_login`, {
                method: 'post',
                credentials: 'include',
                body: fd
            })
                .then(response => {
                    if (response.ok) {
                        response.json().then(data => {
                            if (data.username) {
                                this.updateLoginStatus({ loggedIn: true, username: data.username, config: data.config });
                            } else {
                                this.fetchLoginStatus();
                            }
                        });
                    } else {
                        this.updateLoginStatus({ loggedIn: false, username: "Not logged in", config: [] });
                    }
                });
        } else {
            //attempt login with cookie only
            this.fetchLoginStatus();
        }
    }

    /** fetch the current login status async into this.state.loginStatus using the session cookie. */
    private fetchLoginStatus() {
        fetch(`${DeploymentManager.getUrl()}api/loginstatus`, {
            method: 'get',
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        if (data.username) {
                            this.updateLoginStatus({ loggedIn: true, username: data.username, config: data.config });
                        }
                    });
                } else {
                    this.updateLoginStatus({ loggedIn: false, username: "Not logged in", config: [] });
                }
            });
    }

    /** Log out the current user. */
    public logOut() {
        fetch(`${DeploymentManager.getUrl()}api/perform_logout`, {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        this.updateLoginStatus({ loggedIn: false, username: "Not logged in", config: [] });
                    });
                } else {
                    this.updateLoginStatus({ loggedIn: false, username: "Not logged in", config: [] });
                }
            });
    }

    /** @return the loginStatus object containing a username and a loggedIn value */
    public getLoginStatus(): LoginStatus {
        if (!this.loginStatus.loggedIn) {
            this.logIn();
        }
        return { username: this.loginStatus.username, loggedIn: this.loginStatus.loggedIn, config: this.loginStatus.config };
    }
}