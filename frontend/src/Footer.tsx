import React from "react";
import './Footer.css';

export class Footer extends React.Component<any>{
    public render() {
        return <div className="footer">
            <span><a href="/">Home</a></span>&nbsp;|&nbsp;
            <span><a href="/overview">Overview</a></span>&nbsp;|&nbsp;
            <span><a target="blank" href="https://confluence.supersocial.cloud/display/SP/Changelog">What's new</a></span>&nbsp;|&nbsp;
            <span><a target="blank" href="https://confluence.supersocial.cloud/display/SP/User+Guide">User Guide</a></span>&nbsp;|&nbsp;
            <span><a href="/privacy">Data Privacy and Contact</a></span>&nbsp;|&nbsp;
            <span><a href="/about">About Us</a></span>
        </div>;
    }
}