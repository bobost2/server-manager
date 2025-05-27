export { SidebarButton };

declare global {
    interface SidebarButton {
        id: string;
        label: string;
        link?: string;
        icon?: string;
    }
}