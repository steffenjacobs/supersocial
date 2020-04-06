import React from 'react';
import Supersocial from './Supersocial';
import { Login } from './Login';
import { Route, Switch, Redirect, BrowserRouter } from 'react-router-dom';
import { EventBus, EventBusEventType } from './EventBus';
import { LoginManager } from './LoginManager';
import { Registration } from './Registration';

export interface LoginInfo {
  loggedIn?: boolean
  username?: string
}

/** Main entry point for the application. The EventBus and the LoginManager live on this layer and are passed downwards from here. */
class App extends React.Component<LoginInfo, LoginInfo> {
  eventBus = new EventBus();
  loginManager = new LoginManager(this.eventBus);

  constructor(props) {
    super(props);
    this.state = {
      loggedIn: false,
      username: "Not logged in"
    };
    this.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));

  }

  private onUserChange(eventData?: any) {
    this.setState(eventData);
  }

  render() {
    // /login -> go to Login Page
    // everything else -> Go to Supersocial application
    return (
      <BrowserRouter>
        <Switch>  
          <Route path="/register" render={(props) => <Registration username="" password="" email="" eventBus={this.eventBus} loginManager={this.loginManager} />} />        
          <Route path="/login" render={(props) => <Login username="" password="" eventBus={this.eventBus} loginManager={this.loginManager} />} />
          {this.state.loggedIn ? <Route path="/" render={(props) => <Supersocial eventBus={this.eventBus} loginManager={this.loginManager} />} /> : <Redirect to="/login" />} />}
        </Switch>
      </BrowserRouter>
    );
  }
}
export default App;
