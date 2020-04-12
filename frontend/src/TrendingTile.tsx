import * as React from "react";
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";
import { ToastManager } from "./ToastManager";
import { LoginManager } from "./LoginManager";
import { UserConfigurationDecoder } from "./UserConfigurationDecoder";
import { SnippetManager } from "./SnippetManager";

export interface TrendingTileState {
    updating?: boolean
    topics: TrendingTopic[]
}
export interface TrendingTileProps {
    eventBus: EventBus
    loginManager: LoginManager
}

export interface TrendingTopic {
    name: string
    tweet_volume: number
}

/** Shows the most recent twitter trends. */
export class TrendingTile extends React.Component<TrendingTileProps, TrendingTileState>{
    constructor(props: TrendingTileProps) {
        super(props);
        this.state = { updating: false, topics: [] };
        this.refreshTrendingTopics(UserConfigurationDecoder.decodeLocation(props.loginManager).woeid);
        props.eventBus.register(EventBusEventType.USER_CHANGE, (e, ld) => this.refreshTrendingTopics(UserConfigurationDecoder.decodeLocationFromLoginStatus(ld).woeid));
    }

    /** Fetches the most recent twitter trends from the backend. */
    private refreshTrendingTopics(woeid: any) {
        this.setState({ updating: true });
        fetch(DeploymentManager.getUrl() + 'api/analytics/trending/' + woeid, {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => this.setState({
                        updating: false,
                        topics: data[0]._source.trends
                    }));
                }
            });
    }

    /** Create a link to the given twitter topic */
    private getTrendUrl(topic: TrendingTopic) {
        return "https://twitter.com/search?q=" + encodeURIComponent(topic.name);
    }

    public render() {
        //enumeration with topics
        const topics = this.state.topics.sort(
            (t1: TrendingTopic, t2: TrendingTopic) => t2.tweet_volume - t1.tweet_volume)
            .slice(0, 10).map(elem => {
                return <li><a target="_blank" href={this.getTrendUrl(elem)}>{elem.name}</a></li>;
            });

        //animation for refresh button
        let classUpdating = ["inline-block", "btn-icon", "btn-small", "header-icon-margin-left"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        const locationName = UserConfigurationDecoder.decodeLocation(this.props.loginManager).name;

        return (
            <div className="container dynamic-container inline-block">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">Trending Topics for {locationName} {locationName==="Global"&&SnippetManager.createInfo("https://confluence.supersocial.cloud/display/SP/Select+your+Location", "","","To select a different location, go to the settings.", "fontsize6")}</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={e => this.refreshTrendingTopics(UserConfigurationDecoder.decodeLocation(this.props.loginManager).woeid)}
                    >
                        {ImageProvider.getImage("refresh")}
                    </div>
                </div>
                <div className="box-content">
                    The most popular topics right now:
                    <ol>
                        {topics}
                    </ol>
                </div>
            </div >
        );
    }
}