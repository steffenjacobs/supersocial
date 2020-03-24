import React from 'react';
import { Sidebar } from './Sidebar';
import { AboutPage } from './AboutPage';
import { MessageOverview } from './MessageOverview';
import { EventBus } from './EventBus';
import { LoginManager } from './LoginManager';
import { ImageProvider } from './ImageProvider';

export interface EventBusParams {
    eventBus: EventBus
    loginManager: LoginManager
}

/** This class holds the different pages that are later rendered in the side bar.  */
export class Supersocial extends React.Component<EventBusParams> {

    public render() {
        const components = [{
            title: 'Message Overview',
            page: <MessageOverview eventBus={this.props.eventBus} />,
            selected: true,
            id: 0,
            icon: ImageProvider.getImage("home-icon")
        }, {
            id: 1,
            title: 'About',
            page: <AboutPage />,
            icon: ImageProvider.getImage("info-icon")
        }];

        return (
            <div className="App">
                <Sidebar components={components} loginManager={this.props.loginManager} />
            </div>);
    }
}
export default Supersocial;