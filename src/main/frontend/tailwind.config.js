/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    extend: {},
  },
  plugins: [],
  safelist: [
      'bg-yellow-200', 'text-yellow-800', 'font-semibold', 'text-center', 'border-b',
      'bg-blue-200', 'text-blue-800',
      'bg-red-200', 'text-red-800',
      'bg-gray-200', 'text-gray-800',
  ],
}