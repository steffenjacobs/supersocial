import React from "react";
import './Sidebar.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { LoginManager } from "./LoginManager";

export interface PageComponents {
    components: PageComponent[]
    eventBus: EventBus
    loginManager: LoginManager
}

export interface PageComponent {
    id: number
    title: string
    page: JSX.Element
    icon: JSX.Element
    selected?: boolean
}

export class Sidebar extends React.Component<PageComponents, PageComponents>{
    constructor(props: PageComponents, state: PageComponents) {
        super(props);
        this.state = props;

        this.state.eventBus.register(EventBusEventType.USER_CHANGE, (eventType, eventData?) => this.onUserChange(eventData));
    }

    private onUserChange(eventData?: any) {
        //TODO: validate that this is necessary
        this.setState({ components: this.state.components, eventBus: this.state.eventBus, loginManager: this.state.loginManager });
    }

    private setActivePage(pageId: number) {
        var newComponents = [];
        for (let c = 0; c < this.state.components.length; c++) {
            let comp = this.state.components[c];
            if (comp.selected && comp.id !== pageId) {
                newComponents.push({ id: comp.id, title: comp.title, page: comp.page, icon: comp.icon })
            }
            else if (comp.id === pageId) {
                newComponents.push({ id: comp.id, title: comp.title, page: comp.page, icon: comp.icon, selected: true })
            } else {
                newComponents.push(comp);
            }
        }

        this.setState({ components: newComponents });
        //TODO: set title and url
    }


    private getTitleComponent(elem?: PageComponent) {
        if (elem) {

            return (
                <div>
                    {elem?.icon}
                    <div className="navbar-text">{elem?.title}</div>
                </div>
            );
        } else {
            return <div />;
        }
    }

    public render() {
        const components = this.state.components.map((elem) => {
            const clazz = elem.selected ? 'navbar-menuItem navbar-menuItem-active' : 'navbar-menuItem';
            return (
                <a onClick={() => this.setActivePage(elem.id)}>
                    <div className={clazz}>
                        {elem.icon}
                        <div className="navbar-text">{elem.title}</div>
                    </div>
                </a>
            );
        });

        const selectedComponent = this.state.components.find((c) => { return c.selected });

        return (
            <div>
                <div className="navbar">
                    <div className="navbar-logo">
                        <img src="/logo192.png" alt="Logo" />
                        <div className="navbar-brandname"> Supersocial</div>
                    </div>
                    {components}
                </div>
                <div className="navbar-header">
                    <div className="navbar-header-left">
                        {this.getTitleComponent(selectedComponent)}
                    </div>
                    <div className="navbar-header-userdetails">
                        Hello {this.state.loginManager.getLoginStatus().username}!
                    </div>
                </div>
                <div className="navbar-page">{selectedComponent?.page}</div>
            </div>
        );
    }
}