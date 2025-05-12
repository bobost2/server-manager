// noinspection JSUnusedLocalSymbols,JSUnusedGlobalSymbols

export default defineNuxtRouteMiddleware((to, from) => {

    // Not authenticated!
    // Placeholder code, change in future!
    if ( useCookie("auth").value == null || useCookie("auth").value == "" ) {
        return navigateTo('/login');
    }
})