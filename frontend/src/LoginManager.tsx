import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";

export interface LoginStatus {
    loggedIn: boolean
    username: string
}

/** The LoginManager class contains all the required logic to perform logins and check and update the login status of the current user. */

//TODO: Logout
export class LoginManager {
    eventBus: EventBus;
    loginStatus = {
        loggedIn: false,
        username: "Not logged in"
    };

    constructor(eventBus: EventBus) {
        this.eventBus = eventBus;
    }

    /** @return true if the user is currently logged in. If not, the cookie is checked and a USER_CHANGE event is fired later. */
    public isLoggedIn() {
        if (!this.loginStatus.loggedIn) {
            //attempt login with cookie
            this.logIn(undefined, undefined);
            return false;
        }
        return this.loginStatus.loggedIn;
    }

    private updateLoginStatus(newLoginStatus: LoginStatus) {
        this.loginStatus = { loggedIn: newLoginStatus.loggedIn, username: newLoginStatus.username };
        this.eventBus.fireEvent(EventBusEventType.USER_CHANGE, this.loginStatus);
    }

    /** Login with username and password. If both are undefined, login just with cookie is attempted. */
    public logIn(username?: string, password?: string) {
        let dynamicHeaders;
        if (username && password) {
            dynamicHeaders = new Headers({
                'Authorization': 'Basic ' + btoa(username + ":" + password)
            });
        }
        else {
            dynamicHeaders = new Headers({});
        }
        fetch(DeploymentManager.getUrl() + 'api/loginstatus', {
            method: 'get',
            credentials: 'include',
            headers: dynamicHeaders
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

    /** @return the loginStatus object containing a username and a loggedIn value */
    public getLoginStatus() {
        if (!this.loginStatus.loggedIn) {
            this.logIn(undefined, undefined);
        }
        return { username: this.loginStatus.username, loggedIn: this.loginStatus.loggedIn };
    }
}