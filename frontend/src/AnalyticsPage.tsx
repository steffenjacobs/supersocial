import * as React from "react";
import { EventBus, EventBusEventType } from "./EventBus";
import { PostAnalyticsTile, AnalyticsType } from "./PostAnalyticsTile";
import { DeploymentManager } from "./DeploymentManager";
import { ToastManager } from "./ToastManager";
import './AnalyticsPage.css';
import { Multiselect } from 'multiselect-react-dropdown';

export interface AnalyticsProps {
    eventBus: EventBus
}

interface AccountDataSource {
    id: string
    platform: number
    name: string
}

interface PostDataSource {
    id: string
    platform: number
    account: string
    summary: string
}

interface AnalyticsState {
    accounts: AccountDataSource[]
    posts: PostDataSource[]
    selectedAccounts: AccountDataSource[]
    selectedPosts: PostDataSource[]
}

interface MultiSelectValue {
    label: string
    value: string
    type: string
}

/** Analytics page. Contains all sorts of analytics tiles with KPIs and graphs. */
export class AnalyticsPage extends React.Component<AnalyticsProps, AnalyticsState>{
    constructor(props: AnalyticsProps) {
        super(props);
        this.updateData(EventBusEventType.REFRESH_POST_ANALYTICS_REQ, "");
        this.updateData(EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ, "");
        props.eventBus.register(EventBusEventType.REFRESH_POST_ANALYTICS_REQ, (t, e) => this.updateData(t, e.relId));
        props.eventBus.register(EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ, (t, e) => this.updateData(t, e.relId));
        this.state = { accounts: [], posts: [], selectedAccounts: [], selectedPosts: [] };
    }

    public render() {
        let post = { relId: "", value: 0 }

        let accounts = this.state.accounts.map(a => ({ label: a.name, value: a.id, type: "Accounts" }));
        let posts = this.state.posts.map(p => ({ label: p.summary, value: p.id, type: "Posts" }));
        let selectedAccounts = this.state.selectedAccounts.map(a => ({ label: a.name, value: a.id, type: "Accounts" }));
        let selectedPosts = this.state.selectedPosts.map(p => ({ label: p.summary, value: p.id, type: "Posts" }));
        let options = accounts.concat(posts);
        let selectedOptions = selectedAccounts.concat(selectedPosts);

        return (
            <div>
                <div className="filter-area">
                    <Multiselect
                        placeholder="Select data sources..."
                        options={options}
                        selectedValues={selectedOptions}
                        onSelect={this.onSelectionChange.bind(this)}
                        onRemove={this.onSelectionChange.bind(this)}
                        displayValue="label"
                        closeIcon="cancel"
                        groupBy="type"
                    />
                </div>
                <PostAnalyticsTile analyticsNumber={post} label="# Impressions" eventBus={this.props.eventBus} keyVal="impressions" type={AnalyticsType.POST} background="binoculars" />
                <PostAnalyticsTile analyticsNumber={post} label="# Comments" eventBus={this.props.eventBus} keyVal="comments" type={AnalyticsType.POST} background="comment" />
                <PostAnalyticsTile analyticsNumber={post} label="# Likes" eventBus={this.props.eventBus} keyVal="likes" type={AnalyticsType.POST} background="like" />
                <PostAnalyticsTile analyticsNumber={post} label="# Shares" eventBus={this.props.eventBus} keyVal="shares" type={AnalyticsType.POST} background="share" />
                <PostAnalyticsTile analyticsNumber={post} label="# Followers" eventBus={this.props.eventBus} keyVal="acc_followers" type={AnalyticsType.ACCOUNT} background="follower" />
                <PostAnalyticsTile analyticsNumber={post} label="# Posts" eventBus={this.props.eventBus} keyVal="acc_posts" type={AnalyticsType.ACCOUNT} background="post" />
            </div>
        );
    }

    /***Called by the Multiselect UI element. Synchronizes the multiselect state to the component state. */
    private onSelectionChange(value: MultiSelectValue[]) {
        let accounts = value.filter(v => v.type === "Accounts").map(v => this.state.accounts.find(sa => v.value == sa.id));
        let posts = value.filter(v => v.type === "Posts").map(v => this.state.posts.find(sa => v.value == sa.id));
        this.setState({ selectedAccounts: accounts as AccountDataSource[], selectedPosts: posts as PostDataSource[] });
    }

    /** Called by the event handler when new data are to be fetched. */
    private updateData(eventType: EventBusEventType, relId: string) {
        let typedPath = eventType === EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ ? "account" : "post";
        fetch(DeploymentManager.getUrl() + "api/analytics/" + typedPath + "/" + relId + "?query=", {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (!response.ok) {
                    ToastManager.showErrorToast(response);
                } else {
                    response.json().then(data => {
                        if (eventType === EventBusEventType.REFRESH_ACCOUNT_ANALYTICS_REQ) {
                            this.props.eventBus.fireEvent(EventBusEventType.REFRESH_ACCOUNT_ANALYTICS, data);
                            this.setState({ accounts: data[0].entities, selectedAccounts: Object.assign([], data[0].entities) });
                        }
                        else {
                            this.props.eventBus.fireEvent(EventBusEventType.REFRESH_POST_ANALYTICS, data);
                            this.setState({ posts: data[0].entities, selectedPosts: Object.assign([], data[0].entities) });
                        };
                    });
                }
            });
    }
}