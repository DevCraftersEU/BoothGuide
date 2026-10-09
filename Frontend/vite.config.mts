// Plugins
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { VitePWA } from 'vite-plugin-pwa'
import Vue from '@vitejs/plugin-vue'
import VueRouter from 'unplugin-vue-router/vite'
import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'

// Utilities
import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/

export default defineConfig(({}) => {
  return {
    css: {
      preprocessorOptions: {
        scss: {
          api: 'modern',
        },
        sass: {
          api: 'modern',
        },
      }
    },
    plugins: [
      VitePWA({
        strategies: 'generateSW',
        workbox: {
          globPatterns: ['**/*.{js,css,html,woff}'],
        },
        registerType: 'autoUpdate',
        manifest: {
          name: '$PWA_TITLE',
          short_name: '$PWA_TITLE_SHORT',
          description: 'App zur Auflistung von Ausstellern auf Messen',
          lang: 'de',
          icons: [
            {
              src: '/img/icons/android-chrome-192x192.png',
              sizes: '192x192',
              type: 'image/png',
            }, {
              src: '/img/icons/android-chrome-512x512.png',
              sizes: '512x512',
              type: 'image/png',
            }, {
              src: '/img/icons/android-chrome-512x512.png',
              sizes: '512x512',
              type: 'image/png',
              purpose: 'any',
            },
          ],
        },
      }),
      VueRouter({
          routesFolder: 'src/pages',
        //dts: 'src/typed-router.d.ts',
      }),
      AutoImport({
        imports: [
          'vue',
          {
            'vue-router/auto': ['useRoute', 'useRouter'],
          },
        ],
        dts: 'src/auto-imports.d.ts',
        eslintrc: {
          enabled: true,
        },
        vueTemplate: true,
      }),
      Components({
        dts: 'src/components.d.ts',
      }),
      Vue({
        template: { transformAssetUrls },
      }),
      // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
      Vuetify({
        autoImport: true,
        styles: {
          configFile: 'src/styles/settings.scss',
        },
      }),
    ],
    define: { 'process.env': {} },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
      extensions: [
        '.js',
        '.json',
        '.jsx',
        '.mjs',
        '.ts',
        '.tsx',
        '.vue',
      ],
    },
    server: {
      port: 3000,
      proxy: {
        '/pub': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/auth': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/config': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  }
})
