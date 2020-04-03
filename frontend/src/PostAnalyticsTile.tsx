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

export interface PostAnalyticsNumber {
    postId: string
    viewCount: number
}

export interface PostAnalyticsNumberProps {
    post: PostAnalyticsNumber
    updating?: boolean
    eventBus: EventBus
    endpointUrl: string
    label: string
}

/** Lists the already published posts. */

//TODO: make table scrollable and pageable.
export class PostAnalyticsTile extends React.Component<PostAnalyticsNumberProps, PostAnalyticsNumberProps>{
    constructor(props: PostAnalyticsNumberProps) {
        super(props);
        this.state = { post: props.post, updating: props.updating, eventBus: props.eventBus, endpointUrl: props.endpointUrl, label: props.label };

        this.refreshPostViews(true);
    }

    /** Triggers a refresh of this list. This is also triggered when a REFRESH_POSTS event is received via the EventBus. */
    private refreshPostViews(notMounted?: boolean) {
        if (notMounted) {
            this.state = { post: this.state.post, eventBus: this.state.eventBus, endpointUrl: this.state.endpointUrl, label: this.state.label, updating: true };
        }
        else {
            this.setState({ post: this.state.post, updating: true });
        }
        fetch(DeploymentManager.getUrl() + this.state.endpointUrl, {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => this.setState({
                        post: data
                    }));
                }
            });
    }

    public render() {
        let classUpdating = ["inline-block", "btn-icon", "btn-small"]
        if (this.state.updating) {
            classUpdating.push("crossRotate");
        }

        return (
            <div className=" container container-two-thirds inline-block container-margins">
                <div className="box-header box-header-with-icon">
                    <div className="inline-block">{this.state.label}</div>
                    <div
                        className={classUpdating.join(" ")}
                        onClick={e => this.refreshPostViews()}
                    >
                        {ImageProvider.getImage("refresh")}
                    </div>
                </div>
                <div className="box-content">
                    <div>{this.state.post.viewCount}</div>
                </div>
            </div >
        );
    }
}