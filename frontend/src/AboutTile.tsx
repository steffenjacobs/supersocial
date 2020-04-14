import React from "react";
import { ImageProvider } from "./ImageProvider";

export interface ProjectMemberProps {
    name: string
    role: string
    description: string
    imageUrl: string
    twitterHandle?: string
    facebookHandle?: string
    linkedinHandle?: string
    xingHandle?: string
    githubHandle?: string
}

export class AboutTile extends React.Component<ProjectMemberProps>{

    public render() {
        return (
            <div className="about-entry">
                <h2>{this.props.name} - {this.props.role}</h2>
                <img className="about-entry-image" src={this.props.imageUrl}></img>
                <div className="about-entry-left">
                    <div className="about-entry-left-inner">
                        <div className="about-entry-description">{this.props.description}</div>
                        <div >
                            {(this.props.twitterHandle || this.props.facebookHandle || this.props.linkedinHandle || this.props.xingHandle || this.props.githubHandle) && <span className="about-entry-socialmediatext">Follow me on social media:</span>}
                            {this.props.twitterHandle && <a target="_blank" rel="noopener noreferrer" className="about-entry-socialmedia-icon" href={"https://twitter.com/" + this.props.twitterHandle}>{ImageProvider.getSocialmediaIcon(2)}</a>}
                            {this.props.facebookHandle && <a target="_blank" rel="noopener noreferrer" className="about-entry-socialmedia-icon" href={"https://facebook.com/" + this.props.facebookHandle}>{ImageProvider.getSocialmediaIcon(1)}</a>}
                            {this.props.linkedinHandle && <a target="_blank" rel="noopener noreferrer" className="about-entry-socialmedia-icon" href={"https://www.linkedin.com/in/" + this.props.linkedinHandle}>{ImageProvider.getSocialmediaIcon(3)}</a>}
                            {this.props.xingHandle && <a target="_blank" rel="noopener noreferrer" className="about-entry-socialmedia-icon" href={"https://www.xing.com/profile/" + this.props.xingHandle}>{ImageProvider.getSocialmediaIcon(4)}</a>}
                            {this.props.githubHandle && <a target="_blank" rel="noopener noreferrer" className="about-entry-socialmedia-icon" href={"https://github.com/" + this.props.githubHandle}>{ImageProvider.getSocialmediaIcon(1001)}</a>}
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}