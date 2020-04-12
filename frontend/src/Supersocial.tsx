import React from 'react';
import { Sidebar } from './Sidebar';
import { AboutPage } from './AboutPage';
import { MessageOverview } from './MessageOverview';
import { EventBus } from './EventBus';
import { LoginManager } from './LoginManager';
import { ImageProvider } from './ImageProvider';
import { SettingsPage } from './SettingsPage';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import { AnalyticsPage } from './AnalyticsPage';

export interface SupersocialProps {
    eventBus: EventBus
    loginManager: LoginManager
}

/** This class holds the different pages that are later rendered in the side bar.  */
export class Supersocial extends React.Component<SupersocialProps> {

    /**Change the selected page. */
    private selectComponent(components, selected: number) {
        return (<div className="App">
            <Sidebar components={components} loginManager={this.props.loginManager} selected={selected} />
        </div>);
    }

    public render() {
        const components = [{
            title: 'Message Overview',
            page: <MessageOverview eventBus={this.props.eventBus} loginManager={this.props.loginManager} />,
            selected: true,
            id: 0,
            icon: ImageProvider.getImage("home-icon"),
            path: "/overview"
        }, {
            id: 1,
            title: 'Analytics',
            page: <AnalyticsPage eventBus={this.props.eventBus} />,
            icon: ImageProvider.getImage("analytics"),
            path: "/analytics"
        }, {
            id: 2,
            title: 'Settings',
            page: <SettingsPage eventBus={this.props.eventBus} />,
            icon: ImageProvider.getImage("settings-icon"),
            path: "/settings"
        }, {
            id: 3,
            title: 'About',
            page: <AboutPage />,
            icon: ImageProvider.getImage("info-icon"),
            path: "/about"
        }];

        const routes = components.forEach(c => {
            return <Route path={c.path} render={(props) => this.selectComponent(components, c.id)} />;
        });

        return (
            <BrowserRouter>
                <Switch>
                    <Route path="/" render={(props) => this.selectComponent(components, 0)} />
                    {routes}
                </Switch>
            </BrowserRouter>);
    }
}
export default Supersocial;