import { createI18n } from 'vue-i18n'
import de from '@/locales/de.json'
import en from '@/locales/en.json'

const messages = { de, en }

export default createI18n({
  legacy: false,
  locale: 'en',
  fallbackLocale: 'en',
  messages
})
