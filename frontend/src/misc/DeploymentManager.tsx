/**Contains settings for deployment. */
export class DeploymentManager {
    //set this to 'true' if the user connects via a localhost connection (local development instance inside docker-compose)
    static local = true;
    //set this to 'true' if the user connects iva port 8080 on a localhost connection (Start backend from IDE)
    static local8080 = true;
    static getUrl(): string {
        return this.local ? (this.local8080?"http://localhost:8080/":"http://localhost:9001/") : "https://api.supersocial.cloud/";
    }
}