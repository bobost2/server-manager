// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  modules: ['vuetify-nuxt-module'],
  css: ['@/assets/styles/global.css'],
  ssr: false,
  vuetify: {
    vuetifyOptions: {
      theme: {
        defaultTheme: 'dark'
      },
      defaults: {
      }
    }
  },
  app: {
    head: {
      title: 'Server Manager',
      htmlAttrs: {
        lang: 'en',
      },
    }
  }
})