import * as React from "react";
import Moment from 'react-moment';
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";
import { ImageProvider } from "./ImageProvider";
import { DeploymentManager } from "./DeploymentManager";
import moment from "moment";
import { ToastManager } from "./ToastManager";

export interface PublishedPostsProps {
    eventBus: EventBus
}

interface PublishedPost {
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

interface PublishedPostsState {
    posts: PublishedPost[]
    updating?: boolean
    requireLogin?: boolean
}

/** Lists the already published posts. */

//TODO: make table scrollable and pageable.
export class PublishedPostsTile extends React.Component<PublishedPostsProps, PublishedPostsState>{
    constructor(props: PublishedPostsProps) {
        super(props);
        this.state = { posts: [] };

        this.refreshPosts(true);
        this.props.eventBus.register(EventBusEventType.REFRESH_POSTS, (eventType, eventData) => this.refreshPosts());
    }

    /** Triggers a refresh of this list. This is also triggered when a REFRESH_POSTS event is received via the EventBus. */
    private refreshPosts(notMounted?: boolean) {
        if (notMounted) {
            this.state = { posts: this.state.posts, requireLogin: this.state.requireLogin, updating: true };
        }
        else {
            this.setState({ posts: this.state.posts, updating: true });
        }
        fetch(DeploymentManager.getUrl() + 'api/post', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
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

    /** Create a table cell with the platform icon. */
    private createPlatformElement(platformId: number) {
        if (platformId === 0) {
            return <td className="centered tooltip x-gray icon-medium">{ImageProvider.getImage("none")}<span className="tooltiptext">No Platform selected yet.</span></td>;
        }
        else {
            return <td className="centered icon-medium">{ImageProvider.getSocialmediaIcon(platformId)}</td>;
        }
    }

    /** Fire an event to change the selected post. Lets all listeners on the event bus (e.g. PublishMessageTile.tsx) know to update accordingly. */
    private selectPost(post: PublishedPost) {
        this.props.eventBus.fireEvent(EventBusEventType.SELECTED_POST_CHANGED, post);
    }

    /** Delete the post from the system without unpublishing it. */
    private deletePostLink(post: PublishedPost) {
        fetch(DeploymentManager.getUrl() + 'api/post/' + post.id, {
            method: 'delete',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    ToastManager.showSuccessToast("Deleted post from this system.");
                    this.refreshPosts();
                }
            });
    }

    /** Remove scheduling information for a scheduled, not yet posted post. */
    private unschedulePost(post: PublishedPost) {
        fetch(DeploymentManager.getUrl() + 'api/schedule/post/' + post.id, {
            method: 'delete',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    ToastManager.showSuccessToast("Unscheduled post.");
                    this.refreshPosts();
                }
            });
    }

    /** Open the post on the native platform in a new tab. */
    private goToPost(post: PublishedPost) {
        window.open(post.postUrl, "_blank")
    }

    /** Publish an unpublished and/or scheduled post right away. */
    private publishPost(post: PublishedPost) {
        fetch(DeploymentManager.getUrl() + 'api/publishnow/' + post.id, {
            method: 'POST',
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                    this.props.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                } else {
                    ToastManager.showSuccessToast("Published post.");
                    response.json().then(data => {
                        this.props.eventBus.fireEvent(EventBusEventType.REFRESH_POSTS);
                    });
                }
            });
    }

    public render() {
        //rows with posts
        const posts = this.state.posts.sort(
            (p1: PublishedPost, p2: PublishedPost) => p2.created.getTime() - p1.created.getTime())
            .map(elem => {
                //status: first column with checkmark or error with error description.
                var status;
                if (elem.errorMessage) {
                    status = <td key={elem.id} className="tooltip centered icon-medium">{ImageProvider.getImage("none-logo")}
                        <span className="tooltiptext">{elem.errorMessage}</span></td>

                } else {
                    if (elem.postUrl) {
                        status = <td key={elem.id} onClick={o => this.goToPost(elem)} className="checkmark centered pointer">{ImageProvider.getImage("check")}{ImageProvider.getImage("link")}</td>
                    } else {
                        const statusMsg = elem.scheduled ? "This post is scheduled for " + moment(elem.scheduled).format("YYYY-MM-DD HH:mm") : "This post is neither published nor scheduled yet.";
                        const statusIcons = elem.scheduled ? [ImageProvider.getImage("check"), ImageProvider.getImage("clock-small")] : ImageProvider.getImage("check");
                        status = <td key={elem.id} className="checkmark checkmark-gray centered tooltip">{statusIcons}<span className="tooltiptext">{statusMsg}</span></td>
                    }
                }

                //actions for the current post
                const allowedActions = (
                    <div className="inline-block">
                        <span className="table-icon">
                            <span onClick={() => this.selectPost(elem)}>{ImageProvider.getImage("edit")}
                            </span>
                        </span>
                        <span className="table-icon table-icon-del">
                            <span className="tooltip" onClick={() => this.deletePostLink(elem)} >{ImageProvider.getImage("none")}{ImageProvider.getImage("link")}
                                <span className="tooltiptext">Delete the post from the system without unpublishing it.</span>
                            </span>
                        </span>
                        {elem.scheduled &&
                            <span className="table-icon table-icon-del">
                                <span className="tooltip" onClick={() => this.unschedulePost(elem)}>{ImageProvider.getImage("clock")}
                                    <span className="tooltiptext">Unschedule the post. It will remain in the system but not automatically posted.</span>
                                </span>
                            </span>}
                        {!elem.published &&
                            <span className="table-icon table-icon-green">
                                <span className="tooltip" onClick={() => this.publishPost(elem)}>{ImageProvider.getImage("play")}
                                    <span className="tooltiptext">Publish this post right away.</span>
                                </span>
                            </span>}
                    </div>);

                //Publication date if present
                const published = elem.published ? <Moment format="YYYY-MM-DD">{elem.published}</Moment> : "";
                return (
                    <tr key={elem.id}>
                        {status}
                        <td>
                            {published}
                        </td>
                        {this.createPlatformElement(elem.platformId)}
                        <td>{elem.creatorName}</td>
                        <td>{elem.text}</td>
                        <td>{allowedActions}</td>
                    </tr>
                );
            });

        //animation for refresh button
        let classUpdating = ["inline-block", "btn-icon", "btn-small"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        return (
            <div className="container dynamic-container inline-block">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">All Posts</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={e => this.refreshPosts()}
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
                                <th className="table-colum-actions">Actions</th>
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