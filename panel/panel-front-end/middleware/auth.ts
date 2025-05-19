// noinspection JSUnusedLocalSymbols,JSUnusedGlobalSymbols

import type {NavigationFailure, NavigationGuardReturn, RouteLocationRaw} from "#vue-router";

export default defineNuxtRouteMiddleware(async (to, from) => {
    let returnNav: Promise<void | NavigationFailure | false> | false | void | RouteLocationRaw = undefined;

    await $fetch("http://localhost:8080/user/info", {
        credentials: 'include'
    }).then(() => {
    }).catch(() => {
        returnNav = navigateTo('/login');
    })

    return returnNav;
})