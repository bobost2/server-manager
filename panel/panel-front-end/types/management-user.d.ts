export { ManagementUserInfo };

declare global {
    interface ManagementUserInfo {
        id: number;
        username: string;
        passwordExpired: boolean;
        has2fa: boolean;
        roleId?: number;
        roleName?: string;
        currentUser: boolean;
        admin: boolean;
    }
}