import React from "react";
import { ImageProvider } from "./ImageProvider";

/**Contains a bunch of useful snippets to be standardized across the application. */
export class SnippetManager {

    /**Create an info message with a documentation link. */
    static createInfo(url: string, text: string, cssClasses?: string) {
        const className = "info-label " + cssClasses;
        return <span className={className}>{text}
            <a target="blank" href={url}>here</a>.&nbsp;
        <a target="blank" href={url}>{ImageProvider.getImage("info-text")}</a>
        </span>
    }
}