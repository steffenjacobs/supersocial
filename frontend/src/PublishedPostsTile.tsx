import * as React from "react";
import Moment from 'react-moment';
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";
import { DeploymentManager } from "./DeploymentManager";
import moment from "moment";

export interface PublishedPost {
    id: string
    text: string
    platformId: number
    created: Date
    creatorName: string
    errorMessage?: string
    postUrl?: string
    published: Date
    scheduled?: Date
    error: string
    accountId: string
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
        this.state = {posts: props.posts, updating: props.updating, eventBus: props.eventBus, requireLogin: props.requireLogin};

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
                    console.log(this.state);
                }
            });
    }

    private createPlatformElement(platformId: number){
        if(platformId === 0){
            return <td className="centered tooltip x-gray">{ImageProvider.getImage("none")}<span className="tooltiptext">No Platform selected yet.</span></td>;
        }
        else{
            return <td className="centered icon-medium">{ImageProvider.getSocialmediaIcon(platformId)}</td>;
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
                    if(elem.postUrl){
                        status = <td onClick={o=>this.goToPost(elem)} className="checkmark centered pointer">{ImageProvider.getImage("check")}{ImageProvider.getImage("link")}</td>
                    }else {
                        const statusMsg = elem.scheduled?"This post is scheduled for " + moment(elem.scheduled).format("YYYY-MM-DD HH:mm") :"This post is not posted and not scheduled yet.";
                        const statusIcons = elem.scheduled?[ImageProvider.getImage("check"),ImageProvider.getImage("clock")]:ImageProvider.getImage("check");
                        status = <td className="checkmark checkmark-gray centered tooltip">{statusIcons}<span className="tooltiptext">{statusMsg}</span></td>
                    }
                }

                const published = elem.published?<Moment format="YYYY-MM-DD">{elem.published}</Moment>:"";
                return (
                    <tr key={elem.id} onClick={() => this.selectPost(elem)}>
                        {status}
                        <td>
                            {published}
                        </td>
                        {this.createPlatformElement(elem.platformId)}
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
            <div className="container double-container inline-block">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">All Posts</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={this.refreshPosts.bind(this)}
                    >
                        {ImageProvider.getImage("refresh")}
                    </div>
                </div>
                <div className="box-content">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Status</th>
                                <th>Published</th>
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