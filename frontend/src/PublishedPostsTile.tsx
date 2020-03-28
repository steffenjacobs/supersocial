import * as React from "react";
import Moment from 'react-moment';
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";
import { DeploymentManager } from "./DeploymentManager";

export interface PublishedPost {
    id: number
    text: string
    platformId: number
    created: Date
    creatorName: string
    errorMessage?: string
    postUrl?: string
}

export interface PublishedPosts {
    posts: PublishedPost[]
    updating?: boolean
    eventBus: EventBus
    requireLogin?: boolean
}

/** Lists the already published posts. */

//TODO: make table scrollable and pageable.
export class PublishedPostsTile extends React.Component<PublishedPosts, PublishedPosts>{
    constructor(props: PublishedPosts, state: PublishedPosts) {
        super(props);
        this.state = props;

        this.refreshPosts();
        this.state.eventBus.register(EventBusEventType.REFRESH_POSTS, (eventType, eventData) => this.refreshPosts());
    }

    /** Triggers a refresh of this list. This is also triggered when a REFRESH_POSTS event is received via the EventBus. */
    private refreshPosts() {
        this.setState({ posts: this.state.posts, updating: true });
        fetch(DeploymentManager.getUrl() + 'api/post', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (response.status === 401) {
                    console.error("Could not fetch data: 401");
                } else {
                    response.json().then(data => this.setState({
                        posts: data.map((d: PublishedPost) => {
                            d.created = new Date(d.created);
                            return d;
                        }), updating: false
                    }));
                }
            });
    }

    /** @returns a social media icon for a given platform identifier. */
    private getSocialmediaIcon(platformId: number) {
        if (platformId === 1) {
            return ImageProvider.getImage("facebook-logo");
        }
        else if (platformId === 2) {
            return ImageProvider.getImage("twitter-logo");
        }
        else {
            return ImageProvider.getImage("none-logo");
        }
    }

    /** Fire an event to change the selected post. Lets all listeners on the event bus (e.g. PublishMessageTile.tsx) know to update accordingly. */
    private selectPost(post: PublishedPost) {
        this.state.eventBus.fireEvent(EventBusEventType.SELECTED_POST_CHANGED, post);
    }

    private goToPost(post: PublishedPost){
        window.open(post.postUrl, "_blank")
    }

    public render() {
        const posts = this.state.posts.sort(
            (p1: PublishedPost, p2: PublishedPost) => p2.created.getTime() - p1.created.getTime())
            .map(elem => {
                var status;
                if(elem.errorMessage){
                    status = <td className="tooltip centered">{ImageProvider.getImage("none-logo")}
                    <span className="tooltiptext">{elem.errorMessage}</span></td>

                } else{
                    status = <td onClick={o=>this.goToPost(elem)} className="checkmark centered pointer">{ImageProvider.getImage("check")}{ImageProvider.getImage("link")}</td>
                }
                return (
                    <tr onClick={() => this.selectPost(elem)}>
                        {status}
                        <td>
                            <Moment format="YYYY-MM-DD">{elem.created}</Moment>
                        </td>
                        <td className="centered">{this.getSocialmediaIcon(elem.platformId)}</td>
                        <td>{elem.creatorName}</td>
                        <td>{elem.text}</td>
                    </tr>
                );
            });

        let classUpdating = ["inline-block", "btn-icon"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        return (
            <div className="container double-container inline">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">Published Posts</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={this.refreshPosts.bind(this)}
                    >
                        {ImageProvider.getImage("refresh")}
                    </div>
                </div>
                <div className="box-content">
                    <table>
                        <thead>
                            <tr>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Platform</th>
                                <th>Author</th>
                                <th className="column-text">Post</th>
                            </tr>
                        </thead>
                        <tbody>
                            {posts}
                        </tbody>

                    </table>
                </div>
            </div >
        );
    }
}