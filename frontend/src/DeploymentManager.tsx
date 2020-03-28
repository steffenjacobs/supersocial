export class DeploymentManager {
    static local = true;
    static local8080 = true;
    static getUrl(): String {
        return this.local ? (this.local8080?"http://localhost:8080/":"http://localhost:9001/") : "https://api.supersocial.cloud/";
    }
}