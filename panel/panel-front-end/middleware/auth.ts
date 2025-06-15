// noinspection JSUnusedLocalSymbols,JSUnusedGlobalSymbols

import type {NavigationFailure, RouteLocationRaw} from "#vue-router";

export default defineNuxtRouteMiddleware(async () => {
    let returnNav: Promise<void | NavigationFailure | false> | false | void | RouteLocationRaw = undefined;

    await $fetch("http://localhost:8080/user/info", {
        credentials: 'include'
    }).then(() => {
    }).catch(() => {
        returnNav = navigateTo('/login');
    })

    return returnNav;
})