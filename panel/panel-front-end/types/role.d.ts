export { UserRole };

declare global {
    interface UserRole {
        id?: number;
        name: string;
        permissions: Map<string, boolean>;
    }
}