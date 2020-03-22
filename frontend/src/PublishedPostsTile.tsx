import * as React from "react";
import './PublishedPostsTile.css';
import './UiTile.css';
import './UiElements.css';
import { EventBus, EventBusEventType } from "./EventBus";

export interface PublishedPost {
    id: number
    text: string
    platformId: number
    created: string
    creatorName: string
}

export interface PublishedPosts {
    posts: PublishedPost[]
    updating?: boolean
    eventBus: EventBus
    requireLogin?: boolean
}

export class PublishedPostsTile extends React.Component<PublishedPosts, PublishedPosts>{
    constructor(props: PublishedPosts, state: PublishedPosts) {
        super(props);
        this.state = props;

        this.refreshPosts();
        this.state.eventBus.register(EventBusEventType.REFRESH_POSTS, (eventType, eventData) => this.refreshPosts());
    }

    private refreshPosts() {
        this.setState({ posts: this.state.posts, updating: true });
        fetch('http://localhost:8080/api/post', {
            method: 'get',
            credentials: 'include',
        })
            .then(response => {
                if (response.status === 401) {
                    console.error("Could not fetch data: 401");
                }else{
                    response.json().then(data => this.setState({ posts: data, updating: false }));
                }
            });
    }

    private getSocialmediaIcon(platformId: number) {
        if (platformId === 1) {
            return <svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                width="1.5em" height="1.5em" viewBox="0 0 266.893 266.895">
                <path id="Blue_1_" fill="#3C5A99" d="M248.082,262.307c7.854,0,14.223-6.369,14.223-14.225V18.812
	c0-7.857-6.368-14.224-14.223-14.224H18.812c-7.857,0-14.224,6.367-14.224,14.224v229.27c0,7.855,6.366,14.225,14.224,14.225
	H248.082z"/>
                <path id="f" fill="#FFFFFF" d="M182.409,262.307v-99.803h33.499l5.016-38.895h-38.515V98.777c0-11.261,3.127-18.935,19.275-18.935
	l20.596-0.009V45.045c-3.562-0.474-15.788-1.533-30.012-1.533c-29.695,0-50.025,18.126-50.025,51.413v28.684h-33.585v38.895h33.585
	v99.803H182.409z"/>
            </svg>
        }
        else if (platformId === 2) {
            return <svg width="1.5em" height="1.5em" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                viewBox="0 0 210 279.9">
                <path fill="#157DC3" d="M165.5,268.2H94.3l-1.5-0.1c-48.4-4.4-80.8-40.8-80.5-90.3V41.8c0-17.7,14.3-32,32-32s32,14.3,32,32v47.2
                   l92.9,0.9c17.7,0.2,31.9,14.6,31.7,32.3c-0.2,17.6-14.5,31.7-32,31.7c-0.1,0-0.2,0-0.3,0L76.3,153v24.9
                   c-0.1,22.7,14.1,25.6,21,26.3h68.2c17.7,0,32,14.3,32,32S183.2,268.2,165.5,268.2z"/>
            </svg>
        }
        else {
            return <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
                width="1.5em" height="1.5em" viewBox="0 0 1280.000000 1280.000000">
                <g transform="translate(0.000000,1280.000000) scale(0.100000,-0.100000)"
                    fill="#d94f4f">
                    <path d="M1327 11473 l-1327 -1328 1872 -1872 1873 -1873 -1873 -1873 -1872
           -1872 1327 -1328 1328 -1327 1872 1872 1873 1873 1873 -1873 1872 -1872 1328
           1327 1327 1328 -1872 1872 -1873 1873 1873 1873 1872 1872 -1327 1328 -1328
           1327 -1872 -1872 -1873 -1873 -1873 1873 -1872 1872 -1328 -1327z"/>
                </g>
            </svg>;
        }
    }

    private selectPost(post: PublishedPost) {
        this.state.eventBus.fireEvent(EventBusEventType.SELECTED_POST_CHANGED, post);
    }

    public render() {
        const posts = this.state.posts.map((elem) => {
            return (
                <tr onClick={() => this.selectPost(elem)}>
                    <td>{elem.id}</td>
                    <td>{this.getSocialmediaIcon(elem.platformId)}</td>
                    <td>{elem.created}</td>
                    <td>{elem.creatorName}</td>
                    <td>{elem.text}</td>
                </tr>
            );
        });

        const updating = this.state.updating ? "btn-align-top-right btn-icon crossRotate" : "btn-align-top-right btn-icon";

        return (
            <div className="container double-container">
                <div className="box-header">
                    Published Posts
                    <div
                        className={updating}
                        onClick={this.refreshPosts.bind(this)}
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" version="1.1" width="1.5em" fill="#545c60">
                            <g><path d="M 16 4 C 10.886719 4 6.617188 7.160156 4.875 11.625 L 6.71875 12.375 C 8.175781 8.640625 11.710938 6 16 6 C 19.242188 6 22.132813 7.589844 23.9375 10 L 20 10 L 20 12 L 27 12 L 27 5 L 25 5 L 25 8.09375 C 22.808594 5.582031 19.570313 4 16 4 Z M 25.28125 19.625 C 23.824219 23.359375 20.289063 26 16 26 C 12.722656 26 9.84375 24.386719 8.03125 22 L 12 22 L 12 20 L 5 20 L 5 27 L 7 27 L 7 23.90625 C 9.1875 26.386719 12.394531 28 16 28 C 21.113281 28 25.382813 24.839844 27.125 20.375 Z " /></g>
                        </svg>
                    </div>
                </div>
                <div className="box-content">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Platforms</th>
                                <th>Created</th>
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