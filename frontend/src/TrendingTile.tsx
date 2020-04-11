import * as React from "react";
import './UiTile.css';
import './UiElements.css';
import { EventBus } from "./EventBus";
import { DeploymentManager } from "./DeploymentManager";
import { ImageProvider } from "./ImageProvider";
import { ToastManager } from "./ToastManager";

export interface TrendingTileState {
    updating?: boolean
    topics: TrendingTopic[]
}
export interface TrendingTileProps {
    eventBus: EventBus
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
        this.refreshTrendingTopics();
    }

    /** Fetches the most recent twitter trends from the backend. */
    private refreshTrendingTopics() {
        this.setState({ updating: true });
        fetch(DeploymentManager.getUrl() + 'api/analytics/trending', {
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
        let classUpdating = ["inline-block", "btn-icon", "btn-small"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        return (
            <div className="container dynamic-container inline-block">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">Trending Topics</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={e => this.refreshTrendingTopics()}
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