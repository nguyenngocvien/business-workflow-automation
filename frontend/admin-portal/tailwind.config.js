/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f1f5ff',
          100: '#e3ebff',
          500: '#3366ff',
          600: '#1d4ed8',
          700: '#163caa',
        },
        slate: {
          950: '#0f172a',
        },
      },
      boxShadow: {
        panel: '0 20px 45px -25px rgba(15, 23, 42, 0.28)',
      },
      backgroundImage: {
        grid:
          'radial-gradient(circle at 1px 1px, rgba(148, 163, 184, 0.16) 1px, transparent 0)',
      },
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
