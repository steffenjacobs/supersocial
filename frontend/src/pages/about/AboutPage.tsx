import React from "react";
import { AboutTile } from "./AboutTile";
import './AboutTile.css';
import { DeploymentManager } from "../../misc/DeploymentManager";
import { ToastManager } from "../../misc/ToastManager";

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
        fetch(`${DeploymentManager.getUrl()}api/version`, {
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
                <AboutTile name="Steffen Jacobs" role="Product Owner and Lead Developer" tiktokHandle="steffen.jacobs" instagramHandle="steffen16378" twitterHandle="Steffen_Jacobs_" facebookHandle="steffen.jacobs.796" linkedinHandle="steffen-jacobs" xingHandle="Steffen_Jacobs7" githubHandle="steffenjacobs" description="Hi, I'm Steffen. I am a consultant and software engineer and work on all sorts of interesting private and professional entrepreneurial projects. I love to work with likeminded individuals who have a faible for recent technologies and learning new stuff. If you'd like to work with us and have the required entrepreneurial sprit, feel free to message me." imageUrl="team/steffen.JPG" />
                <AboutTile name="Sebastian Hädrich" role="Frontend Developer" description="This is me!" imageUrl="team/placeholder.svg" />
                <AboutTile name="Thomas Kruse" role="Developer" description="This is me!" imageUrl="team/placeholder.svg" />
                <button onClick={e=>window.location.href="/privacy"} className="btn btn-primary landing-btn-header">Data Privacy &nbsp;&#9654;</button>
                <div className="about-version">Product version: Supersocial v{this.state.version}</div>
            </div>
        );
    } 
}