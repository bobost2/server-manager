export { RegisterUser };

declare global {
    interface RegisterUser {
        username: string;
        initPassword: string;
        passwordExpired?: boolean;
        admin?: boolean;
        roleId?: number;
    }
}