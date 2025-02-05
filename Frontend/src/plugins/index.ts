/**
 * plugins/index.ts
 *
 * Automatically included in `./src/main.ts`
 */

import i18n from './i18n'
// Plugins
import vuetify from './vuetify'
import pinia from '../stores'
import router from '../router'

// Types
import type { App } from 'vue'

export function registerPlugins (app: App) {
  app
    .use(vuetify)
    .use(router)
    .use(pinia)
    .use(i18n)
  app.config.globalProperties.$t = i18n.global.t
}
