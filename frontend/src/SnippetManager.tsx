import React from "react";
import { ImageProvider } from "./ImageProvider";
import { SocialMediaAccount } from "./SocialMediaAccountsListTile";

/**Contains a bunch of useful snippets to be standardized across the application. */
export class SnippetManager {

    /**Create an info message with a documentation link. */
    static createInfo(url: string, text: string, cssClasses?: string, tooltip?: string, cssClassesToolTipText?:string) {
        const className = "tooltip " + cssClasses;
        const classNameText = "tooltiptext " + cssClassesToolTipText;
        return <span className={className}>{text}
           {text && <span><a target="blank" href={url}>here</a>.&nbsp;</span>}
        <a target="blank" href={url}>{ImageProvider.getImage("info-text")}</a>
        <span className={classNameText}>{tooltip?tooltip:text}</span>
        </span>
    }

    /** Redirect the browser directly to the external social media account page on the associated social network in a new tab. */
    static goToAccount(account: SocialMediaAccount) {
        let url;
        if (account.platformId === 1) {
            url = "https://www.facebook.com/" + account.credentials.find(x => x.descriptor === "facebook.page.id")?.value;
        } else if (account.platformId === 2) {
            url = "https://twitter.com/" + account.credentials.find(x => x.descriptor === "twitter.api.accountname")?.value;
        }
        window.open(url, "_blank");
    }

    /** @return true if the given social media account is linked to an external social network */
    static isLinked(account: SocialMediaAccount) {
        if (account.platformId === 1) {
            return account.credentials.find(x => x.descriptor === "facebook.page.id")
        }
        if (account.platformId === 2) {
            return account.credentials.find(x => x.descriptor === "twitter.api.accountname");
        }
        return false;
    }
}