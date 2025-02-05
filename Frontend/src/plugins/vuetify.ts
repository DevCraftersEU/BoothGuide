/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import 'vuetify/styles'

// Composables
import { createVuetify } from 'vuetify'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  theme: {
    themes: {
      default: {
        dark: false,
        colors: {
          background: '#f7f9ff',
          surface: '#537291',
          primary: '#537291',
          secondary: '#bdbdbd',
          accent: '#4caf50',
          error: '#FF5449',
          warning: '#FB8C00',
          info: '#2196F3',
          success: '#4CAF50',
        },
        variables: {
          'border-color': '#000000', // Standard-Randfarbe
          'border-opacity': 0.12, // Rand-Deckkraft
          'high-emphasis-opacity': 0.87, // Hohe Hervorhebungsdeckkraft
          'medium-emphasis-opacity': 0.80, // Mittlere Hervorhebungsdeckkraft
          'disabled-opacity': 0.38, // Deaktivierte Deckkraft
          'idle-opacity': 0.04, // Leerlauf-Deckkraft
          'hover-opacity': 0.04, // Hover-Deckkraft
          'focus-opacity': 0.12, // Fokus-Deckkraft
          'selected-opacity': 0.08, // Ausgewählte Deckkraft
          'activated-opacity': 0.22, // Aktivierte Deckkraft
          'pressed-opacity': 0.12, // Gedrückte Deckkraft
          'dragged-opacity': 0.08, // Gezogene Deckkraft
          'theme-kbd': '#212529', // Tastaturhintergrundfarbe
          'theme-on-kbd': '#FFFFFF', // Tastaturtextfarbe
          'theme-code': '#F5F5F5', // Code-Hintergrundfarbe
          'theme-on-code': '#000000', // Code-Textfarbe
        },
      },
      preview: {
        dark: false,
        colors: {
          background: '#f7f9ff',
          surface: '#537291',
          primary: '#537291',
          secondary: '#bdbdbd',
          accent: '#4caf50',
          error: '#FF5449',
          warning: '#FB8C00',
          info: '#2196F3',
          success: '#4CAF50',
        },
      },
    },
    defaultTheme: 'default',
  },
})
