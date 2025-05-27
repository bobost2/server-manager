export { UserInfo };

declare global {
    interface UserInfo {
        username?: string;
        permissions?: string[];
    }
}