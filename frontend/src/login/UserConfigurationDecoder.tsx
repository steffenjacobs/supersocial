import { UserConfiguration, LoginManager, LoginStatus } from "./LoginManager";

export interface LocationConfiguration {
    country: string
    countryCode: string
    name: string
    parentId: number
    placeType: LocationConfigurationPlaceType
    url: string
    woeid: number
}

export interface LocationConfigurationPlaceType {
    code: number
    name: string
}

/** Select pre-defined objects from the user configuration. */
export class UserConfigurationDecoder {

    /** Select the location selected by the user from the loginManager. */
    static decodeLocation(loginManager: LoginManager): LocationConfiguration {
        return UserConfigurationDecoder.decodeLocationFromLoginStatus(loginManager.getLoginStatus());
    }
    /** Select the location selected by the user from a user configuration element. It is assumed that this has the descriptor 'user.location'. */
    static decodeLocationFromConfig(locationConfig?: UserConfiguration): LocationConfiguration {
        if (!locationConfig) {
            return { country: "Global", countryCode: "", name: "Global", parentId: -1, placeType: { code: -1, name: "Anywhere" }, url: "", woeid: 1 };
        }
        let cleanedValue = locationConfig.value.replace("\\", "");
        return JSON.parse(cleanedValue)[0];
    }
    /** Select the location selected by the user from the login status. */
    static decodeLocationFromLoginStatus(loginStatus: LoginStatus): LocationConfiguration {
        return UserConfigurationDecoder.decodeLocationFromConfig(loginStatus.config.find(x => x.descriptor === "user.location"));
    }
}