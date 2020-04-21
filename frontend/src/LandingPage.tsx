import React from "react";
import './LandingPage.css';
import './UiElements.css';


export class LandingPage extends React.Component<any, any>{

    public render() {
        return <div className="landing-site">
            <div className="landing-site-inner">
                <h1 className="landing-h1">Welcome to Supersocial</h1>
                <h2 className="landing-h2">Social Media Marketing on a new level.</h2>
                <div className="landing-box landing-flex">
                    <img alt="Logo" className="landing-header-elem" src="logo512.png" />
                    <div className="landing-header-elem landing-btn-wrapper">
                        <div className="landing-header-text">Welcome to a new world of data-driven and AI-based social media marketing.</div>
                        <div className="landing-flex-center">
                            <button onClick={e => window.location.href="/register"} className="btn btn-primary landing-btn-header">Explore now &nbsp;&#9654;</button>
                        </div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Activate your data </h3>
                    <h4 className="landing-h4">Leverage the potential of the data already collector for you.</h4>
                    <div>
                        <div className="landing-img-container"><img alt="Activate your data" src="frontpage/activate-your-data.jpg" /></div>
                        <div className="landing-txt-container">Supersocial can help to activate existing data to leverage your hidden marketing potential right away, drive engagement and expand your business or organization.</div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Make the right choices based on solid data</h3>
                    <h4 className="landing-h4">Don't read the tea-leaves - Use real data to optimize conversion rates.</h4>
                    <div>
                        <div className="landing-txt-container">Solid data helps your team to get insights about your customer and better understand their needs. Supersocial provides all the data you will need ASAP to make the right decisions. Closely monitor how your choices improve brand identity and recognition.</div>
                        <div className="landing-img-container"><img alt="Make the right choices" src="frontpage/make-the-right-choices.jpg" /></div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Gain new insights from your customer's perspective</h3>
                    <h4 className="landing-h4">Use our AI-based sentiment analysis system to visualize the customer's reaction to your efforts in real time.</h4>
                    <div>
                        <div className="landing-img-container"><img alt="Gain new insides" src="frontpage/gain-new-insides.jpg" /></div>
                        <div className="landing-txt-container">Our advanced interactive data-driven analytics dashboards help you to understand how your customers <b>really</b> thinks about you. Supersocial helps to channel and identify key issues to get you the feedback you need, anticipate customer needs, deepen customer relationships and improve the whole experience for your customers. Because customers tell you everything you need to know <i>all the time.</i></div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Give away control without giving away control</h3>
                    <h4 className="landing-h4">Make sure your team has exactly the tools it needs to maximize exposure.</h4>
                    <div>
                        <div className="landing-txt-container">Supersocial supports the way things are done: Unify access management for your social media assets. Stay in control of your most important marketing assets without losing the agility you need.</div>
                        <div className="landing-img-container"><img alt="Give away control" src="frontpage/give-away-control.jpg" /></div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Automate your marketing</h3>
                    <h4 className="landing-h4">Schedule content and geofence your efforts to be even more successful.</h4>
                    <div>
                        <div className="landing-img-container"><img alt="Automate your marketing" src="frontpage/automate-your-marketing.JPG" /></div>
                        <div className="landing-txt-container">Perfect your market timing and schedule your content to go viral exactly at the right time in the pricely the right market.</div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Realtime data streams </h3>
                    <h4 className="landing-h4">Live data streams will help you to react quickly in a changing environment</h4>
                    <div>
                        <div className="landing-txt-container">Supersocial fetches all data necessary for a profund analysis directly from the social network your customers use.</div>
                        <div className="landing-img-container"><img alt="Realtime data streams" src="frontpage/realtime-data-streams.jpg" /></div>
                    </div>
                </div>

                <div className="landing-box">
                    <h3 className="landing-h3">Intelligible Visualization</h3>
                    <h4 className="landing-h4">Our dashboards give your team all the information they need at a glance.</h4>
                    <div>
                        <div className="landing-img-container"><img alt="Intelligible Visualization" src="frontpage/intelligible-visualization.jpg" /></div>
                        <div className="landing-txt-container">Monitor your most important KPIs and their development over time to spot trends easily. Get feedback promptly as it happens via push notification and never miss your post going viral.</div>
                    </div>

                </div>
            </div >
        </div>
    }
}