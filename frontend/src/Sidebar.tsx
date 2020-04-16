import React from "react";
import './Sidebar.css';
import { LoginManager } from "./LoginManager";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export interface PageComponents {
    components: PageComponent[]
    loginManager: LoginManager
    selected: number
}

export interface PageComponent {
    id: number
    title: string
    page: JSX.Element
    icon: JSX.Element
    path: string
    selected?: boolean
}

/** Menu component which holds the different pages and makes them accesible. */
export class Sidebar extends React.Component<PageComponents, PageComponents>{
    constructor(props: PageComponents, state: PageComponents) {
        super(props);
        this.state = { components: props.components, loginManager: props.loginManager, selected: props.selected };
        this.setActivePage(this.state.selected, true);
    }

    /** Set this page active. Remove the old page and select the new one to be rendered. */
    private setActivePage(pageId: number, notMounted?: boolean) {
        var newComponents: PageComponent[] = [];
        for (let c = 0; c < this.state.components.length; c++) {
            let comp = this.state.components[c];
            if (comp.selected && comp.id !== pageId) {
                newComponents.push({ id: comp.id, title: comp.title, page: comp.page, icon: comp.icon, path: comp.path })
            }
            else if (comp.id === pageId) {
                newComponents.push({ id: comp.id, title: comp.title, page: comp.page, icon: comp.icon, selected: true, path: comp.path })
            } else {
                newComponents.push(comp);
            }
        }

        if (notMounted) {
            this.state = { components: newComponents, loginManager: this.state.loginManager, selected: pageId };
        } else {
            this.setState({ components: newComponents, selected: pageId });
        }
        //TODO: set title and url in browser window
    }

    /** @return the header component with the titleo f the page. */
    private getTitleComponent(elem?: PageComponent) {
        return (<div>
            {elem != null && <>
                {elem?.icon}
                <div className="navbar-text">{elem?.title}</div>
            </>}
        </div>);
    }

    /**Log out the user. */
    private performLogout() {
        this.state.loginManager.logOut();
    }

    public render() {
        //Create the menu items
        const components = this.state.components.map((elem) => {
            const clazz = elem.selected ? 'navbar-menuItem navbar-menuItem-active' : 'navbar-menuItem';
            return (
                <a key={elem.id} href={elem.path}>
                    <div className={clazz}>
                        {elem.icon}
                        <div className="navbar-text">{elem.title}</div>
                    </div>
                </a>
            );
        });

        const selectedComponent = this.state.components.find(c => c.selected);
        //1. create the menu
        //2. create the header
        //3. add the page
        return (
            <div>
                <ToastContainer position={toast.POSITION.TOP_RIGHT} autoClose={5500} />
                <div className="navbar">
                    <div className="navbar-logo">
                        <a href="/"><img src="/logo192.png" alt="Logo" /></a>
                        <div className="navbar-brandname"> Supersocial</div>
                    </div>
                    {components}
                </div>
                <div className="navbar-header">
                    <div className="navbar-header-left">
                        {this.getTitleComponent(selectedComponent)}
                    </div>
                    <div onClick={this.performLogout.bind(this)} className="navbar-header-userdetails">
                        Hello {this.state.loginManager.getLoginStatus().username}!
                    </div>
                </div>
                <div className="navbar-page">{selectedComponent?.page}</div>
            </div>
        );
    }
}