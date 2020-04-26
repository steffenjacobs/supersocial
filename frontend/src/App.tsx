import React from 'react';
import { Login } from './Login';
import { Route, Switch, Redirect, BrowserRouter } from 'react-router-dom';
import { EventBus, EventBusEventType } from './EventBus';
import { LoginManager } from './LoginManager';
import { Registration } from './Registration';
import { Sidebar, PageComponent } from './Sidebar';
import { MessageOverview } from './MessageOverview';
import { ImageProvider } from './ImageProvider';
import { AnalyticsPage } from './AnalyticsPage';
import { SettingsPage } from './SettingsPage';
import { AboutPage } from './AboutPage';
import { LandingPage } from './LandingPage';
import { DataPrivacyPage } from './DataPrivacyPage';

/** Main entry point for the application. The EventBus and the LoginManager live on this layer and are passed downwards from here. */
class App extends React.Component<any> {
  eventBus = new EventBus();
  loginManager = new LoginManager(this.eventBus);

  constructor(props: any) {
    super(props);
    this.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
  }

  private onUserChange(eventData?: any) {
    this.setState(eventData);
  }



  /**Change the selected page. */
  private selectComponent(components, selected: PageComponent) {
    return (<div className="App">
      <Sidebar components={components} loginManager={this.loginManager} selected={selected} />
    </div>);
  }

  render() {

    const components = [{
      title: 'Message Overview',
      page: <MessageOverview eventBus={this.eventBus} loginManager={this.loginManager} />,
      selected: true,
      id: 0,
      icon: ImageProvider.getImage("home"),
      path: "/overview"
    }, {
      id: 1,
      title: 'Analytics',
      page: <AnalyticsPage eventBus={this.eventBus} />,
      icon: ImageProvider.getImage("analytics"),
      path: "/analytics"
    }, {
      id: 2,
      title: 'Settings',
      page: <SettingsPage eventBus={this.eventBus} loginManager={this.loginManager} />,
      icon: ImageProvider.getImage("settings"),
      path: "/settings"
    }, {
      id: 3,
      title: 'About',
      page: <AboutPage />,
      icon: ImageProvider.getImage("info"),
      path: "/about"
    }];

    // /login -> go to Login Page
    // everything else -> Go to Supersocial application
    return (
      <BrowserRouter>
        <Switch>
          <Route path="/register" render={(props) => <Registration eventBus={this.eventBus} loginManager={this.loginManager} />} />
          <Route path="/login" render={(props) => <Login eventBus={this.eventBus} loginManager={this.loginManager} params={props.location.search} />} />
          {components.map((component, idx) => <Route path={component.path} render={(props) => this.loginManager.isLoggedIn() ? this.selectComponent(components, component) : <Redirect to={"/login?redirect=" + encodeURIComponent(component.path)} />} />)}
          <Route path="/privacy" render={(props) => <DataPrivacyPage />} />
          <Route path="/" render={(props) => <LandingPage />} />
        </Switch>
      </BrowserRouter>
    );
  }
}
export default App;
