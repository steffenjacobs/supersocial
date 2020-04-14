import React from "react";
import { AboutTile } from "./AboutTile";
import './AboutTile.css';
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";

interface AboutState {
    version: string
}

export class AboutPage extends React.Component<any, AboutState> {
    constructor(props: any) {
        super(props);
        this.state = { version: "unknown" };
        this.fetchVersionInfo();
    }


    private fetchVersionInfo() {
        fetch(DeploymentManager.getUrl() + 'api/version', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(json => this.setState({ version: json.version }));
                }
            });
    }

    public render() {
        return (
            <div className="about-container">
                <h1>Supersocial by </h1>
                <AboutTile name="Steffen Jacobs" role="Project Manager and Lead Developer" twitterHandle="Steffen_Jacobs_" facebookHandle="steffen.jacobs.796" linkedinHandle="steffen-jacobs" xingHandle="Steffen_Jacobs7" githubHandle="steffenjacobs" description="Hi, I'm Steffen. I am a consultant and software engineer and work on all sorts of interesting private and professional projects. I lead this project and also contributed most of the initial source code. I love to work with recent technologies and learn new stuff." imageUrl="team/steffen.JPG" />
                <AboutTile name="Sebastian Hädrich" role="Frontend Developer" description="This is me!" imageUrl="team/placeholder.svg" />
                <AboutTile name="Thomas Kruse" role="Developer" description="This is me!" imageUrl="team/placeholder.svg" />
                <div>Product version: {this.state.version}</div>
            </div>
        );
    }
}