/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts,scss}"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        // Primary colors
        primaryTail: "#03256C",
        secondaryTail: "#2541B2",
        accentTail: "#1768AC",
        infoTail: "#06BEE1",

        // Light mode
        bgLight: "#FFFFFF",
        surfaceLight: "#F9FAFB",
        textLight: "#111827",

        // Dark mode
        bgDark: "#0F172A", // deep navy/blueish
        surfaceDark: "#1E293B", // lighter panel
        textDark: "#E0E0E0", // soft gray
        primaryDarkTail: "#3B6EDB",
        secondaryDarkTail: "#5A7FCF",
        accentDarkTail: "#4CA3E0",
        infoDarkTail: "#33C1F0",

        // Status colors
        successTail: "#16A34A",
        warningTail: "#F59E0B",
        dangerTail: "#DC2626",
      },
      fontFamily: {
        sans: ["Inter", "sans-serif"],
        heading: ["Poppins", "sans-serif"],
      },
      fontSize: {
        base: ["1rem", { lineHeight: "1.5rem" }],
        lg: ["1.125rem", { lineHeight: "1.75rem" }],
        xl: ["1.25rem", { lineHeight: "1.75rem" }],
        "2xl": ["1.5rem", { lineHeight: "2rem" }],
        "3xl": ["1.875rem", { lineHeight: "2.25rem" }],
        "4xl": ["2.25rem", { lineHeight: "2.5rem" }],
        "5xl": ["3rem", { lineHeight: "1" }],
      },
      fontWeight: {
        normal: "400",
        medium: "500",
        semibold: "600",
        bold: "700",
      },
      lineHeight: {
        normal: "1.5",
        relaxed: "1.625",
        loose: "2",
      },
      letterSpacing: {
        tight: "-0.025em",
        normal: "0",
        wide: "0.025em",
      },
    },
  },
  plugins: [],
};
