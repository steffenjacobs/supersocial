export class DeploymentManager {
    static local = true;
    static getUrl(): String {
        return this.local ? "http://localhost:9001/" : "https://api.supersocial.cloud/";
    }
}