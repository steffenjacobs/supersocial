import React from "react";
import { ImageProvider } from "./ImageProvider";
import { SocialMediaAccount } from "./SocialMediaAccountsListTile";

export interface Param {
    name: string
    value: string
}

/**Contains a bunch of useful snippets to be standardized across the application. */
export class SnippetManager {

    /**Create an info message with a documentation link and a tooltip. */
    static createInfo(url: string, text: string, cssClasses?: string, tooltip?: string, cssClassesToolTipText?: string) {
        const className = "tooltip " + cssClasses;
        const classNameText = "tooltiptext " + cssClassesToolTipText;
        return <span className={className}>{text}
            {text && <span><a target="blank" href={url}>here</a>.&nbsp;</span>}
            <a target="blank" href={url}>{ImageProvider.getImage("info-text")}</a>
            <span className={classNameText}>{tooltip ? tooltip : text}</span>
        </span>
    }

    /**Create an info message with a documentation link without a tooltip. */
    static createInfoWithoutTooltip(url: string, text: string, cssClasses?: string) {
        const className = "" + cssClasses;
        return <span className={className}>{text}
            {text && <span><a target="blank" href={url}>here</a>.&nbsp;</span>}
            <a target="blank" href={url}>{ImageProvider.getImage("info-text")}</a>
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

    static findUrlParam(search: string, paramName: string): string | undefined {
        return SnippetManager.parseUrlParams(search).find(x => x.name === paramName)?.value;
    }

    static parseUrlParams(search: string): Param[] {
        if (search.length == 0) {
            return [];
        }
        search = search.substr(1);
        let params = search.split("&");
        return params.map(p => {
            if (!p.includes("=")) {
                return { name: decodeURIComponent(p), value: "" };
            }
            let split = p.split("=");
            return { name: decodeURIComponent(split[0]), value: decodeURIComponent(split[1]) }
        })
    }
}