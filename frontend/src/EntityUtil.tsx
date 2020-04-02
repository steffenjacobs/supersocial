export class EntityUtil {
    static prefix = 'fe_id_';

    /** @return a random string to be used as an identifier in the UI. This will not be the identifier used in the back end. */
    static makeId(): string {
        let outString: string = EntityUtil.prefix;
        let inOptions: string = 'abcdefghijklmnopqrstuvwxyz0123456789';

        for (let i = 0; i < 32; i++) {
            outString += inOptions.charAt(Math.floor(Math.random() * inOptions.length));
        }
        return outString;
    }

    static isGeneratedId(id: string): boolean {
        return id.startsWith(EntityUtil.prefix);
    }
}