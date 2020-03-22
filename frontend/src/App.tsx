import React, { Component } from 'react';
import Supersocial from './Supersocial';
import { Login } from './Login';
import { Route, Switch, Redirect, BrowserRouter } from 'react-router-dom';
import { EventBus, EventBusEventType } from './EventBus';
import { LoginManager, LoginStatus } from './LoginManager';

export interface LoginInfo{
  loggedIn?: boolean
  username?: string
}

class App extends React.Component<LoginInfo, LoginInfo> {
  constructor() {
    super({
      loggedIn: false,
      username: "Not logged in"
    });
    this.state = {
      loggedIn: false,
      username: "Not logged in"
    };
  }

  private onUserChange(eventData?: any) {
    this.setState(eventData);
  }

  private async performLoginCheckAndReturnResult(redirectTo: any) {
    const response = await fetch('http://localhost:8080/api/loginstatus', {
      method: 'GET',
      headers: new Headers({
        // 'Authorization': 'Basic ' + btoa('user:pass')
      })
    });
    if (response.ok) {
      return <Route path="/" component={redirectTo} />;
    } else {
      return <Redirect to="/login" />;
    }
  }

  render() {
    let eventBus = new EventBus();
    let loginManager = new LoginManager(eventBus);
    eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));

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
