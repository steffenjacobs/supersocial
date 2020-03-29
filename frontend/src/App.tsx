import React from 'react';
import Supersocial from './Supersocial';
import { Login } from './Login';
import { Route, Switch, Redirect, BrowserRouter } from 'react-router-dom';
import { EventBus, EventBusEventType } from './EventBus';
import { LoginManager } from './LoginManager';

export interface LoginInfo {
  loggedIn?: boolean
  username?: string
}

/** Main entry point for the application. The EventBus and the LoginManager live on this layer and are passed downwards from here. */
class App extends React.Component<LoginInfo, LoginInfo> {
  constructor(props) {
    super(props);
    this.state = {
      loggedIn: false,
      username: "Not logged in"
    };
  }

  private onUserChange(eventData?: any) {
    this.setState(eventData);
  }

  render() {
    let eventBus = new EventBus();
    let loginManager = new LoginManager(eventBus);
    eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));

    // /login -> go to Login Page
    // everything else -> Go to Supersocial application
    return (
      <BrowserRouter>
        <Switch>          
          <Route path="/login" render={(props) => <Login username="" password="" eventBus={eventBus} loginManager={loginManager} />} />
          {this.state.loggedIn ? <Route path="/" render={(props) => <Supersocial eventBus={eventBus} loginManager={loginManager} />} /> : <Redirect to="/login" />} />}
        </Switch>
      </BrowserRouter>
    );
  }
}
export default App;
